package net.stackoverflow.fastcall.registry.zookeeper;

import net.stackoverflow.fastcall.registry.JsonUtils;
import net.stackoverflow.fastcall.registry.ServiceMetaData;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * ZooKeeper子节点事件监听器
 *
 * @author wormhole
 */
public class ChildrenWatcher implements Watcher {

    private static final Logger log = LoggerFactory.getLogger(ChildrenWatcher.class);

    private final ServiceAddressCache cache;

    private final ZooKeeper zooKeeper;

    public ChildrenWatcher(ServiceAddressCache cache, ZooKeeper zooKeeper) {
        this.cache = cache;
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
            String path = watchedEvent.getPath();
            String[] paths = path.split("/");
            switch (paths.length) {
                case 2:
                    cache(path);
                    break;
                case 3:
                    cache(paths[2], path);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 缓存全部
     *
     * @param path zookeeper路径
     */
    private void cache(String path) {
        try {
            Map<String, Set<ServiceMetaData>> latestCache = new HashMap<>();
            List<String> itfChildPaths = zooKeeper.getChildren(path, this);
            log.debug("Zookeeper watched children of path {}", path);
            for (String itfChildPath : itfChildPaths) {
                String itfPath = path + "/" + itfChildPath;
                List<String> serviceChildPaths = zooKeeper.getChildren(itfPath, this);
                log.debug("Zookeeper watched children of path {}", itfPath);

                Set<ServiceMetaData> metaDataSet = new LinkedHashSet<>();
                latestCache.put(itfChildPath, metaDataSet);
                for (String serviceChildPath : serviceChildPaths) {
                    String servicePath = itfPath + "/" + serviceChildPath;
                    //目前没有在服务运行过程中，动态修改地址的情况，因此此事件不监听
                    byte[] bytes = zooKeeper.getData(servicePath, false, new Stat());
                    String json = new String(bytes);
                    ServiceMetaData meta = JsonUtils.json2bean(json, ServiceMetaData.class);
                    metaDataSet.add(meta);
                }
            }
            cache.reset(latestCache);
        } catch (Exception e) {
            log.error("Zookeeper fail to watched path", e);
        }
    }

    /**
     * 缓存某个接口下的服务
     *
     * @param interfaceName 接口名
     * @param path          接口的zookeeper路径
     */
    private void cache(String interfaceName, String path) {
        try {
            Set<ServiceMetaData> set = new HashSet<>();
            List<String> serviceChildPaths = zooKeeper.getChildren(path, this);
            log.debug("Zookeeper watched children of path {}", path);
            for (String serviceChildPath : serviceChildPaths) {
                String servicePath = path + "/" + serviceChildPath;
                //目前没有在服务运行过程中，动态修改地址的情况，因此此事件不监听
                byte[] bytes = zooKeeper.getData(servicePath, false, new Stat());
                String json = new String(bytes);
                ServiceMetaData meta = JsonUtils.json2bean(json, ServiceMetaData.class);
                set.add(meta);
            }
            cache.reset(interfaceName, set);
        } catch (Exception e) {
            log.error("Zookeeper fail to watched path", e);
        }
    }
}
