package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.ConnectionManager;
import net.stackoverflow.fastcall.DefaultConnectionManager;
import net.stackoverflow.fastcall.properties.FastcallProperties;
import net.stackoverflow.fastcall.register.RegisterManager;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.FastcallClient;
import net.stackoverflow.fastcall.transport.handler.client.ClientRpcHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * fastcall client自动化配置类
 *
 * @author wormhole
 */
@Configuration
@AutoConfigureAfter(FastcallCommonAutoConfiguration.class)
public class FastcallConsumerAutoConfiguration {

    @Autowired
    private SerializeManager serializeManager;

    @Autowired
    private RegisterManager registerManager;

    @Autowired
    private FastcallProperties properties;

    @Bean
    public ClientRpcHandler clientRpcHandler() {
        return new ClientRpcHandler(serializeManager);
    }

    @Bean
    public ConnectionManager connectionManager() {
        return new DefaultConnectionManager(serializeManager,clientRpcHandler(),properties.getConsumer().getTimeout());
    }

    /**
     * 动态代理@FastcallReference注解接口
     *
     * @return
     */
    @Bean
    public BeanPostProcessor beanPostProcessor() {
        return new FastcallBeanPostProcessor(registerManager, connectionManager());
    }

}
