package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.balance.BalanceManager;
import net.stackoverflow.fastcall.balance.PollBalanceManager;
import net.stackoverflow.fastcall.balance.RandomBalanceManager;
import net.stackoverflow.fastcall.config.FastcallConfig;
import net.stackoverflow.fastcall.config.FastcallConfigBuilder;
import net.stackoverflow.fastcall.registry.redis.RedisRegistryManager;
import net.stackoverflow.fastcall.transport.TransportManager;
import net.stackoverflow.fastcall.transport.fastcall.FastcallTransportManager;
import net.stackoverflow.fastcall.util.JsonUtils;
import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.registry.zookeeper.ZooKeeperRegistryManager;
import net.stackoverflow.fastcall.serialize.JsonSerializeManager;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * fastcall 公共模块自动化配置类
 *
 * @author wormhole
 */
@Configuration
@EnableConfigurationProperties(FastcallProperties.class)
public class FastcallSubSystemAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FastcallSubSystemAutoConfiguration.class);

    @Autowired
    private FastcallProperties properties;

    /**
     * 初始化配置
     *
     * @return
     */
    @Bean
    public FastcallConfig fastcallConfig() {
        FastcallConfigBuilder builder = new FastcallConfigBuilder();
        String json = JsonUtils.bean2json(properties);
        builder.setConfig(json);
        log.info("Instance FastcallConfig");
        return builder.build();
    }

    /**
     * 初始化序列化管理器
     *
     * @return
     */
    @Bean
    public SerializeManager serializeManager() {
        SerializeManager manager = null;
        switch (properties.getSerialize()) {
            case "json":
                manager = new JsonSerializeManager();
                log.info("Instance JsonSerializeManager");
                break;
            default:
                break;
        }
        return manager;
    }

    /**
     * 初始化注册管理器
     *
     * @return
     */
    @Bean
    public RegistryManager registryManager() {
        RegistryManager manager = null;
        switch (properties.getRegistry().getType()) {
            case "zookeeper":
                FastcallProperties.Zookeeper zk = properties.getRegistry().getZookeeper();
                manager = new ZooKeeperRegistryManager(zk.getAddress(), zk.getSessionTimeout());
                log.info("Instance ZooKeeperRegistryManager");
                break;
            case "redis":
                FastcallProperties.Redis redis = properties.getRegistry().getRedis();
                manager = new RedisRegistryManager(redis.getHost(), redis.getPort(), redis.getPassword(), redis.getTimeout());
                log.info("Instance RedisRegistryManager");
                break;
            default:
                break;
        }
        return manager;
    }

    /**
     * 负载均衡策略
     *
     * @return
     */
    @Bean
    public BalanceManager balanceManager() {
        BalanceManager balanceManager = null;
        switch (properties.getBalance()) {
            case "random":
                balanceManager = new RandomBalanceManager();
                log.info("Instance RandomBalanceManager");
                break;
            case "poll":
                balanceManager = new PollBalanceManager();
                log.info("Instance PollBalanceManager");
                break;
            default:
                break;
        }
        return balanceManager;
    }

    /**
     * 传输管理
     *
     * @return
     */
    @Bean
    public TransportManager transportManager() {
        TransportManager transportManager = null;
        switch (properties.getTransport().getProto()) {
            case "fastcall":
                transportManager = new FastcallTransportManager(serializeManager(), properties.getThreads());
                log.info("Instance FastcallTransportManager");
                break;
            default:
                break;
        }
        return transportManager;
    }
}
