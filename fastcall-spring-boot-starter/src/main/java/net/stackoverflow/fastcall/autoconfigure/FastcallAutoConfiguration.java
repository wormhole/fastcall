package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.annotation.FastcallService;
import net.stackoverflow.fastcall.io.FastcallServer;
import net.stackoverflow.fastcall.properties.FastcallProperties;
import net.stackoverflow.fastcall.proxy.FastcallProxy;
import net.stackoverflow.fastcall.register.RegisterManager;
import net.stackoverflow.fastcall.register.ServiceMeta;
import net.stackoverflow.fastcall.register.zookeeper.ZkClient;
import net.stackoverflow.fastcall.register.zookeeper.ZooKeeperRegisterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * fastcall自动化配置类
 *
 * @author wormhole
 */
@Configuration
@EnableConfigurationProperties(FastcallProperties.class)
public class FastcallAutoConfiguration implements CommandLineRunner {

    @Autowired
    private FastcallProperties properties;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnProperty(prefix = "fastcall", value = "enabled", matchIfMissing = true)
    public FastcallServer fastcallServer() {
        FastcallServer server = new FastcallServer(properties.getBacklog(), properties.getTimeout(), properties.getHost(), properties.getPort(), applicationContext);
        return server;
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

    @Bean
    public Object buildProxy() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("net.stackoverflow.fastcall.demo.api.SayService");
        Object proxy = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new FastcallProxy());
        return proxy;
    }

    @Override
    public void run(String... args) throws Exception {
        RegisterManager manager = registerManager();
        FastcallServer server = fastcallServer();
        List<ServiceMeta> metas = getServiceMeta();

        for (ServiceMeta meta : metas) {
            manager.register(meta);
        }
        server.bind();
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
