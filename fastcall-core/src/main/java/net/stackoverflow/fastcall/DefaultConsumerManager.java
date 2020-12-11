package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.config.ConsumerConfig;
import net.stackoverflow.fastcall.context.ResponseFuture;
import net.stackoverflow.fastcall.context.ResponseFutureContext;
import net.stackoverflow.fastcall.exception.ConnectionInactiveException;
import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.NettyClient;
import net.stackoverflow.fastcall.transport.proto.Message;
import net.stackoverflow.fastcall.transport.proto.MessageType;
import net.stackoverflow.fastcall.transport.proto.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;

/**
 * ConsumerManager默认实现
 *
 * @author wormhole
 */
public class DefaultConsumerManager implements ConsumerManager {

    private static final Logger log = LoggerFactory.getLogger(DefaultConsumerManager.class);

    private final SerializeManager serializeManager;

    private final RegistryManager registryManager;

    private final Map<String, NettyClient> clientPool;

    private final ExecutorService executorService;

    private final ConsumerConfig config;

    private final ResponseFutureContext responseFutureContext;

    public DefaultConsumerManager(ConsumerConfig config, SerializeManager serializeManager, RegistryManager registryManager) {
        this.serializeManager = serializeManager;
        this.registryManager = registryManager;
        this.config = config;
        this.clientPool = new HashMap<>();
        this.responseFutureContext = new ResponseFutureContext();
        this.executorService = Executors.newFixedThreadPool(config.getThreads());
        this.subscribe();
    }

    @Override
    public ConsumerConfig getConfig() {
        return config;
    }

    @Override
    public SerializeManager getSerializeManager() {
        return this.serializeManager;
    }

    @Override
    public ResponseFuture call(Method method, Object[] args, String group) {
        RpcRequest request = new RpcRequest();
        request.setId(UUID.randomUUID().toString());
        request.setInterfaceType(method.getDeclaringClass());
        request.setMethod(method.getName());
        request.setGroup(group);
        request.setParams(args == null ? null : Arrays.asList(args));
        request.setParamsType(Arrays.asList(method.getParameterTypes()));

        List<InetSocketAddress> addresses = registryManager.getServiceAddress(request.getInterfaceType(), request.getGroup());
        ResponseFuture future = null;
        for (InetSocketAddress address : addresses) {
            NettyClient client = this.getClient(address);
            try {
                future = responseFutureContext.createFuture(request.getId());
                client.send(new Message(MessageType.BUSINESS_REQUEST, request));
                break;
            } catch (ConnectionInactiveException e) {
                responseFutureContext.removeFuture(request.getId());
                log.error("[R:{}] ConsumerManager fail to call", e.getHost() + ":" + e.getPort(), e);
            }
        }
        return future;
    }

    /**
     * 订阅服务
     */
    @Override
    public void subscribe() {
        registryManager.subscribe();
    }

    /**
     * 关闭客户端所有连接
     */
    @Override
    public void close() {
        for (NettyClient client : clientPool.values()) {
            client.close();
        }
        executorService.shutdown();
    }

    /**
     * 获取Netty客户端
     *
     * @param address 地址
     * @return
     */
    public synchronized NettyClient getClient(InetSocketAddress address) {
        String host = address.getAddress().getHostAddress();
        Integer port = address.getPort();
        String key = host + ":" + port;
        NettyClient client = clientPool.get(key);
        if (client == null) {
            log.debug("[R:{}] ConsumerManager not found client", host + ":" + port);
            client = new NettyClient(serializeManager, responseFutureContext, host, port, config.getTimeout());
            clientPool.put(key, client);
            initClient(client);
        } else {
            if (!client.isActive()) {
                log.debug("[R:{}] ConsumerManager detect client inactive", client.getHost() + ":" + client.getPort());
                initClient(client);
            }
        }
        return client;
    }

    /**
     * 初始化连接
     *
     * @param client Netty客户端
     */
    private void initClient(NettyClient client) {
        log.debug("[R:{}] ConsumerManager start init client", client.getHost() + ":" + client.getPort());
        CountDownLatch countDownLatch = new CountDownLatch(1);
        executorService.execute(() -> client.connect(countDownLatch));
        try {
            countDownLatch.await();
            log.debug("[R:{}] ConsumerManager init client success", client.getHost() + ":" + client.getPort());
        } catch (InterruptedException e) {
            log.debug("[R:{}] ConsumerManager fail to init client", client.getHost() + ":" + client.getPort(), e);
        }
    }
}
