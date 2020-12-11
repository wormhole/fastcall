package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.config.FastcallConfig;
import net.stackoverflow.fastcall.proxy.RpcProxyFactory;
import net.stackoverflow.fastcall.registry.RegistryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**
     * 设置Provider管理类实现
     *
     * @param providerManager provider管理类实现
     */
    @Override
    public void setProviderManager(ProviderManager providerManager) {
        this.providerManager = providerManager;
    }

    /**
     * 获取fastcall配置
     *
     * @return fastcall配置类
     */
    @Override
    public FastcallConfig getConfig() {
        return config;
    }

    /**
     * 生成代理对象
     *
     * @param clazz   接口Class对象
     * @param group   所属分组
     * @param version 版本号
     * @param timeout rpc调用超时时间
     * @param <T>     泛型
     * @return
     */
    @Override
    public <T> T createProxy(Class<T> clazz, String group, String version, Long timeout) {
        return RpcProxyFactory.create(clazz, group, version, timeout, consumerManager);
    }

    /**
     * 注册服务
     *
     * @param clazz 需要暴露的接口
     * @param bean  服务bean对象
     */
    @Override
    public void registerService(Class<?> clazz, Object bean) {
        providerManager.registerService(clazz, bean);
    }

    /**
     * 启动服务
     */
    @Override
    public void start() {
        providerManager.start();
    }

    /**
     * 停止服务
     */
    @Override
    public void stop() {
        if (config.getProvider().getEnabled()) {
            providerManager.close();
        }
        consumerManager.close();
        registryManager.close();
    }


}
