package net.stackoverflow.fastcall.proxy;

import net.stackoverflow.fastcall.transport.FastcallClient;
import net.stackoverflow.fastcall.transport.proto.CallRequest;
import net.stackoverflow.fastcall.register.RegisterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class FastcallProxy implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(FastcallProxy.class);

    private RegisterManager manager;

    public FastcallProxy(RegisterManager manager) {
        this.manager = manager;
    }

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
            request.setParameters(Arrays.asList((String) args[0]));
            InetSocketAddress address = manager.getRemoteAddr(group, className);
            FastcallClient client = new FastcallClient(address.getHostName(), address.getPort(), 60, request);
            client.connect();
            return "proxy interface";
        }
    }
}
