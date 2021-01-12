package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.annotation.FastcallService;
import net.stackoverflow.fastcall.balance.BalanceManager;
import net.stackoverflow.fastcall.config.FastcallConfig;
import net.stackoverflow.fastcall.config.TransportConfig;
import net.stackoverflow.fastcall.core.BeanContext;
import net.stackoverflow.fastcall.core.ResponseFuture;
import net.stackoverflow.fastcall.factory.RpcProxyFactory;
import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.registry.ServiceDefinition;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.TransportManager;
import net.stackoverflow.fastcall.transport.fastcall.proto.RpcRequest;
import net.stackoverflow.fastcall.util.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * FastcallManager默认实现
 *
 * @author wormhole
 */
public class DefaultFastcallManager implements FastcallManager {

    private static final Logger log = LoggerFactory.getLogger(DefaultFastcallManager.class);

    private final SerializeManager serializeManager;

    private final RegistryManager registryManager;

    private final TransportManager transportManager;

    private final BalanceManager balanceManager;

    private final FastcallConfig config;

    public DefaultFastcallManager(FastcallConfig config, SerializeManager serializeManager, RegistryManager registryManager, BalanceManager balanceManager, TransportManager transportManager) {
        this.config = config;
        this.serializeManager = serializeManager;
        this.registryManager = registryManager;
        this.balanceManager = balanceManager;
        this.transportManager = transportManager;
    }

    @Override
    public FastcallConfig config() {
        return config;
    }

    @Override
    public <T> T createProxy(Class<T> clazz, String group, String version, Long timeout, Class<?> fallback) {
        return RpcProxyFactory.create(this, clazz, group, version, timeout, fallback);
    }

    @Override
    public void register(Class<?> clazz, Object bean) {
        Class<?> cls = bean.getClass();
        FastcallService fastcallService = cls.getAnnotation(FastcallService.class);
        if (fastcallService != null) {
            BeanContext context = BeanContext.getInstance();
            context.setBean(clazz, bean);
            String group = fastcallService.group();
            String version = fastcallService.version();
            TransportConfig transportConfig = config.getTransport();
            registryManager.register(new ServiceDefinition(group, version, clazz.getName(), IpUtils.getIp(transportConfig.getHost()), transportConfig.getPort()));
        }
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

        List<ServiceDefinition> definitions = registryManager.getService(request.getInterfaceType(), group, version);
        InetSocketAddress address = balanceManager.choose(definitions);
        return transportManager.sendTo(address, request);
    }

    @Override
    public void start() {
        if (BeanContext.getInstance().size() > 0) {
            TransportConfig transportConfig = config.getTransport();
            transportManager.bind(new InetSocketAddress(transportConfig.getHost(), transportConfig.getPort()));
        }
    }

    @Override
    public void stop() {
        transportManager.close();
        registryManager.close();
    }

    @Override
    public SerializeManager serializeManager() {
        return serializeManager;
    }

    @Override
    public BalanceManager balanceManager() {
        return balanceManager;
    }

    @Override
    public RegistryManager registryManager() {
        return registryManager;
    }

    @Override
    public TransportManager transportManager() {
        return transportManager;
    }

}
