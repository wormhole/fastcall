package net.stackoverflow.fastcall.registry.zookeeper;

import net.stackoverflow.fastcall.exception.ServiceNotFoundException;
import net.stackoverflow.fastcall.registry.JsonUtils;
import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.registry.ServiceMetaData;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper注册中心管理器
 *
 * @author wormhole
 */
public class ZooKeeperRegistryManager implements RegistryManager {

    private static final Logger log = LoggerFactory.getLogger(ZooKeeperRegistryManager.class);

    private static final String ROOT_PATH = "/fastcall";

    private ZooKeeper zookeeper;

    private final String host;

    private final Integer port;

    private final Integer sessionTimeout;

    public ZooKeeperRegistryManager(String host, Integer port, Integer sessionTimeout) throws IOException, InterruptedException {
        this.host = host;
        this.port = port;
        this.sessionTimeout = sessionTimeout;
        connect();
        this.checkPathAndCreate(ROOT_PATH);
    }

    /**
     * 连接zookeeper集群
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void connect() throws IOException, InterruptedException {
        String connection = host + ":" + port;
        CountDownLatch connectedSignal = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(connection, sessionTimeout, watchedEvent -> {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                connectedSignal.countDown();
            }
        });
        connectedSignal.await();
        this.zookeeper = zooKeeper;
        log.info("RegistryManager connected zookeeper");
    }

    @Override
    public synchronized void registerService(ServiceMetaData meta) {
        String path = ROOT_PATH + "/" + meta.getInterfaceName();
        this.checkPathAndCreate(path);
        String groupPath = path + "/" + meta.getGroup();
        this.checkPathAndCreate(groupPath);
        try {
            String json = JsonUtils.bean2json(meta);
            zookeeper.create(groupPath + "/service_", json.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            log.info("RegistryManager register service: {}", meta);
        } catch (InterruptedException | KeeperException e) {
            log.error("RegistryManager fail to register service: {}", meta, e);
        }
    }

    @Override
    public List<InetSocketAddress> getServiceAddress(Class<?> clazz, String group) {
        List<InetSocketAddress> socketAddresses = new ArrayList<>();
        try {
            String path = ROOT_PATH + "/" + clazz.getName() + "/" + group;
            List<String> childPath = zookeeper.getChildren(path, null);
            if (childPath != null && childPath.size() > 0) {
                for (String cp : childPath) {
                    byte[] bytes = zookeeper.getData(path + "/" + cp, false, new Stat());
                    String json = new String(bytes);
                    ServiceMetaData meta = JsonUtils.json2bean(json, ServiceMetaData.class);
                    socketAddresses.add(new InetSocketAddress(meta.getHost(), meta.getPort()));
                }
            } else {
                throw new ServiceNotFoundException(clazz.getName(), group, String.format("Service not found, interfaceName:{}, group:{}", clazz.getName(), group));
            }
        } catch (InterruptedException | KeeperException e) {
            log.error("RegistryManager fail to get service address, interfaceName:{}, group:{}", clazz.getName(), group, e);
        }
        return socketAddresses;
    }

    /**
     * 检查节点是否存在，不存在则创建
     */
    private synchronized void checkPathAndCreate(String path) {
        try {
            if (zookeeper.exists(path, false) == null) {
                zookeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                log.info("RegistryManager create path {}", path);
            }
        } catch (Exception e) {
            log.error("RegistryManager fail to check path {}", path, e);
        }
    }
}
