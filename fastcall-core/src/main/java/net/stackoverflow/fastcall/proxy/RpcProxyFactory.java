package net.stackoverflow.fastcall.proxy;

import net.stackoverflow.fastcall.ConnectionManager;
import net.stackoverflow.fastcall.register.RegisterManager;

import java.lang.reflect.Proxy;

/**
 * 代理对象工厂类
 *
 * @author wormhole
 */
public class RpcProxyFactory {

    public static <T> T create(Class<T> clazz, ConnectionManager connectionManager, RegisterManager registerManager, String group) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new RpcInvocationHandler(connectionManager, registerManager, group));
    }
}
