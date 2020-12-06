package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.config.FastcallConfig;

import java.lang.reflect.Method;

/**
 * Fastcall对外统一外观
 *
 * @author wormhole
 */
public interface FastcallManager {

    /**
     * 设置服务提供Manager
     *
     * @param providerManager
     */
    void setProviderManager(ProviderManager providerManager);

    /**
     * 获取配置
     *
     * @return
     */
    FastcallConfig getConfig();

    /**
     * 生成代理对象
     *
     * @param clazz 接口Class对象
     * @param group 所属分组
     * @param <T>   泛型
     * @return
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
     * 注册服务（通过非注解）
     *
     * @param clazz 需要暴露的接口
     * @param bean  服务bean对象
     * @param group 所属分组
     */
    void registerService(Class<?> clazz, Object bean, String group);

    /**
     * RPC调用
     *
     * @param method 方法
     * @param args   参数
     * @param group  所属分组
     * @return
     */
    Object call(Method method, Object[] args, String group);

    /**
     * 启动服务
     */
    void start();

    /**
     * 停止服务
     */
    void stop();
}
