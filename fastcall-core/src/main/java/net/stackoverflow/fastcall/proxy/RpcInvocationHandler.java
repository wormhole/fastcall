package net.stackoverflow.fastcall.proxy;

import net.stackoverflow.fastcall.ConnectionManager;
import net.stackoverflow.fastcall.transport.NettyClient;
import net.stackoverflow.fastcall.transport.proto.RpcRequest;
import net.stackoverflow.fastcall.register.RegisterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.UUID;

/**
 * RpcInvocation
 *
 * @author wormhole
 */
public class RpcInvocationHandler implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(RpcInvocationHandler.class);

    private ConnectionManager connectionManager;

    private RegisterManager registerManager;

    private String group;

    public RpcInvocationHandler(ConnectionManager connectionManager, RegisterManager registerManager,String group) {
        this.connectionManager = connectionManager;
        this.registerManager = registerManager;
        this.group = group;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setId(UUID.randomUUID().toString());
        request.setClazz(method.getDeclaringClass());
        request.setMethod(method.getName());
        request.setGroup(group);
        request.setParams(Arrays.asList(args));
        request.setParamsType(Arrays.asList(method.getParameterTypes()));

        InetSocketAddress address = registerManager.getRemoteAddr(request.getGroup(), request.getClazz().getName());
        NettyClient client = connectionManager.getClient(address);
        ResponseFuture future = client.call(request);
        Object response = future.getResponse();
        return response;
    }
}
