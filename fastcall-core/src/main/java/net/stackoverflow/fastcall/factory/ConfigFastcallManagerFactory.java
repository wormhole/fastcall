package net.stackoverflow.fastcall.factory;

import net.stackoverflow.fastcall.*;
import net.stackoverflow.fastcall.balance.BalanceManager;
import net.stackoverflow.fastcall.balance.PollBalanceManager;
import net.stackoverflow.fastcall.balance.RandomBalanceManager;
import net.stackoverflow.fastcall.config.FastcallConfig;
import net.stackoverflow.fastcall.config.RegistryConfig;
import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.registry.zookeeper.ZooKeeperRegistryManager;
import net.stackoverflow.fastcall.serialize.JsonSerializeManager;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.TransportManager;
import net.stackoverflow.fastcall.transport.fastcall.FastcallTransportManager;

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
    public FastcallManager getInstance() {
        if (fastcallManager == null) {
            synchronized (this) {
                if (fastcallManager == null) {
                    SerializeManager serializeManager = this.serializeManager(config.getSerialize());
                    RegistryManager registryManager = this.registryManager(config.getRegistry());
                    BalanceManager balanceManager = this.balanceManager(config.getBalance());
                    TransportManager transportManager = this.transportManager(config.getTransport().getProto(), config.getThreads(), serializeManager);
                    fastcallManager = new DefaultFastcallManager(config, serializeManager, registryManager, balanceManager, transportManager);
                }
            }
        }
        return this.fastcallManager;
    }

    private SerializeManager serializeManager(String serialize) {
        if ("json".equals(serialize)) {
            return new JsonSerializeManager();
        } else {
            return null;
        }
    }

    private RegistryManager registryManager(RegistryConfig config) {
        if ("zookeeper".equals(config.getType())) {
            RegistryConfig.ZooKeeperConfig zk = config.getZookeeper();
            return new ZooKeeperRegistryManager(zk.getHost(), zk.getPort(), zk.getSessionTimeout());
        } else {
            return null;
        }
    }

    private BalanceManager balanceManager(String balance) {
        if ("random".equals(balance)) {
            return new RandomBalanceManager();
        } else if ("poll".equals(balance)) {
            return new PollBalanceManager();
        } else {
            return null;
        }
    }

    private TransportManager transportManager(String proto, Integer threads, SerializeManager serializeManager) {
        if ("fastcall".equals(proto)) {
            return new FastcallTransportManager(serializeManager, threads);
        } else {
            return null;
        }
    }

}
