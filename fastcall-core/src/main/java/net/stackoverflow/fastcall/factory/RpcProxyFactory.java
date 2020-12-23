package net.stackoverflow.fastcall.factory;

import net.stackoverflow.fastcall.ConsumerManager;
import net.stackoverflow.fastcall.core.RpcInvocationHandler;
import net.stackoverflow.fastcall.serialize.SerializeManager;

import java.lang.reflect.Proxy;

/**
 * 代理对象工厂类
 *
 * @author wormhole
 */
public class RpcProxyFactory {

    /**
     * 工厂类方法
     *
     * @param clazz            接口Class对象
     * @param consumerManager  服务消费Manager
     * @param serializeManager 序列化Manager
     * @param group            所属分组
     * @param version          版本号
     * @param timeout          rpc调用超时时间
     * @param fallback         服务降级
     * @param <T>              泛型
     * @return
     */
    public static <T> T create(Class<T> clazz, String group, String version, Long timeout, Class<?> fallback, ConsumerManager consumerManager, SerializeManager serializeManager) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new RpcInvocationHandler(consumerManager, serializeManager, group, version, timeout, fallback));
    }
}
