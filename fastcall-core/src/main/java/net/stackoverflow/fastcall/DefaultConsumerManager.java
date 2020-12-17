package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.balance.BalanceManager;
import net.stackoverflow.fastcall.config.ConsumerConfig;
import net.stackoverflow.fastcall.core.ResponseFuture;
import net.stackoverflow.fastcall.context.ResponseFutureContext;
import net.stackoverflow.fastcall.factory.NameThreadFactory;
import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.registry.ServiceMetaData;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ConsumerManager默认实现
 *
 * @author wormhole
 */
public class DefaultConsumerManager implements ConsumerManager {

    private static final Logger log = LoggerFactory.getLogger(DefaultConsumerManager.class);

    private final SerializeManager serializeManager;

    private final RegistryManager registryManager;

    private final BalanceManager balanceManager;

    private final Map<String, NettyClient> clientPool;

    private final ExecutorService connectionExecutorService;

    private final ConsumerConfig config;

    private final ResponseFutureContext responseFutureContext;

    public DefaultConsumerManager(ConsumerConfig config, SerializeManager serializeManager, RegistryManager registryManager, BalanceManager balanceManager) {
        this.serializeManager = serializeManager;
        this.registryManager = registryManager;
        this.balanceManager = balanceManager;
        this.config = config;
        this.clientPool = new ConcurrentHashMap<>();
        this.responseFutureContext = new ResponseFutureContext();
        this.connectionExecutorService = Executors.newFixedThreadPool(config.getMaxConnection(), new NameThreadFactory("ConnectionThreadPool"));
        this.subscribe();
    }

    @Override
    public ConsumerConfig config() {
        return config;
    }

    @Override
    public SerializeManager getSerializeManager() {
        return this.serializeManager;
    }

    @Override
    public ResponseFuture call(Method method, Object[] args, String group, String version) {
        RpcRequest request = new RpcRequest();
        request.setId(UUID.randomUUID().toString());
        request.setInterfaceType(method.getDeclaringClass());
        request.setMethod(method.getName());
        request.setGroup(group);
        request.setVersion(version);
        request.setParams(args == null ? null : Arrays.asList(args));
        request.setParamsType(Arrays.asList(method.getParameterTypes()));

        List<ServiceMetaData> serviceMetaDataList = registryManager.getService(request.getInterfaceType(), group, version);
        InetSocketAddress address = balanceManager.choose(serviceMetaDataList);
        NettyClient client = this.getClient(address);
        return client.send(new Message(MessageType.BUSINESS_REQUEST, request));
    }

    /**
     * 移除ResponseFuture
     *
     * @param requestId 唯一标识
     */
    @Override
    public void removeFuture(String requestId) {
        responseFutureContext.removeFuture(requestId);
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
        connectionExecutorService.shutdown();
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
            client = new NettyClient(serializeManager, responseFutureContext, host, port, config.getTimeout());
            initClient(client);
        }
        return client;
    }

    /**
     * 初始化连接
     *
     * @param client Netty客户端
     */
    private void initClient(NettyClient client) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        connectionExecutorService.execute(() -> {
            clientPool.put(client.getHost() + ":" + client.getPort(), client);
            client.connect(countDownLatch);
            clientPool.remove(client.getHost() + ":" + client.getPort());
        });
        try {
            countDownLatch.await();
            log.debug("[R:{}] ConsumerManager init client success", client.getHost() + ":" + client.getPort());
        } catch (InterruptedException e) {
            log.debug("[R:{}] ConsumerManager fail to init client", client.getHost() + ":" + client.getPort(), e);
        }
    }
}
