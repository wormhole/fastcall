package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.ConnectionManager;
import net.stackoverflow.fastcall.DefaultConnectionManager;
import net.stackoverflow.fastcall.properties.FastcallProperties;
import net.stackoverflow.fastcall.register.RegisterManager;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.handler.client.ClientRpcHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * fastcall client自动化配置类
 *
 * @author wormhole
 */
@Configuration
@AutoConfigureAfter(FastcallCommonAutoConfiguration.class)
public class FastcallConsumerAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FastcallConsumerAutoConfiguration.class);

    @Autowired
    private SerializeManager serializeManager;

    @Autowired
    private RegisterManager registerManager;

    @Autowired
    private FastcallProperties properties;

    @Bean
    public ClientRpcHandler clientRpcHandler() {
        ClientRpcHandler clientRpcHandler = new ClientRpcHandler();
        log.info("instance ClientRpcHandler");
        return clientRpcHandler;
    }

    @Bean
    public ConnectionManager connectionManager() {
        ConnectionManager connectionManager = new DefaultConnectionManager(serializeManager, clientRpcHandler(), properties.getConsumer().getTimeout());
        log.info("instance DefaultConnectionManager");
        return connectionManager;
    }

    /**
     * 动态代理@FastcallReference注解接口
     *
     * @return
     */
    @Bean
    public BeanPostProcessor beanPostProcessor() {
        FastcallBeanPostProcessor fastcallBeanPostProcessor = new FastcallBeanPostProcessor(registerManager, connectionManager());
        log.info("instance FastcallBeanPostProcessor");
        return fastcallBeanPostProcessor;
    }

}
