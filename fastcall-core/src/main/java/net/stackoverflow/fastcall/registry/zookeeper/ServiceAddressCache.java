package net.stackoverflow.fastcall.registry.zookeeper;

import net.stackoverflow.fastcall.registry.ServiceMetaData;

import java.util.*;

/**
 * 服务地址缓存
 *
 * @author wormhole
 */
public class ServiceAddressCache {

    private Map<String, Map<String, Set<ServiceMetaData>>> cache;

    public ServiceAddressCache() {
        this.cache = new HashMap<>();
    }

    /**
     * 缓存远程服务信息
     *
     * @param meta
     */
    public synchronized void put(ServiceMetaData meta) {
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

    /**
     * 获取指定缓存信息
     *
     * @param interfaceName 接口名
     * @param group         分组名
     * @return
     */
    public synchronized Set<ServiceMetaData> get(String interfaceName, String group) {
        Map<String, Set<ServiceMetaData>> groupMap = cache.get(interfaceName);
        if (groupMap != null) {
            return groupMap.get(group);
        } else {
            return null;
        }
    }

    /**
     * 清空缓存
     */
    public synchronized void clear() {
        this.cache.clear();
    }

    /**
     * 重新设置缓存
     *
     * @param cache
     */
    public synchronized void clearAndSet(Map<String, Map<String, Set<ServiceMetaData>>> cache) {
        this.cache = cache;
    }
}
