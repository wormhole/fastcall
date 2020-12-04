package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.annotation.FastcallService;
import net.stackoverflow.fastcall.properties.FastcallProperties;
import net.stackoverflow.fastcall.register.RegisterManager;
import net.stackoverflow.fastcall.register.ServiceMetaData;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.NettyServer;
import net.stackoverflow.fastcall.transport.handler.server.ServerRpcHandler;
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
@ConditionalOnProperty(prefix = "fastcall.provider", name = "enabled", havingValue = "true")
public class FastcallProviderAutoConfiguration implements InitializingBean, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(FastcallProviderAutoConfiguration.class);

    @Autowired
    private FastcallProperties properties;

    @Autowired
    private SerializeManager serializeManager;

    @Autowired
    private RegisterManager registerManager;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 向注册中心注册服务信息，并启动netty服务
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.registerService();
        new Thread(() -> fastcallServer().bind()).start();
    }

    /**
     * netty服务端
     *
     * @return
     */
    @Bean
    public NettyServer fastcallServer() {
        FastcallProperties.Provider provider = properties.getProvider();
        NettyServer server = new NettyServer(provider.getBacklog(), provider.getTimeout(), provider.getHost(), provider.getPort(), provider.getThreads());
        server.setSerializeManager(serializeManager);
        server.setRpcHandler(serverRpcHandler());
        log.info("Instance NettyServer");
        return server;
    }

    /**
     * rpc处理器
     *
     * @return
     */
    @Bean
    public ServerRpcHandler serverRpcHandler() {
        ServerRpcHandler serverRpcHandler = new ServerRpcHandler();
        log.info("Instance ServerRpcHandler");
        return serverRpcHandler;
    }

    /**
     * 获取服务元数据
     *
     * @return
     * @throws UnknownHostException
     */
    private List<ServiceMetaData> getServiceMeta() throws UnknownHostException {
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(FastcallService.class);
        List<ServiceMetaData> metas = new ArrayList<>();
        String host = getIp();

        for (Object obj : map.values()) {
            Class<?> clazz = obj.getClass();
            FastcallService fastcallService = clazz.getAnnotation(FastcallService.class);
            String group = fastcallService.group();
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> itf : interfaces) {
                metas.add(new ServiceMetaData(group, itf.getName(), host, properties.getProvider().getPort()));
            }
        }
        return metas;
    }

    /**
     * 向注册中心注册服务信息
     *
     * @throws UnknownHostException
     */
    private void registerService() throws UnknownHostException {
        List<ServiceMetaData> metas = getServiceMeta();
        for (ServiceMetaData meta : metas) {
            registerManager.register(meta);
        }
    }

    /**
     * 获取服务ip
     *
     * @return
     * @throws UnknownHostException
     */
    private String getIp() throws UnknownHostException {
        if (properties.getProvider().getHost().equals("0.0.0.0")) {
            InetAddress ip4 = Inet4Address.getLocalHost();
            return ip4.getHostAddress();
        } else {
            return properties.getProvider().getHost();
        }
    }
}
