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
import java.util.Arrays;

/**
 * RpcInvocation
 *
 * @author wormhole
 */
public class RpcInvocationHandler implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(RpcInvocationHandler.class);

    private RegisterManager registerManager;

    private SerializeManager serializeManager;

    public RpcInvocationHandler(RegisterManager registerManager, SerializeManager serializeManager) {
        this.registerManager = registerManager;
        this.serializeManager = serializeManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String group = "group-1";
        RpcRequest request = new RpcRequest();
        request.setClazz(method.getDeclaringClass());
        request.setMethod(method.getName());
        request.setGroup(group);
        request.setParams(Arrays.asList(args));
        InetSocketAddress address = registerManager.getRemoteAddr(group, method.getDeclaringClass().getName());
        FastcallClient client = new FastcallClient(address.getHostName(), address.getPort(), 60, serializeManager);
        ResponseFuture future = client.call(request);
        Object ret = future.getResponse();
        return ret;
    }
}
