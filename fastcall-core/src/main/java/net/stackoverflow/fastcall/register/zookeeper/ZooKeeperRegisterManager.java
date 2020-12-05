package net.stackoverflow.fastcall.register.zookeeper;

import net.stackoverflow.fastcall.exception.ServiceNotFoundException;
import net.stackoverflow.fastcall.register.RegistryData;
import net.stackoverflow.fastcall.register.RegisterManager;
import net.stackoverflow.fastcall.register.ServiceMetaData;
import net.stackoverflow.fastcall.util.JsonUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper注册中心管理器
 *
 * @author wormhole
 */
public class ZooKeeperRegisterManager implements RegisterManager {

    private static final Logger log = LoggerFactory.getLogger(ZooKeeperRegisterManager.class);

    private static final String ROOT_PATH = "/fastcall";

    private ZooKeeper zookeeper;

    private final String host;

    private final Integer port;

    private final Integer sessionTimeout;

    public ZooKeeperRegisterManager(String host, Integer port, Integer sessionTimeout) throws IOException, InterruptedException {
        this.host = host;
        this.port = port;
        this.sessionTimeout = sessionTimeout;
        connect();
        this.createRootNode();
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
        log.info("RegisterManager connected zookeeper");
    }

    @Override
    public synchronized void register(ServiceMetaData meta) {
        String path = ROOT_PATH + "/" + meta.getInterfaceName();
        try {
            Stat stat = zookeeper.exists(path, false);
            if (stat == null) {
                RegistryData data = new RegistryData();
                data.addRouteAddress(meta.getGroup(), meta.getHost(), meta.getPort());
                String json = JsonUtils.bean2json(data);
                zookeeper.create(path, json.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } else {
                byte[] bytes = zookeeper.getData(path, false, new Stat());
                String json = new String(bytes);
                RegistryData data = JsonUtils.json2bean(json, RegistryData.class);
                data.addRouteAddress(meta.getGroup(), meta.getHost(), meta.getPort());
                json = JsonUtils.bean2json(data);
                zookeeper.setData(path, json.getBytes(), stat.getVersion());
            }
            log.info("RegisterManager register service: {}", meta);
        } catch (InterruptedException | KeeperException e) {
            log.error("RegisterManager fail to register service: {}", meta, e);
        }
    }

    @Override
    public InetSocketAddress getServiceAddress(String group, String className) {
        InetSocketAddress inetSocketAddress = null;
        try {
            byte[] bytes = zookeeper.getData(ROOT_PATH + "/" + className, false, new Stat());
            String json = new String(bytes);
            RegistryData data = JsonUtils.json2bean(json, RegistryData.class);
            Map<String, List<RegistryData.RouteAddress>> map = data.getRoute();
            List<RegistryData.RouteAddress> routeAddresses = map.get(group);
            if (routeAddresses != null) {
                RegistryData.RouteAddress routeAddress = this.randomRouteAddress(routeAddresses);
                inetSocketAddress = new InetSocketAddress(routeAddress.getHost(), routeAddress.getPort());
                log.debug("RegisterManager get service address, interfaceName:{}, group:{}, ip:{}, port:{}", className, group, routeAddress.getHost(), routeAddress.getPort());
            } else {
                throw new ServiceNotFoundException(className, group, String.format("Service not found, interfaceName:{}, group:{}", className, group));
            }
        } catch (InterruptedException | KeeperException e) {
            log.error("RegisterManager fail to get service address, interfaceName:{}, group:{}", className, group, e);
        }
        return inetSocketAddress;
    }

    private RegistryData.RouteAddress randomRouteAddress(List<RegistryData.RouteAddress> addresses) {
        Random random = new Random();
        int index = random.nextInt(addresses.size());
        return addresses.get(index);
    }

    /**
     * 检查根节点是否存在，不存在则创建
     */
    private synchronized void createRootNode() {
        try {
            if (zookeeper.exists(ROOT_PATH, false) == null) {
                zookeeper.create(ROOT_PATH, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                log.info("RegisterManager create root node");
            }
        } catch (Exception e) {
            log.error("RegisterManager fail to check root node", e);
        }
    }
}
