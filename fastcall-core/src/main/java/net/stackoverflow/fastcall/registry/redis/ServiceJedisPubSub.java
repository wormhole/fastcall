package net.stackoverflow.fastcall.registry.redis;

import net.stackoverflow.fastcall.registry.RegistryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

/**
 * redis发布订阅回调
 *
 * @author wormhole
 */
public class ServiceJedisPubSub extends JedisPubSub {

    private static final Logger log = LoggerFactory.getLogger(ServiceJedisPubSub.class);

    private final RegistryManager registryManager;

    public ServiceJedisPubSub(RegistryManager registryManager) {
        this.registryManager = registryManager;
    }

    @Override
    public void onMessage(String channel, String message) {
        log.debug("ServiceJedisPubSub received message, {}", message);
        registryManager.updateCache();
    }
}
