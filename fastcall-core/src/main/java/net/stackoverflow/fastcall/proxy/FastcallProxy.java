package net.stackoverflow.fastcall.proxy;

import net.stackoverflow.fastcall.io.proto.CallRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class FastcallProxy implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(FastcallProxy.class);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            Object ret = method.invoke(this, args);
            return ret;
        } else {
            String className = method.getDeclaringClass().getName();
            String methodName = method.getName();
            String group = "group-1";
            CallRequest request = new CallRequest();
            request.setClassName(className);
            request.setMethod(methodName);
            request.setGroup(group);
            request.setParameters(Arrays.asList(args));
            return "proxy interface";
        }
    }
}
