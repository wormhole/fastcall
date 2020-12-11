package net.stackoverflow.fastcall.proxy;

import net.stackoverflow.fastcall.ConsumerManager;

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
     * @param clazz           接口Class对象
     * @param consumerManager Consumer管理对象
     * @param group           所属分组
     * @param version         版本号
     * @param <T>             泛型
     * @return
     */
    public static <T> T create(Class<T> clazz, String group, String version, ConsumerManager consumerManager) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new RpcInvocationHandler(consumerManager, group, version));
    }
}
