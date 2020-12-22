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

/**
 * Redis注册中心管理器
 *
 * @author wormhole
 */
public class RedisRegistryManager extends AbstractRegistryManager {

    private static final Logger log = LoggerFactory.getLogger(RedisRegistryManager.class);

    private static final String ROOT_PREFIX = "fastcall:";

    private final String host;

    private final Integer port;

    private final String password;

    private final Integer timeout;

    private final JedisPool jedisPool;

    public RedisRegistryManager(String host, Integer port, String password, Integer timeout) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.timeout = timeout;
        if (password != null && password.length() > 0) {
            this.jedisPool = new JedisPool(new JedisPoolConfig(), host, port, timeout, password);
        } else {
            this.jedisPool = new JedisPool(new JedisPoolConfig(), host, port, timeout);
        }
    }

    @Override
    public void register(ServiceMetaData meta) {
        Jedis jedis = jedisPool.getResource();
        String key = ROOT_PREFIX + meta.getInterfaceName();
        String hashKey = meta.getHost() + ":" + meta.getPort();
        String json = JsonUtils.bean2json(meta);
        jedis.hset(key, hashKey, json);
        jedis.close();
    }

    @Override
    public void subscribe() {
        this.updateCache();
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
        jedisPool.close();
    }
}
