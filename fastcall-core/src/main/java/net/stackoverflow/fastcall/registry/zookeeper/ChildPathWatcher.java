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
import java.util.concurrent.CountDownLatch;

/**
 * ZooKeeper子节点事件Watcher
 *
 * @author wormhole
 */
public class ChildPathWatcher implements Watcher {

    private static final Logger log = LoggerFactory.getLogger(ChildPathWatcher.class);

    private final CountDownLatch countDownLatch;

    private ServiceCache cache;

    private ZooKeeper zooKeeper;

    public ChildPathWatcher(CountDownLatch countDownLatch, ServiceCache cache, ZooKeeper zooKeeper) {
        this.countDownLatch = countDownLatch;
        this.cache = cache;
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
            countDownLatch.countDown();
        } else if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
            try {
                Map<String, Map<String, Set<ServiceMetaData>>> latestCache = new HashMap<>();

                List<String> itfChildPaths = zooKeeper.getChildren(PathConst.ROOT_PATH, true);
                for (String itfChildPath : itfChildPaths) {
                    String itfPath = PathConst.ROOT_PATH + "/" + itfChildPath;
                    List<String> groupChildPaths = zooKeeper.getChildren(itfPath, true);
                    for (String groupChildPath : groupChildPaths) {
                        String groupPath = itfPath + "/" + groupChildPath;
                        List<String> serviceChildPaths = zooKeeper.getChildren(groupPath, true);
                        for (String serviceChildPath : serviceChildPaths) {
                            String servicePath = groupPath + "/" + serviceChildPath;
                            //目前没有在服务运行过程中，动态修改地址的情况，因此此事件不监听
                            byte[] bytes = zooKeeper.getData(servicePath, false, new Stat());
                            String json = new String(bytes);
                            ServiceMetaData meta = JsonUtils.json2bean(json, ServiceMetaData.class);
                            put(latestCache, meta);
                        }
                    }
                }

                cache.clearAndSet(latestCache);
            } catch (Exception e) {
                log.error("ChildPathWatcher subscribe error", e);
            }
        }
    }

    /**
     * 将服务信息添加进缓存
     *
     * @param cache
     * @param meta
     */
    private void put(Map<String, Map<String, Set<ServiceMetaData>>> cache, ServiceMetaData meta) {
        Map<String, Set<ServiceMetaData>> groupMap = cache.get(meta.getInterfaceName());
        if (groupMap == null) {
            groupMap = new HashMap<>();
            Set<ServiceMetaData> metaDataSet = new LinkedHashSet<>();
            metaDataSet.add(meta);
            groupMap.put(meta.getGroup(), metaDataSet);
            cache.put(meta.getInterfaceName(), groupMap);
        } else {
            Set<ServiceMetaData> metaDataSet = groupMap.get(meta.getGroup());
            if (metaDataSet == null) {
                metaDataSet = new LinkedHashSet<>();
                metaDataSet.add(meta);
                groupMap.put(meta.getGroup(), metaDataSet);
            } else {
                metaDataSet.add(meta);
            }
        }
    }
}
