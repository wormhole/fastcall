package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.config.ConsumerConfig;
import net.stackoverflow.fastcall.exception.ConnectionInActiveException;
import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.NettyClient;
import net.stackoverflow.fastcall.transport.proto.Message;
import net.stackoverflow.fastcall.transport.proto.MessageType;
import net.stackoverflow.fastcall.transport.proto.RpcRequest;
import net.stackoverflow.fastcall.transport.proto.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Consumer默认实现
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

    public DefaultConsumerManager(ConsumerConfig config, SerializeManager serializeManager, RegistryManager registryManager) {
        this.serializeManager = serializeManager;
        this.registryManager = registryManager;
        this.config = config;
        this.clientPool = new ConcurrentHashMap<>();
        this.executorService = Executors.newFixedThreadPool(config.getThreads());
    }

    @Override
    public ConsumerConfig getConfig() {
        return config;
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
            try {
                NettyClient client = this.getClient(address);
                future = ResponseFutureContext.createFuture(request.getId());
                client.send(new Message(MessageType.BUSINESS_REQUEST, request));
                break;
            } catch (ConnectionInActiveException e) {
                ResponseFutureContext.removeFuture(request.getId());
                log.error("[R:{}] Connection is inactive", e.getHost() + ":" + e.getPort());
            }
        }
        return future;
    }

    public synchronized NettyClient getClient(InetSocketAddress address) {
        String host = address.getAddress().getHostAddress();
        Integer port = address.getPort();
        String key = host + ":" + port;
        NettyClient client = clientPool.get(key);
        if (client == null) {
            log.info("[R:{}] ConsumerManager not found client", host + ":" + port);
            client = new NettyClient(serializeManager, host, port, config.getTimeout());
            clientPool.put(key, client);
            initClient(client);
        } else {
            if (!client.isActive()) {
                log.info("[R:{}] ConsumerManager detect client inactive", client.getHost() + ":" + client.getPort());
                initClient(client);
            }
        }
        return client;
    }

    private void initClient(NettyClient client) {
        log.info("[R:{}] ConsumerManager init client start", client.getHost() + ":" + client.getPort());
        CountDownLatch countDownLatch = new CountDownLatch(1);
        executorService.execute(() -> client.connect(countDownLatch));
        try {
            countDownLatch.await();
            log.info("[R:{}] ConsumerManager init client success", client.getHost() + ":" + client.getPort());
        } catch (InterruptedException e) {
            log.info("[R:{}] ConsumerManager init client fail", client.getHost() + ":" + client.getPort(), e);
        }
    }
}
