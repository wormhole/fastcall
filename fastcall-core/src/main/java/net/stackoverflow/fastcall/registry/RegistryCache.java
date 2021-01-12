package net.stackoverflow.fastcall.registry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务地址缓存
 *
 * @author wormhole
 */
public class RegistryCache {

    private final Map<String, List<ServiceDefinition>> cache;

    public RegistryCache() {
        this.cache = new ConcurrentHashMap<>();
    }

    /**
     * 获取指定缓存信息
     *
     * @param interfaceName 接口名
     * @param group         分组名
     * @return
     */
    public synchronized List<ServiceDefinition> get(String interfaceName, String group, String version) {
        List<ServiceDefinition> list = new ArrayList<>();
        List<ServiceDefinition> definitions = cache.get(interfaceName);
        if (definitions != null && definitions.size() > 0) {
            for (ServiceDefinition definition : definitions) {
                if (definition.getGroup().equals(group) && definition.getVersion().equals(version)) {
                    list.add(definition);
                }
            }
        }
        return list;
    }

    /**
     * 重置指定接口的所有缓存
     *
     * @param interfaceName 接口名
     * @param definitions   服务集合
     */
    public synchronized void setCache(String interfaceName, List<ServiceDefinition> definitions) {
        List<ServiceDefinition> list = cache.get(interfaceName);
        if (list == null) {
            list = new ArrayList<>();
            list.addAll(definitions);
            cache.put(interfaceName, list);
        } else {
            list.clear();
            list.addAll(definitions);
        }
    }

    /**
     * 重置所有缓存
     *
     * @param cache 缓存map
     */
    public synchronized void setCache(Map<String, List<ServiceDefinition>> cache) {
        for (Map.Entry<String, List<ServiceDefinition>> entry : cache.entrySet()) {
            this.setCache(entry.getKey(), entry.getValue());
        }
    }
}
