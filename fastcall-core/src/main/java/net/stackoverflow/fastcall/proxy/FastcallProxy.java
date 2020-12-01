package net.stackoverflow.fastcall.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class FastcallProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            System.out.println("before");
            Object ret = method.invoke(this, args);
            System.out.println("after");
            return ret;
        } else {
            return "proxy interface";
        }
    }
}
