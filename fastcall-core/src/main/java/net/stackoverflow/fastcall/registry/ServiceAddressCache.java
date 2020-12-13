package net.stackoverflow.fastcall.registry;

import java.util.*;

/**
 * 服务地址缓存
 *
 * @author wormhole
 */
public class ServiceAddressCache {

    private Map<String, Set<ServiceMetaData>> cache;

    public ServiceAddressCache() {
        this.cache = new HashMap<>();
    }

    /**
     * 缓存远程服务信息
     *
     * @param meta
     */
    public synchronized void put(ServiceMetaData meta) {
        Set<ServiceMetaData> metaDataSet = cache.get(meta.getInterfaceName());
        if (metaDataSet == null) {
            metaDataSet = new LinkedHashSet<>();
            metaDataSet.add(meta);
            cache.put(meta.getInterfaceName(), metaDataSet);
        } else {
            metaDataSet.add(meta);
        }
    }

    /**
     * 获取指定缓存信息
     *
     * @param interfaceName 接口名
     * @param group         分组名
     * @return
     */
    public synchronized Set<ServiceMetaData> get(String interfaceName, String group, String version) {
        Set<ServiceMetaData> set = new LinkedHashSet<>();
        Set<ServiceMetaData> metaDataSet = cache.get(interfaceName);
        if (metaDataSet != null && metaDataSet.size() > 0) {
            for (ServiceMetaData meta : metaDataSet) {
                if (meta.getGroup().equals(group) && meta.getVersion().equals(version)) {
                    set.add(meta);
                }
            }
        }
        return set;
    }

    /**
     * 重置指定接口的所有缓存
     *
     * @param interfaceName 接口名
     * @param metaDataSet   服务集合
     */
    public synchronized void reset(String interfaceName, Set<ServiceMetaData> metaDataSet) {
        cache.put(interfaceName, metaDataSet);
    }

    /**
     * 重置所有缓存
     *
     * @param cache 缓存map
     */
    public synchronized void reset(Map<String, Set<ServiceMetaData>> cache) {
        this.cache = cache;
    }
}
