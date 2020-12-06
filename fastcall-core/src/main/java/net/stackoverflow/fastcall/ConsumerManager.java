package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.config.ConsumerConfig;
import net.stackoverflow.fastcall.transport.NettyClient;
import net.stackoverflow.fastcall.transport.proto.RpcRequest;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * 消费者管理器
 *
 * @author wormhole
 */
public interface ConsumerManager {

    ConsumerConfig getConfig();

    /**
     * RPC调用
     *
     * @param method 方法
     * @param args   参数
     * @param group  所属分组
     * @return
     */
    Object call(Method method, Object[] args, String group);
}
