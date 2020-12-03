package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.register.RegisterManager;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.FastcallClient;
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

    @Autowired
    private SerializeManager serializeManager;

    @Autowired
    private RegisterManager registerManager;

    @Bean
    public FastcallClient fastcallClient() {
        return new FastcallClient(serializeManager, registerManager);
    }

    /**
     * 动态代理@FastcallReference注解接口
     *
     * @return
     */
    @Bean
    public BeanPostProcessor beanPostProcessor() {
        return new FastcallBeanPostProcessor(fastcallClient());
    }

}
