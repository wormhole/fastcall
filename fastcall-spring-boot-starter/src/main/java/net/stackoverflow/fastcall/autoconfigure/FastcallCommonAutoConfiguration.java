package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.properties.FastcallProperties;
import net.stackoverflow.fastcall.register.RegisterManager;
import net.stackoverflow.fastcall.register.zookeeper.ZkClient;
import net.stackoverflow.fastcall.register.zookeeper.ZooKeeperRegisterManager;
import net.stackoverflow.fastcall.serialize.JsonSerializeManager;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties(FastcallProperties.class)
public class FastcallCommonAutoConfiguration {

    @Autowired
    private FastcallProperties properties;

    @Bean
    public SerializeManager serializeManager() {
        return new JsonSerializeManager();
    }

    @Bean
    public RegisterManager registerManager() throws IOException, InterruptedException {
        RegisterManager manager = null;
        switch (properties.getRegister()) {
            case "zookeeper":
                FastcallProperties.Zookeeper zk = properties.getZookeeper();
                ZkClient client = new ZkClient(zk.getHost(), zk.getPort(), zk.getSessionTimeout());
                manager = new ZooKeeperRegisterManager(client);
                break;
            default:
                break;
        }
        return manager;
    }
}
