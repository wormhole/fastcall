package net.stackoverflow.fastcall.factory;

import net.stackoverflow.fastcall.FastcallManager;
import net.stackoverflow.fastcall.core.RpcInvocationHandler;

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
     * @param fastcallManager fastcallManager对象
     * @param clazz           接口Class对象
     * @param group           所属分组
     * @param version         版本号
     * @param timeout         rpc调用超时时间
     * @param fallback        服务降级
     * @param <T>             泛型
     * @return
     */
    public static <T> T create(FastcallManager fastcallManager, Class<T> clazz, String group, String version, Long timeout, Class<?> fallback) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new RpcInvocationHandler(fastcallManager, group, version, timeout, fallback));
    }
}