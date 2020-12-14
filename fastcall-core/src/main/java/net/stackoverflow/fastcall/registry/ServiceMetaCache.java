package net.stackoverflow.fastcall.registry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务地址缓存
 *
 * @author wormhole
 */
public class ServiceMetaCache {

    private Map<String, List<ServiceMetaData>> cache;

    public ServiceMetaCache() {
        this.cache = new ConcurrentHashMap<>();
    }

    /**
     * 获取指定缓存信息
     *
     * @param interfaceName 接口名
     * @param group         分组名
     * @return
     */
    public List<ServiceMetaData> get(String interfaceName, String group, String version) {
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
    public void reset(String interfaceName, List<ServiceMetaData> metaDataList) {
        cache.put(interfaceName, metaDataList);
    }

    /**
     * 重置所有缓存
     *
     * @param cache 缓存map
     */
    public void reset(Map<String, List<ServiceMetaData>> cache) {
        this.cache = cache;
    }
}
