package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.config.FastcallConfig;

/**
 * Fastcall对外统一外观
 *
 * @author wormhole
 */
public interface FastcallManager {

    /**
     * 获取配置
     *
     * @return fastcall配置
     */
    FastcallConfig config();

    /**
     * 生成代理对象
     *
     * @param clazz    接口Class对象
     * @param group    所属分组
     * @param version  版本号
     * @param timeout  rpc调用超时时间
     * @param fallback 服务降级
     * @param <T>      泛型
     * @return 代理对象
     */
    <T> T createProxy(Class<T> clazz, String group, String version, Long timeout, Class<?> fallback);

    /**
     * 注册服务（通过注解）
     *
     * @param clazz 需要暴露的接口
     * @param bean  服务bean对象
     */
    void register(Class<?> clazz, Object bean);

    /**
     * 启动服务
     */
    void start();

    /**
     * 停止服务
     */
    void stop();
}
