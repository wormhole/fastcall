package net.stackoverflow.fastcall.core;

import net.stackoverflow.fastcall.ConsumerManager;
import net.stackoverflow.fastcall.DefaultConsumerManager;
import net.stackoverflow.fastcall.exception.RpcTimeoutException;
import net.stackoverflow.fastcall.serialize.SerializeManager;
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

    private final SerializeManager serializeManager;

    private final String group;

    private final String version;

    private final Long timeout;

    private final Class<?> fallback;

    public RpcInvocationHandler(ConsumerManager consumerManager, SerializeManager serializeManager, String group, String version, Long timeout, Class<?> fallback) {
        this.consumerManager = consumerManager;
        this.serializeManager = serializeManager;
        this.group = group;
        this.version = version;
        this.timeout = timeout;
        this.fallback = fallback;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ResponseFuture future = null;
        int retry = consumerManager.config().getRetry();
        while (true) {
            try {
                future = consumerManager.call(method, args, group, version);
                RpcResponse response = future.getResponse(timeout);
                if (response != null) {
                    log.trace("Method: {}, code: {}", method.getName(), response.getCode());
                    if (response.getCode() == 0) {
                        byte[] responseBytes = response.getResponseBytes();
                        Class<?> responseType = response.getResponseType();
                        Object object = serializeManager.deserialize(responseBytes, responseType);
                        return object;
                    } else {
                        byte[] throwableBytes = response.getThrowableBytes();
                        Class<?> throwableType = response.getThrowableType();
                        Throwable throwable = (Throwable) serializeManager.deserialize(throwableBytes, throwableType);
                        throw throwable;
                    }
                } else {
                    throw new RpcTimeoutException();
                }
            } catch (RpcTimeoutException exception) {
                if (retry > 0) {
                    log.warn("Proxy execute rpc timeout and retry, retry:{}, requestId:{}", retry, future.getRequestId());
                    --retry;
                } else {
                    log.error("Proxy execute rpc timeout, requestId:{}", future.getRequestId());
                    throw exception;
                }
            } catch (Throwable throwable) {
                if (fallback != Void.class) {
                    log.warn("Proxy execute fallback method, requestId:{}", future.getRequestId());
                    Object object = fallback.newInstance();
                    Object response = method.invoke(object, args);
                    return response;
                } else {
                    log.error("Proxy fail to call rpc, requestId:{}", future.getRequestId());
                    throw throwable;
                }
            } finally {
                if (future != null) {
                    consumerManager.removeFuture(future.getRequestId());
                }
            }
        }
    }
}
