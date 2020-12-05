package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.properties.FastcallProperties;
import net.stackoverflow.fastcall.register.RegisterManager;
import net.stackoverflow.fastcall.register.zookeeper.ZooKeeperRegisterManager;
import net.stackoverflow.fastcall.serialize.JsonSerializeManager;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * fastcall common自动化配置类
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
    @ConditionalOnProperty(prefix = "fastcall", name = "register", havingValue = "zookeeper")
    public RegisterManager registerManager() throws IOException, InterruptedException {
        FastcallProperties.Zookeeper zk = properties.getZookeeper();
        RegisterManager manager = new ZooKeeperRegisterManager(zk.getHost(), zk.getPort(), zk.getSessionTimeout());
        log.info("Instance ZooKeeperRegisterManager");
        return manager;
    }
}
