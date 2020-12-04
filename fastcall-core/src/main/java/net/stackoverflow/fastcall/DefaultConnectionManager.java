package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.NettyClient;
import net.stackoverflow.fastcall.transport.handler.client.ClientRpcHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 默认连接管理器实现
 *
 * @author wormhole
 */
public class DefaultConnectionManager implements ConnectionManager {

    private static final Logger log = LoggerFactory.getLogger(DefaultConnectionManager.class);

    private final Map<String, NettyClient> map;

    private final ExecutorService executorService;

    private final SerializeManager serializeManager;

    private final ClientRpcHandler clientRpcHandler;

    private final Integer timeout;

    /**
     * 构造方法
     *
     * @param serializeManager 序列化管理器
     * @param clientRpcHandler 客户端rpc handler
     * @param timeout          客户端连接超时时间
     */
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
            client = new NettyClient(timeout, serializeManager, clientRpcHandler, address);
            map.put(key, client);
            initClient(client);
        } else {
            if (!client.isActive()) {
                log.debug("client inactive ip:{}, port:{}", address.getAddress().getHostAddress(), address.getPort());
                initClient(client);
            }
        }
        return client;
    }

    private void initClient(NettyClient client) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        executorService.execute(() -> client.connect(countDownLatch));
        try {
            countDownLatch.await();
            log.debug("init client ip:{}, port:{}", client.getHost(), client.getPort());
        } catch (InterruptedException e) {
            log.error("fail to init client ip:{}, port:{}", client.getHost(), client.getPort(), e);
        }
    }
}
