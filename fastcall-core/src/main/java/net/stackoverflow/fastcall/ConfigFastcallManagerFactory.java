package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.config.ConsumerConfig;
import net.stackoverflow.fastcall.config.FastcallConfig;
import net.stackoverflow.fastcall.config.ProviderConfig;
import net.stackoverflow.fastcall.config.RegistryConfig;
import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.registry.zookeeper.ZooKeeperRegistryManager;
import net.stackoverflow.fastcall.serialize.JsonSerializeManager;
import net.stackoverflow.fastcall.serialize.SerializeManager;

import java.io.IOException;

/**
 * ConfigFastcallManager工厂类
 *
 * @author wormhole
 */
public class ConfigFastcallManagerFactory implements FastcallManagerFactory {

    private final FastcallConfig config;

    private FastcallManager fastcallManager;

    public ConfigFastcallManagerFactory() {
        this.config = new FastcallConfig();
    }

    public ConfigFastcallManagerFactory(FastcallConfig config) {
        this.config = config;
    }

    @Override
    public FastcallManager getInstance() throws IOException, InterruptedException {
        if (fastcallManager == null) {
            synchronized (this) {
                if (fastcallManager == null) {
                    SerializeManager serializeManager = this.serializeManager(config.getSerialize());
                    RegistryManager registryManager = this.registryManager(config.getRegistry());
                    ConsumerManager consumerManager = this.consumerManager(config.getConsumer(), serializeManager, registryManager);
                    fastcallManager = new DefaultFastcallManager(config, registryManager, null, consumerManager);
                    if (config.getProvider().getEnabled()) {
                        ProviderManager providerManager = this.providerManager(config.getProvider(), serializeManager, registryManager);
                        fastcallManager.setProviderManager(providerManager);
                    }
                }
            }
        }
        return this.fastcallManager;
    }

    private SerializeManager serializeManager(String type) {
        if ("json".equals(type)) {
            return new JsonSerializeManager();
        } else {
            return null;
        }
    }

    private RegistryManager registryManager(RegistryConfig config) throws IOException, InterruptedException {
        if ("zookeeper".equals(config.getType())) {
            RegistryConfig.ZooKeeperConfig zk = config.getZookeeper();
            return new ZooKeeperRegistryManager(zk.getHost(), zk.getPort(), zk.getSessionTimeout());
        } else {
            return null;
        }
    }

    private ConsumerManager consumerManager(ConsumerConfig config, SerializeManager serializeManager, RegistryManager registryManager) {
        return new DefaultConsumerManager(config, serializeManager, registryManager);
    }

    private ProviderManager providerManager(ProviderConfig config, SerializeManager serializeManager, RegistryManager registryManager) {
        return new DefaultProviderManager(config, serializeManager, registryManager);
    }

}
