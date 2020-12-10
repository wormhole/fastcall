package net.stackoverflow.fastcall.factory;

import net.stackoverflow.fastcall.config.RegistryConfig;
import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.registry.zookeeper.ZooKeeperRegistryManager;

/**
 * RegistryManager工厂类
 */
public class ConfigRegistryManagerFactory implements RegistryManagerFactory {

    private final RegistryConfig config;

    private RegistryManager registryManager;

    public ConfigRegistryManagerFactory() {
        this.config = new RegistryConfig();
    }

    public ConfigRegistryManagerFactory(RegistryConfig config) {
        this.config = config;
    }

    @Override
    public RegistryManager getInstance() {
        if (registryManager == null) {
            synchronized (this) {
                if (registryManager == null) {
                    if ("zookeeper".equals(config.getType())) {
                        RegistryConfig.ZooKeeperConfig zk = config.getZookeeper();
                        this.registryManager = new ZooKeeperRegistryManager(zk.getHost(), zk.getPort(), zk.getSessionTimeout());
                    } else {
                        return null;
                    }
                }
            }
        }
        return this.registryManager;
    }
}
