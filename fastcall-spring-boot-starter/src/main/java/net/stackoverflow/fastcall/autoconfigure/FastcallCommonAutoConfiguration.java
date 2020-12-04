package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.properties.FastcallProperties;
import net.stackoverflow.fastcall.register.RegisterManager;
import net.stackoverflow.fastcall.register.zookeeper.ZooKeeperRegisterManager;
import net.stackoverflow.fastcall.serialize.JsonSerializeManager;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public SerializeManager serializeManager() {
        SerializeManager manager = null;
        switch (properties.getSerialize()) {
            case "json":
                manager = new JsonSerializeManager();
                log.info("instance JsonSerializeManager");
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
     * @throws IOException
     * @throws InterruptedException
     */
    @Bean
    public RegisterManager registerManager() throws IOException, InterruptedException {
        RegisterManager manager = null;
        switch (properties.getRegister()) {
            case "zookeeper":
                FastcallProperties.Zookeeper zk = properties.getZookeeper();
                manager = new ZooKeeperRegisterManager(zk.getHost(), zk.getPort(), zk.getSessionTimeout());
                log.info("instance ZooKeeperRegisterManager");
                break;
            default:
                break;
        }
        return manager;
    }
}
