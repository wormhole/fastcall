package net.stackoverflow.fastcall.registry;

import java.util.*;

/**
 * 服务地址缓存
 *
 * @author wormhole
 */
public class ServiceMetaCache {

    private final Map<String, List<ServiceMetaData>> cache;

    public ServiceMetaCache() {
        this.cache = new HashMap<>();
    }

    /**
     * 获取指定缓存信息
     *
     * @param interfaceName 接口名
     * @param group         分组名
     * @return
     */
    public synchronized List<ServiceMetaData> get(String interfaceName, String group, String version) {
        List<ServiceMetaData> list = new ArrayList<>();
        List<ServiceMetaData> metaDataList = cache.get(interfaceName);
        if (metaDataList != null && metaDataList.size() > 0) {
            for (ServiceMetaData meta : metaDataList) {
                if (meta.getGroup().equals(group) && meta.getVersion().equals(version)) {
                    list.add(meta);
                }
            }
        }
        return list;
    }

    /**
     * 重置指定接口的所有缓存
     *
     * @param interfaceName 接口名
     * @param metaDataList  服务集合
     */
    public synchronized void setCache(String interfaceName, List<ServiceMetaData> metaDataList) {
        List<ServiceMetaData> list = cache.get(interfaceName);
        if (list == null) {
            list = new ArrayList<>();
            list.addAll(metaDataList);
            cache.put(interfaceName, list);
        } else {
            list.clear();
            list.addAll(metaDataList);
        }
    }

    /**
     * 重置所有缓存
     *
     * @param cache 缓存map
     */
    public synchronized void setCache(Map<String, List<ServiceMetaData>> cache) {
        for (Map.Entry<String, List<ServiceMetaData>> entry : cache.entrySet()) {
            this.setCache(entry.getKey(), entry.getValue());
        }
    }
}
