package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.config.ConsumerConfig;

import java.lang.reflect.Method;

/**
 * 消费者管理类
 *
 * @author wormhole
 */
public interface ConsumerManager {

    /**
     * 获取配置信息
     *
     * @return Consumer配置类
     */
    ConsumerConfig getConfig();

    /**
     * RPC调用
     *
     * @param method 方法
     * @param args   参数
     * @param group  所属分组
     * @return ResponseFuture对象
     */
    ResponseFuture call(Method method, Object[] args, String group);

    /**
     * 订阅服务
     */
    void subscribe();

    /**
     * 关闭客户端所有连接
     */
    void close();
}
