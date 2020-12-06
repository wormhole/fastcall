package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.ConsumerManager;
import net.stackoverflow.fastcall.DefaultConsumerManager;
import net.stackoverflow.fastcall.DefaultFastcallManager;
import net.stackoverflow.fastcall.FastcallManager;
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
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * fastcall 公共模块自动化配置类
 *
 * @author wormhole
 */
@Configuration
@EnableConfigurationProperties(FastcallProperties.class)
public class FastcallCommonAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FastcallCommonAutoConfiguration.class);

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
    @ConditionalOnProperty(prefix = "fastcall", name = "serialize", havingValue = "json")
    public SerializeManager serializeManager() {
        SerializeManager manager = new JsonSerializeManager();
        log.info("Instance JsonSerializeManager");
        return manager;
    }

    /**
     * 初始化注册管理器
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Bean
    @ConditionalOnProperty(prefix = "fastcall.registry", name = "type", havingValue = "zookeeper")
    public RegistryManager registryManager() throws IOException, InterruptedException {
        FastcallProperties.Zookeeper zk = properties.getRegistry().getZookeeper();
        RegistryManager manager = new ZooKeeperRegistryManager(zk.getHost(), zk.getPort(), zk.getSessionTimeout());
        log.info("Instance ZooKeeperRegistryManager");
        return manager;
    }

    @Bean
    public ConsumerManager consumerManager() throws IOException, InterruptedException {
        ConsumerManager manager = new DefaultConsumerManager(fastcallConfig().getConsumer(), serializeManager(), registryManager());
        log.info("Instance DefaultConsumerManager");
        return manager;
    }

    @Bean
    public FastcallManager fastcallManager() throws IOException, InterruptedException {
        FastcallManager manager = new DefaultFastcallManager(fastcallConfig(), registryManager(), null, consumerManager());
        log.info("Instance FastcallManager");
        return manager;
    }

    /**
     * 动态代理@FastcallReference注解接口
     *
     * @return
     */
    @Bean
    public BeanPostProcessor beanPostProcessor() throws IOException, InterruptedException {
        FastcallBeanPostProcessor fastcallBeanPostProcessor = new FastcallBeanPostProcessor(fastcallManager());
        log.info("Instance FastcallBeanPostProcessor");
        return fastcallBeanPostProcessor;
    }
}
