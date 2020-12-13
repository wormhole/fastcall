package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.ConsumerManager;
import net.stackoverflow.fastcall.DefaultConsumerManager;
import net.stackoverflow.fastcall.balance.BalanceManager;
import net.stackoverflow.fastcall.balance.PollBalanceManager;
import net.stackoverflow.fastcall.balance.RandomBalanceManager;
import net.stackoverflow.fastcall.config.FastcallConfig;
import net.stackoverflow.fastcall.config.FastcallConfigBuilder;
import net.stackoverflow.fastcall.registry.JsonUtils;
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
import org.springframework.core.annotation.Order;

/**
 * fastcall 公共模块自动化配置类
 *
 * @author wormhole
 */
@Configuration
@EnableConfigurationProperties(FastcallProperties.class)
public class FastcallConsumerAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FastcallConsumerAutoConfiguration.class);

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
                manager = new ZooKeeperRegistryManager(zk.getHost(), zk.getPort(), zk.getSessionTimeout());
                log.info("Instance ZooKeeperRegistryManager");
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
        switch (properties.getConsumer().getBalance()) {
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

    @Bean
    public ConsumerManager consumerManager() {
        FastcallConfig config = fastcallConfig();
        SerializeManager serializeManager = serializeManager();
        RegistryManager registryManager = registryManager();
        BalanceManager balanceManager = balanceManager();
        ConsumerManager manager = new DefaultConsumerManager(config.getConsumer(), serializeManager, registryManager, balanceManager);
        log.info("Instance DefaultConsumerManager");
        return manager;
    }
}
