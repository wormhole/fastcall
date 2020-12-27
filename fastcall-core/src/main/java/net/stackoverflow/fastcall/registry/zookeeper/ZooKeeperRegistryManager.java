package net.stackoverflow.fastcall.registry.zookeeper;

import net.stackoverflow.fastcall.registry.AbstractRegistryManager;
import net.stackoverflow.fastcall.registry.ServiceDefinition;
import net.stackoverflow.fastcall.util.JsonUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper注册中心Manager
 *
 * @author wormhole
 */
public class ZooKeeperRegistryManager extends AbstractRegistryManager {

    private static final Logger log = LoggerFactory.getLogger(ZooKeeperRegistryManager.class);

    private static final String ROOT_PATH = "/fastcall";

    private ZooKeeper zookeeper;

    private final String host;

    private final Integer port;

    private final Integer sessionTimeout;

    private Watcher serviceWatcher;

    public ZooKeeperRegistryManager(String host, Integer port, Integer sessionTimeout) {
        this.host = host;
        this.port = port;
        this.sessionTimeout = sessionTimeout;
        this.serviceWatcher = new ServiceWatcher(this);
        this.connect();
        this.updateCache();
        this.subscribe();
    }

    /**
     * 连接zookeeper集群
     */
    private void connect() {
        String connection = host + ":" + port;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            ZooKeeper zooKeeper = new ZooKeeper(connection, sessionTimeout, new InitWatcher(countDownLatch));
            countDownLatch.await();
            this.zookeeper = zooKeeper;
        } catch (Exception e) {
            log.error("RegistryManager fail to connected zookeeper", e);
        }
        this.checkPathAndCreate(ROOT_PATH);
    }

    /**
     * 注册服务
     *
     * @param definition 服务定义
     */
    @Override
    public void register(ServiceDefinition definition) {
        String path = ROOT_PATH + "/" + definition.getInterfaceName();
        this.checkPathAndCreate(path);
        try {
            String json = JsonUtils.bean2json(definition);
            zookeeper.create(path + "/service_", json.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            log.info("RegistryManager register service: {}", definition);
        } catch (InterruptedException | KeeperException e) {
            log.error("RegistryManager fail to register service: {}", definition, e);
        }
    }

    /**
     * 订阅服务
     */
    @Override
    public synchronized void subscribe() {
        try {
            List<String> itfChildPaths = zookeeper.getChildren(ROOT_PATH, serviceWatcher);
            log.debug("RegistryManager watched children of path {}", ROOT_PATH);
            for (String itfChildPath : itfChildPaths) {
                String itfPath = ROOT_PATH + "/" + itfChildPath;
                List<String> serviceChildPaths = zookeeper.getChildren(itfPath, serviceWatcher);
                Collections.sort(serviceChildPaths);
                log.debug("RegistryManager watched children of path {}", itfPath);
            }
        } catch (Exception e) {
            log.error("RegistryManager fail to subscribe", e);
        }
    }

    @Override
    public synchronized void updateCache() {
        try {
            Map<String, List<ServiceDefinition>> latestCache = new ConcurrentHashMap<>();
            List<String> itfChildPaths = zookeeper.getChildren(ROOT_PATH, false);
            for (String itfChildPath : itfChildPaths) {
                String itfPath = ROOT_PATH + "/" + itfChildPath;
                List<String> serviceChildPaths = zookeeper.getChildren(itfPath, false);
                Collections.sort(serviceChildPaths);

                List<ServiceDefinition> definitions = new ArrayList<>();
                latestCache.put(itfChildPath, definitions);
                for (String serviceChildPath : serviceChildPaths) {
                    String servicePath = itfPath + "/" + serviceChildPath;

                    byte[] bytes = zookeeper.getData(servicePath, false, new Stat());
                    String json = new String(bytes);
                    ServiceDefinition definition = JsonUtils.json2bean(json, ServiceDefinition.class);
                    definitions.add(definition);
                }
            }
            cache.setCache(latestCache);
        } catch (Exception e) {
            log.error("RegistryManager fail to update cache", e);
        }
    }

    /**
     * 关闭zookeeper连接
     */
    @Override
    public void close() {
        try {
            zookeeper.close();
            log.info("RegistryManager closed");
        } catch (InterruptedException e) {
            log.error("RegistryManager fail to close zookeeper", e);
        }
    }

    /**
     * 检查节点是否存在，不存在则创建
     */
    private void checkPathAndCreate(String path) {
        try {
            String[] paths = path.split("/");
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < paths.length; i++) {
                sb.append("/").append(paths[i]);
                if (zookeeper.exists(sb.toString(), false) == null) {
                    zookeeper.create(sb.toString(), "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            }
        } catch (Exception e) {
            log.error("RegistryManager fail to check path {}", path, e);
        }
    }
}
