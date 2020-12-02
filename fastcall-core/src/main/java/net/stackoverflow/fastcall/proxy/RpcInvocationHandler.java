package net.stackoverflow.fastcall.proxy;

import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.FastcallClient;
import net.stackoverflow.fastcall.transport.proto.RpcRequest;
import net.stackoverflow.fastcall.register.RegisterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * RpcInvocation
 *
 * @author wormhole
 */
public class RpcInvocationHandler implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(RpcInvocationHandler.class);

    private FastcallClient client;

    public RpcInvocationHandler(FastcallClient client) {
        this.client = client;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setId(UUID.randomUUID().toString());
        request.setClazz(method.getDeclaringClass());
        request.setMethod(method.getName());
        request.setGroup("group-1");
        request.setParams(Arrays.asList(args));
        request.setParamsType(Arrays.asList(method.getParameterTypes()));
        ResponseFuture future = client.call(request);
        Object response = future.getResponse();
        return response;
    }
}
