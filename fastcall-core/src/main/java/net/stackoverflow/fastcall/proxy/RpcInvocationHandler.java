package net.stackoverflow.fastcall.proxy;

import net.stackoverflow.fastcall.ConsumerManager;
import net.stackoverflow.fastcall.context.ResponseFuture;
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

    private final String version;

    public RpcInvocationHandler(ConsumerManager consumerManager, String group, String version) {
        this.consumerManager = consumerManager;
        this.group = group;
        this.version = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ResponseFuture future = consumerManager.call(method, args, group, version);
        RpcResponse response = future.getResponse();
        log.trace("Method: {}, code: {}", method.getName(), response.getCode());
        if (response.getCode() == 0) {
            byte[] responseBytes = response.getResponseBytes();
            Class<?> responseType = response.getResponseType();
            Object object = consumerManager.getSerializeManager().deserialize(responseBytes, responseType);
            return object;
        } else {
            byte[] throwableBytes = response.getThrowableBytes();
            Class<?> throwableType = response.getThrowableType();
            Throwable throwable = (Throwable) consumerManager.getSerializeManager().deserialize(throwableBytes, throwableType);
            throw throwable;
        }
    }
}
