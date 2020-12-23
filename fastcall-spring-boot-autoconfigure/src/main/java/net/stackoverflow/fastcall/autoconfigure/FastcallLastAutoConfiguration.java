package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.ConsumerManager;
import net.stackoverflow.fastcall.DefaultFastcallManager;
import net.stackoverflow.fastcall.FastcallManager;
import net.stackoverflow.fastcall.ProviderManager;
import net.stackoverflow.fastcall.config.FastcallConfig;
import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter({FastcallConsumerAutoConfiguration.class, FastcallProviderAutoConfiguration.class})
public class FastcallLastAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FastcallLastAutoConfiguration.class);

    @Autowired
    private FastcallConfig fastcallConfig;

    @Autowired
    private SerializeManager serializeManager;

    @Autowired
    private RegistryManager registryManager;

    @Autowired
    private ConsumerManager consumerManager;

    @Autowired(required = false)
    private ProviderManager providerManager;

    @Bean
    public FastcallManager fastcallManager() {
        FastcallManager manager = new DefaultFastcallManager(fastcallConfig, serializeManager, registryManager, providerManager, consumerManager);
        log.info("Instance FastcallManager");
        return manager;
    }

    /**
     * 动态代理@FastcallReference注解接口
     *
     * @return
     */
    @Bean
    public BeanPostProcessor beanPostProcessor() {
        FastcallBeanPostProcessor fastcallBeanPostProcessor = new FastcallBeanPostProcessor();
        log.info("Instance FastcallBeanPostProcessor");
        return fastcallBeanPostProcessor;
    }

    /**
     * 生命周期
     *
     * @return
     */
    @Bean
    public FastcallLifecycle fastcallLifecycle() {
        return new FastcallLifecycle();
    }
}
