package net.stackoverflow.fastcall.registry.redis;

import net.stackoverflow.fastcall.registry.AbstractRegistryManager;
import net.stackoverflow.fastcall.registry.ServiceDefinition;
import net.stackoverflow.fastcall.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Redis注册中心Manager
 *
 * @author wormhole
 */
public class RedisRegistryManager extends AbstractRegistryManager {

    private static final Logger log = LoggerFactory.getLogger(RedisRegistryManager.class);

    private static final String ROOT_PREFIX = "fastcall:";

    private static final String CHANNEL = "fastcall";

    private final JedisPool jedisPool;

    private final ExecutorService executorService;

    public RedisRegistryManager(String host, Integer port, String password, Integer timeout) {
        this.executorService = Executors.newSingleThreadExecutor();
        if (password != null && password.length() > 0) {
            this.jedisPool = new JedisPool(new JedisPoolConfig(), host, port, timeout, password);
        } else {
            this.jedisPool = new JedisPool(new JedisPoolConfig(), host, port, timeout);
        }
        this.updateCache();
        this.subscribe();
    }

    @Override
    public void register(ServiceDefinition definition) {
        Jedis jedis = jedisPool.getResource();
        String key = ROOT_PREFIX + definition.getInterfaceName();
        String hashKey = definition.getHost() + ":" + definition.getPort();
        String json = JsonUtils.bean2json(definition);
        jedis.hset(key, hashKey, json);
        jedis.publish(CHANNEL, definition.getInterfaceName());
        jedis.close();
        log.info("RegistryManager register service: {}", definition);
    }

    @Override
    public synchronized void subscribe() {
        Jedis jedis = jedisPool.getResource();
        executorService.execute(() -> jedis.subscribe(new ServiceJedisPubSub(this), CHANNEL));
    }

    @Override
    public synchronized void updateCache() {
        Map<String, List<ServiceDefinition>> latestCache = new ConcurrentHashMap<>();
        Jedis jedis = jedisPool.getResource();
        Set<String> keys = jedis.keys(ROOT_PREFIX + "*");
        for (String key : keys) {
            List<ServiceDefinition> definitions = new ArrayList<>();
            latestCache.put(key.substring(9), definitions);

            Map<String, String> map = jedis.hgetAll(key);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String value = entry.getValue();
                ServiceDefinition definition = JsonUtils.json2bean(value, ServiceDefinition.class);
                definitions.add(definition);
            }
        }
        jedis.close();
        cache.setCache(latestCache);
    }

    @Override
    public void close() {
        executorService.shutdown();
        jedisPool.close();
        log.info("RegistryManager closed");
    }
}
