package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.DefaultFastcallFacade;
import net.stackoverflow.fastcall.FastcallFacade;
import net.stackoverflow.fastcall.balance.BalanceManager;
import net.stackoverflow.fastcall.config.FastcallConfig;
import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.TransportManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter({FastcallSubSystemAutoConfiguration.class})
public class FastcallAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FastcallAutoConfiguration.class);

    @Autowired
    private FastcallConfig fastcallConfig;

    @Autowired
    private SerializeManager serializeManager;

    @Autowired
    private RegistryManager registryManager;

    @Autowired
    private BalanceManager balanceManager;

    @Autowired
    private TransportManager transportManager;

    @Bean
    public FastcallFacade fastcallManager() {
        FastcallFacade manager = new DefaultFastcallFacade(fastcallConfig, serializeManager, registryManager, balanceManager, transportManager);
        log.info("Instance DefaultFastcallFacade");
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
