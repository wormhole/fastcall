package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.annotation.FastcallService;
import net.stackoverflow.fastcall.properties.FastcallProperties;
import net.stackoverflow.fastcall.register.RegisterManager;
import net.stackoverflow.fastcall.register.ServiceMeta;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.FastcallClient;
import net.stackoverflow.fastcall.transport.FastcallServer;
import net.stackoverflow.fastcall.transport.handler.server.ServerRpcHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * fastcall provider自动化配置类
 *
 * @author wormhole
 */
@Configuration
@AutoConfigureAfter(FastcallCommonAutoConfiguration.class)
public class FastcallProviderAutoConfiguration implements InitializingBean, ApplicationContextAware {

    @Autowired
    private FastcallProperties properties;

    @Autowired
    private SerializeManager serializeManager;

    @Autowired
    private RegisterManager registerManager;

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<ServiceMeta> metas = getServiceMeta();
        for (ServiceMeta meta : metas) {
            registerManager.register(meta);
        }
        new Thread(() -> fastcallServer().bind()).start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnProperty(prefix = "fastcall", value = "enabled", matchIfMissing = true)
    public FastcallServer fastcallServer() {
        FastcallServer server = new FastcallServer(properties.getBacklog(), properties.getTimeout(), properties.getHost(), properties.getPort(), 100);
        server.setSerializeManager(serializeManager);
        server.setRpcHandler(serverRpcHandler());
        return server;
    }

    @Bean
    public ServerRpcHandler serverRpcHandler() {
        return new ServerRpcHandler();
    }

    private List<ServiceMeta> getServiceMeta() throws UnknownHostException {
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(FastcallService.class);
        List<ServiceMeta> metas = new ArrayList<>();
        String host = getIp();

        for (Object obj : map.values()) {
            Class<?> clazz = obj.getClass();
            FastcallService fastcallService = clazz.getAnnotation(FastcallService.class);
            String group = fastcallService.group();
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> itf : interfaces) {
                metas.add(new ServiceMeta(group, itf.getName(), host, properties.getPort()));
            }
        }
        return metas;
    }

    private String getIp() throws UnknownHostException {
        if (properties.getHost().equals("0.0.0.0")) {
            InetAddress ip4 = Inet4Address.getLocalHost();
            return ip4.getHostAddress();
        } else {
            return properties.getHost();
        }
    }
}
