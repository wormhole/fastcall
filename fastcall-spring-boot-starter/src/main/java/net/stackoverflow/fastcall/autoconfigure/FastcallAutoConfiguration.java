package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.annotation.FastcallService;
import net.stackoverflow.fastcall.bootstrap.FastcallServer;
import net.stackoverflow.fastcall.model.ServiceMeta;
import net.stackoverflow.fastcall.model.ZkData;
import net.stackoverflow.fastcall.properties.FastcallProperties;
import net.stackoverflow.fastcall.util.JsonUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

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
        FastcallServer server = new FastcallServer(properties.getBacklog(), properties.getTimeout(), properties.getHost(), properties.getPort());
        return server;
    }

    @Bean
    @ConditionalOnProperty(prefix = "fastcall.register", value = "zookeeper", matchIfMissing = true)
    public ZooKeeper zooKeeper() throws IOException, InterruptedException {
        String connection = properties.getZookeeper().getHost() + ":" + properties.getZookeeper().getPort();
        CountDownLatch connectedSignal = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(connection, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    connectedSignal.countDown();
                }
            }
        });
        connectedSignal.await();
        return zooKeeper;
    }

    @Override
    public void run(String... args) throws Exception {
        ZooKeeper zookeeper = zooKeeper();
        init(zookeeper);

        List<ServiceMeta> metas = getServiceMeta();
        String host = getIp();

        for (ServiceMeta meta : metas) {
            String path = "/fastcall/" + meta.getInterfaces();
            Stat stat = zookeeper.exists(path, false);
            if (stat == null) {
                ZkData data = new ZkData();
                data.addRoute(meta.getGroup(), host, properties.getPort());
                String json = JsonUtils.bean2json(data);
                if (json != null) {
                    zookeeper.create(path, json.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } else {
                byte[] data = zookeeper.getData(path, true, new Stat());
                String json = new String(data);
                ZkData zkData = (ZkData) JsonUtils.json2bean(json, ZkData.class);
                zkData.addRoute(meta.getGroup(), host, properties.getPort());
                json = JsonUtils.bean2json(zkData);
                zookeeper.setData(path, json.getBytes(), stat.getVersion());
            }
        }
    }

    private void init(ZooKeeper zookeeper) throws KeeperException, InterruptedException {
        Stat stat = zookeeper.exists("/fastcall", true);
        if (stat == null) {
            zookeeper.create("/fastcall", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    private List<ServiceMeta> getServiceMeta() throws UnknownHostException {
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(FastcallService.class);
        List<ServiceMeta> metas = new ArrayList<>();
        for (Object obj : map.values()) {
            Class<?> clazz = obj.getClass();
            FastcallService fastcallService = clazz.getAnnotation(FastcallService.class);
            String group = fastcallService.group();

            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> itf : interfaces) {
                metas.add(new ServiceMeta(group, itf.getName()));
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
