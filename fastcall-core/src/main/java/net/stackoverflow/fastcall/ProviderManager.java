package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.config.ProviderConfig;

/**
 * Provider管理类
 *
 * @author wormhole
 */
public interface ProviderManager {

    /**
     * 获取配置
     *
     * @return Provider配置
     */
    ProviderConfig getConfig();

    /**
     * 启动服务
     */
    void start();

    /**
     * 停止服务
     */
    void stop();

    /**
     * 注册服务
     *
     * @param clazz 需要暴露的接口
     * @param bean  服务bean对象
     * @param group 所属分组
     */
    void registerService(Class<?> clazz, Object bean, String group);
}
