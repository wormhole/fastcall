package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.config.FastcallConfig;

/**
 * Fastcall对外统一外观
 *
 * @author wormhole
 */
public interface FastcallManager {

    /**
     * 设置服务提供Manager
     *
     * @param providerManager provider管理类实现
     */
    void setProviderManager(ProviderManager providerManager);

    /**
     * 获取配置
     *
     * @return fastcall配置
     */
    FastcallConfig getConfig();

    /**
     * 生成代理对象
     *
     * @param clazz 接口Class对象
     * @param group 所属分组
     * @param <T>   泛型
     * @return 代理对象
     */
    <T> T createProxy(Class<T> clazz, String group);

    /**
     * 注册服务（通过注解）
     *
     * @param clazz 需要暴露的接口
     * @param bean  服务bean对象
     */
    void registerService(Class<?> clazz, Object bean);

    /**
     * 启动服务
     */
    void start();

    /**
     * 停止服务
     */
    void stop();
}
