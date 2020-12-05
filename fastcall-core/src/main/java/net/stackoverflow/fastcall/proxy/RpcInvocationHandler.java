package net.stackoverflow.fastcall.proxy;

import net.stackoverflow.fastcall.FastcallManager;
import net.stackoverflow.fastcall.transport.ConnectionManager;
import net.stackoverflow.fastcall.exception.ConnectionInActiveException;
import net.stackoverflow.fastcall.transport.NettyClient;
import net.stackoverflow.fastcall.ResponseFuture;
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

    private FastcallManager fastcallManager;

    private final String group;

    public RpcInvocationHandler(FastcallManager fastcallManager, String group) {
        this.fastcallManager = fastcallManager;
        this.group = group;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setId(UUID.randomUUID().toString());
        request.setInterfaceType(method.getDeclaringClass());
        request.setMethod(method.getName());
        request.setGroup(group);
        request.setParams(args == null ? null : Arrays.asList(args));
        request.setParamsType(Arrays.asList(method.getParameterTypes()));

        ResponseFuture future = fastcallManager.call(request);
        return future.getResponse();
    }
}
