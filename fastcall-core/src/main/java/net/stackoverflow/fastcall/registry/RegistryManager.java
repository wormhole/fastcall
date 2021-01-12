package net.stackoverflow.fastcall.registry;

import net.stackoverflow.fastcall.exception.ServiceNotFoundException;

import java.util.List;

/**
 * 注册中心管理类
 *
 * @author wormhole
 */
public interface RegistryManager {

    /**
     * 注册服务元数据
     *
     * @param definition 服务定义
     */
    void register(ServiceDefinition definition);

    /**
     * 获取服务
     *
     * @param clazz   接口Class对象
     * @param group   所属分组
     * @param version 版本号
     * @return
     */
    List<ServiceDefinition> getService(Class<?> clazz, String group, String version) throws ServiceNotFoundException;

    /**
     * 订阅服务
     */
    void subscribe();

    /**
     * 更新缓存
     */
    void updateCache();

    /**
     * 关闭连接
     */
    void close();
}
