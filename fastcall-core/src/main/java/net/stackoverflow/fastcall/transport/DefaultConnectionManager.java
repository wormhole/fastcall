package net.stackoverflow.fastcall.transport;

import net.stackoverflow.fastcall.serialize.SerializeManager;
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

    private final Map<String, NettyClient> pool;

    private final ExecutorService executorService;

    private final SerializeManager serializeManager;

    private final Integer timeout;

    /**
     * 构造方法
     *
     * @param serializeManager 序列化管理器
     * @param timeout          客户端心跳检测超时时间
     */
    public DefaultConnectionManager(SerializeManager serializeManager, Integer timeout) {
        this.executorService = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));
        this.pool = new ConcurrentHashMap<>();
        this.serializeManager = serializeManager;
        this.timeout = timeout;
    }

    @Override
    public synchronized NettyClient getConnection(InetSocketAddress address) {
        String host = address.getAddress().getHostAddress();
        Integer port = address.getPort();
        String key = host + ":" + port;
        NettyClient client = pool.get(key);
        if (client == null) {
            log.info("[R:{}] ConnectionManager not found client", host + ":" + port);
            client = new NettyClient(serializeManager, host, port, timeout);
            pool.put(key, client);
            initClient(client);
        } else {
            if (!client.isActive()) {
                log.info("[R:{}] ConnectionManager detect client inactive", client.getHost() + ":" + client.getPort());
                initClient(client);
            }
        }
        return client;
    }

    private void initClient(NettyClient client) {
        log.info("[R:{}] ConnectionManager init client start", client.getHost() + ":" + client.getPort());
        CountDownLatch countDownLatch = new CountDownLatch(1);
        executorService.execute(() -> client.connect(countDownLatch));
        try {
            countDownLatch.await();
            log.info("[R:{}] ConnectionManager init client success", client.getHost() + ":" + client.getPort());
        } catch (InterruptedException e) {
            log.info("[R:{}] ConnectionManager init client fail", client.getHost() + ":" + client.getPort(), e);
        }
    }
}
