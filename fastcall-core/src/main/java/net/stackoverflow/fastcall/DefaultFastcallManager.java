package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.annotation.FastcallService;
import net.stackoverflow.fastcall.config.FastcallConfig;
import net.stackoverflow.fastcall.proxy.RpcProxyFactory;
import net.stackoverflow.fastcall.register.RegistryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * FastcallManager默认实现
 *
 * @author wormhole
 */
public class DefaultFastcallManager implements FastcallManager {

    private static final Logger log = LoggerFactory.getLogger(DefaultFastcallManager.class);

    private final RegistryManager registryManager;

    private ProviderManager providerManager;

    private final ConsumerManager consumerManager;

    private final FastcallConfig config;

    public DefaultFastcallManager(FastcallConfig config, RegistryManager registryManager, ProviderManager providerManager, ConsumerManager consumerManager) {
        this.config = config;
        this.registryManager = registryManager;
        this.providerManager = providerManager;
        this.consumerManager = consumerManager;
    }

    @Override
    public void setProviderManager(ProviderManager providerManager) {
        this.providerManager = providerManager;
    }

    @Override
    public FastcallConfig getConfig() {
        return config;
    }

    @Override
    public <T> T createProxy(Class<T> clazz, String group) {
        return RpcProxyFactory.create(clazz, group, this);
    }

    @Override
    public void registerService(Class<?> clazz, Object bean) {
        FastcallService fastcallService = clazz.getAnnotation(FastcallService.class);
        if (fastcallService != null) {
            String group = fastcallService.group();
            providerManager.registerService(clazz, bean, group);
        }
    }

    @Override
    public void registerService(Class<?> clazz, Object bean, String group) {
        providerManager.registerService(clazz, bean, group);
    }

    @Override
    public Object call(Method method, Object[] args, String group) {
        return consumerManager.call(method, args, group);
    }

    @Override
    public void start() {
        providerManager.start();
    }

    @Override
    public void stop() {
        providerManager.stop();
    }


}
