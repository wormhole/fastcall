package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.config.ConsumerConfig;
import net.stackoverflow.fastcall.transport.NettyClient;
import net.stackoverflow.fastcall.transport.proto.RpcRequest;
import net.stackoverflow.fastcall.transport.proto.RpcResponse;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * 消费者管理器
 *
 * @author wormhole
 */
public interface ConsumerManager {

    /**
     * 获取配置信息
     *
     * @return
     */
    ConsumerConfig getConfig();

    /**
     * RPC调用
     *
     * @param method 方法
     * @param args   参数
     * @param group  所属分组
     * @return
     */
    ResponseFuture call(Method method, Object[] args, String group);

    /**
     * 订阅服务
     */
    void subscribe();
}
