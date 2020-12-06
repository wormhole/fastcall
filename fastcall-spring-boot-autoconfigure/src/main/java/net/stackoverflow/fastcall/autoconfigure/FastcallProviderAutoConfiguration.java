package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.DefaultProviderManager;
import net.stackoverflow.fastcall.FastcallManager;
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
@AutoConfigureAfter(FastcallCommonAutoConfiguration.class)
@ConditionalOnProperty(prefix = "fastcall.provider", name = "enabled", havingValue = "true")
public class FastcallProviderAutoConfiguration implements InitializingBean, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(FastcallProviderAutoConfiguration.class);

    @Autowired
    private FastcallConfig fastcallConfig;

    @Autowired
    private SerializeManager serializeManager;

    @Autowired
    private RegistryManager registryManager;

    @Autowired
    private FastcallManager fastcallManager;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 向注册中心注册服务信息，并启动netty服务
     */
    @Override
    public void afterPropertiesSet() {
        fastcallManager.setProviderManager(providerManager());
        this.registerService();
        this.start();
    }

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

    /**
     * 向注册中心注册服务信息
     */
    private void registerService() {
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(FastcallService.class);

        for (Object obj : map.values()) {
            Class<?> clazz = obj.getClass();
            FastcallService fastcallService = clazz.getAnnotation(FastcallService.class);
            String group = fastcallService.group();
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> itf : interfaces) {
                fastcallManager.registerService(itf, obj, group);
            }
        }
    }

    /**
     * 绑定服务端
     */
    private void start() {
        fastcallManager.start();
    }
}
