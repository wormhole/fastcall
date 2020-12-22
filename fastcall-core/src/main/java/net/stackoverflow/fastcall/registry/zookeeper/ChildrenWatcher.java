package net.stackoverflow.fastcall.registry.zookeeper;

import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.util.JsonUtils;
import net.stackoverflow.fastcall.registry.RegistryCache;
import net.stackoverflow.fastcall.registry.ServiceMetaData;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ZooKeeper子节点事件监听器
 *
 * @author wormhole
 */
public class ChildrenWatcher implements Watcher {

    private static final Logger log = LoggerFactory.getLogger(ChildrenWatcher.class);

    private final RegistryManager registryManager;

    public ChildrenWatcher(RegistryManager registryManager) {
        this.registryManager = registryManager;
    }

    @Override
    public synchronized void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
            log.debug("Zookeeper receive message");
            registryManager.updateCache();
            registryManager.subscribe();
        }
    }
}
