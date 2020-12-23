package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.DefaultProviderManager;
import net.stackoverflow.fastcall.ProviderManager;
import net.stackoverflow.fastcall.annotation.FastcallService;
import net.stackoverflow.fastcall.config.FastcallConfig;
import net.stackoverflow.fastcall.config.ProviderConfig;
import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * fastcall provider自动化配置类
 *
 * @author wormhole
 */
@Configuration
@AutoConfigureAfter(FastcallConsumerAutoConfiguration.class)
@ConditionalOnProperty(prefix = "fastcall.provider", name = "enabled", havingValue = "true")
public class FastcallProviderAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FastcallProviderAutoConfiguration.class);

    @Autowired
    private FastcallConfig fastcallConfig;

    @Autowired
    private SerializeManager serializeManager;

    @Autowired
    private RegistryManager registryManager;

    /**
     * 初始化ProviderManager
     *
     * @return
     */
    @Bean
    public ProviderManager providerManager() {
        ProviderConfig config = fastcallConfig.getProvider();
        ProviderManager providerManager = new DefaultProviderManager(config, serializeManager, registryManager);
        log.info("Instance DefaultProviderManager");
        return providerManager;
    }
}
