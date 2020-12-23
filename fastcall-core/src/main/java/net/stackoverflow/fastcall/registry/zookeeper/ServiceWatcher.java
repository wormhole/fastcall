package net.stackoverflow.fastcall.registry.zookeeper;

import net.stackoverflow.fastcall.registry.RegistryManager;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ZooKeeper子节点事件监听器
 *
 * @author wormhole
 */
public class ServiceWatcher implements Watcher {

    private static final Logger log = LoggerFactory.getLogger(ServiceWatcher.class);

    private final RegistryManager registryManager;

    public ServiceWatcher(RegistryManager registryManager) {
        this.registryManager = registryManager;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
            log.debug("ServiceWatcher received message, {}", watchedEvent.getPath());
            registryManager.updateCache();
            registryManager.subscribe();
        }
    }
}
