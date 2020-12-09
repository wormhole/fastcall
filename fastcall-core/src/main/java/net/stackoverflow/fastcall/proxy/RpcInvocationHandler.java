package net.stackoverflow.fastcall.proxy;

import net.stackoverflow.fastcall.ConsumerManager;
import net.stackoverflow.fastcall.FastcallManager;
import net.stackoverflow.fastcall.ResponseFuture;
import net.stackoverflow.fastcall.transport.proto.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * RpcInvocation类
 *
 * @author wormhole
 */
public class RpcInvocationHandler implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(RpcInvocationHandler.class);

    private final ConsumerManager consumerManager;

    private final String group;

    public RpcInvocationHandler(ConsumerManager consumerManager, String group) {
        this.consumerManager = consumerManager;
        this.group = group;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ResponseFuture future = consumerManager.call(method, args, group);
        RpcResponse response = future.getResponse();
        log.trace("Method: {}, code: {}", method.getName(), response.getCode());
        if (response.getCode() == 0) {
            return response.getResponse();
        } else {
            throw response.getThrowable();
        }
    }
}
