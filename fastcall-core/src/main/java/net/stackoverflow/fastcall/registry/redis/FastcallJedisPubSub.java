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
public class FastcallJedisPubSub extends JedisPubSub {

    private static final Logger log = LoggerFactory.getLogger(FastcallJedisPubSub.class);

    private final RegistryManager registryManager;

    public FastcallJedisPubSub(RegistryManager registryManager) {
        this.registryManager = registryManager;
    }

    @Override
    public void onMessage(String channel, String message) {
        log.debug("JedisPubSub receive message {}", message);
        registryManager.updateCache();
    }
}
