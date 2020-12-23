package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.config.ConsumerConfig;
import net.stackoverflow.fastcall.core.ResponseFuture;
import net.stackoverflow.fastcall.serialize.SerializeManager;

import java.lang.reflect.Method;

/**
 * 服务消费Manager接口
 *
 * @author wormhole
 */
public interface ConsumerManager {

    /**
     * 获取配置信息
     *
     * @return Consumer配置类
     */
    ConsumerConfig config();

    /**
     * RPC调用
     *
     * @param method  方法
     * @param args    参数
     * @param group   所属分组
     * @param version 版本号
     * @return ResponseFuture对象
     */
    ResponseFuture call(Method method, Object[] args, String group, String version);

    /**
     * 移除ResponseFuture
     *
     * @param requestId 唯一标识
     */
    void removeFuture(String requestId);

    /**
     * 关闭客户端所有连接
     */
    void close();
}
