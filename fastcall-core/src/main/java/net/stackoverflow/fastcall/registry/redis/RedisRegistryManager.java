package net.stackoverflow.fastcall.registry.redis;

import net.stackoverflow.fastcall.registry.AbstractRegistryManager;
import net.stackoverflow.fastcall.registry.ServiceMetaData;
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
 * Redis注册中心管理器
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
    }

    @Override
    public void register(ServiceMetaData meta) {
        Jedis jedis = jedisPool.getResource();
        String key = ROOT_PREFIX + meta.getInterfaceName();
        String hashKey = meta.getHost() + ":" + meta.getPort();
        String json = JsonUtils.bean2json(meta);
        jedis.hset(key, hashKey, json);
        jedis.publish(CHANNEL, meta.getInterfaceName());
        jedis.close();
        log.info("RegistryManager register service: {}", meta);
    }

    @Override
    public void subscribe() {
        Jedis jedis = jedisPool.getResource();
        executorService.execute(() -> jedis.subscribe(new FastcallJedisPubSub(this), CHANNEL));
    }

    @Override
    public void updateCache() {
        Map<String, List<ServiceMetaData>> latestCache = new ConcurrentHashMap<>();
        Jedis jedis = jedisPool.getResource();
        Set<String> keys = jedis.keys(ROOT_PREFIX + "*");
        for (String key : keys) {
            List<ServiceMetaData> metaDataList = new ArrayList<>();
            latestCache.put(key.substring(9), metaDataList);

            Map<String, String> map = jedis.hgetAll(key);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String value = entry.getValue();
                ServiceMetaData metaData = JsonUtils.json2bean(value, ServiceMetaData.class);
                metaDataList.add(metaData);
            }
        }
        jedis.close();
        cache.setCache(latestCache);
    }

    @Override
    public void close() {
        executorService.shutdown();
        jedisPool.close();
    }
}
