package net.stackoverflow.fastcall.proxy;

import net.stackoverflow.fastcall.transport.FastcallClient;

import java.lang.reflect.Proxy;

/**
 * 代理对象工厂类
 *
 * @author wormhole
 */
public class RpcProxyFactory {

    public static <T> T create(Class<T> clazz, FastcallClient client) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new RpcInvocationHandler(client));
    }
}
