package net.stackoverflow.fastcall.registry.zookeeper;

import net.stackoverflow.fastcall.exception.ServiceNotFoundException;
import net.stackoverflow.fastcall.registry.JsonUtils;
import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.registry.ServiceMetaData;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper注册中心管理器
 *
 * @author wormhole
 */
public class ZooKeeperRegistryManager implements RegistryManager {

    private static final Logger log = LoggerFactory.getLogger(ZooKeeperRegistryManager.class);

    private ZooKeeper zookeeper;

    private final String host;

    private final Integer port;

    private final Integer sessionTimeout;

    private final ServiceAddressCache cache;

    private final ChildPathWatcher watcher;

    public ZooKeeperRegistryManager(String host, Integer port, Integer sessionTimeout) throws IOException, InterruptedException {
        this.host = host;
        this.port = port;
        this.sessionTimeout = sessionTimeout;
        this.cache = new ServiceAddressCache();
        this.watcher = new ChildPathWatcher(cache);
        this.connect();
    }

    /**
     * 连接zookeeper集群
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void connect() throws IOException, InterruptedException {
        String connection = host + ":" + port;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        watcher.setCountDownLatch(countDownLatch);
        ZooKeeper zooKeeper = new ZooKeeper(connection, sessionTimeout, watcher);
        countDownLatch.await();
        watcher.setZooKeeper(zooKeeper);
        this.zookeeper = zooKeeper;
        log.info("RegistryManager connected zookeeper");
    }

    @Override
    public synchronized void registerService(ServiceMetaData meta) {
        String path = PathConst.ROOT_PATH + "/" + meta.getInterfaceName() + "/" + meta.getGroup();
        this.checkPathAndCreate(path);
        try {
            String json = JsonUtils.bean2json(meta);
            zookeeper.create(path + "/service_", json.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            log.info("RegistryManager register service: {}", meta);
        } catch (InterruptedException | KeeperException e) {
            log.error("RegistryManager fail to register service: {}", meta, e);
        }
    }

    @Override
    public List<InetSocketAddress> getServiceAddress(Class<?> clazz, String group) {
        List<InetSocketAddress> socketAddresses = new ArrayList<>();
        Set<ServiceMetaData> metas = cache.get(clazz.getName(), group);
        if (metas != null && metas.size() > 0) {
            for (ServiceMetaData meta : metas) {
                socketAddresses.add(new InetSocketAddress(meta.getHost(), meta.getPort()));
            }
        } else {
            throw new ServiceNotFoundException(clazz.getName(), group, String.format("Service not found, interfaceName:%s, group:%s", clazz.getName(), group));
        }
        return socketAddresses;
    }

    @Override
    public void subscribe() {
        log.info("ZooKeeperRegistryManager subscribe service");
        try {
            List<String> itfChildPaths = zookeeper.getChildren(PathConst.ROOT_PATH, true);
            log.debug("Zookeeper watched children of path {}", PathConst.ROOT_PATH);
            for (String itfChildPath : itfChildPaths) {
                String itfPath = PathConst.ROOT_PATH + "/" + itfChildPath;
                List<String> groupChildPaths = zookeeper.getChildren(itfPath, true);
                log.debug("Zookeeper watched children of path {}", itfPath);
                for (String groupChildPath : groupChildPaths) {
                    String groupPath = itfPath + "/" + groupChildPath;
                    List<String> serviceChildPaths = zookeeper.getChildren(groupPath, true);
                    log.debug("Zookeeper watched children of path {}", groupPath);
                    for (String serviceChildPath : serviceChildPaths) {
                        String servicePath = groupPath + "/" + serviceChildPath;
                        //目前没有在服务运行过程中，动态修改地址的情况，因此此事件不监听
                        byte[] bytes = zookeeper.getData(servicePath, false, new Stat());
                        String json = new String(bytes);
                        ServiceMetaData meta = JsonUtils.json2bean(json, ServiceMetaData.class);
                        cache.put(meta);
                    }
                }
            }
        } catch (Exception e) {
            log.error("ZookeeperRegistryManager subscribe service error", e);
        }
    }

    /**
     * 检查节点是否存在，不存在则创建
     */
    private synchronized void checkPathAndCreate(String path) {
        try {
            String[] paths = path.split("/");
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < paths.length; i++) {
                sb.append("/").append(paths[i]);
                if (zookeeper.exists(sb.toString(), false) == null) {
                    zookeeper.create(sb.toString(), "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    log.info("RegistryManager create path {}", path);
                }
            }
        } catch (Exception e) {
            log.error("RegistryManager fail to check path {}", path, e);
        }
    }
}
