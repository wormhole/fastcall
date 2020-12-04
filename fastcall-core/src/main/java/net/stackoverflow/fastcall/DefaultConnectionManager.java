package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.NettyClient;
import net.stackoverflow.fastcall.transport.handler.client.ClientRpcHandler;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;

public class DefaultConnectionManager implements ConnectionManager {

    private Map<String, NettyClient> map;

    private ExecutorService executorService;

    private SerializeManager serializeManager;

    private ClientRpcHandler clientRpcHandler;

    private Integer timeout;

    public DefaultConnectionManager(SerializeManager serializeManager, ClientRpcHandler clientRpcHandler, Integer timeout) {
        this.executorService = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));
        this.map = new ConcurrentHashMap<>();
        this.serializeManager = serializeManager;
        this.clientRpcHandler = clientRpcHandler;
        this.timeout = timeout;
    }

    @Override
    public synchronized NettyClient getClient(InetSocketAddress address) {
        String host = address.getAddress().getHostAddress();
        Integer port = address.getPort();
        String key = host + ":" + port;
        NettyClient client = map.get(key);
        if (client == null) {
            client = new NettyClient(timeout, serializeManager, clientRpcHandler);
            map.put(key, client);
            initClient(client, new InetSocketAddress(host, port));
        } else {
            if (!client.isActive()) {
                initClient(client, new InetSocketAddress(host, port));
            }
        }
        return client;
    }

    private void initClient(NettyClient client, InetSocketAddress socketAddress) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        executorService.execute(() -> client.connect(socketAddress, countDownLatch));
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
