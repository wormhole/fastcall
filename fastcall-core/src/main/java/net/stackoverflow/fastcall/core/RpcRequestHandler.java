package net.stackoverflow.fastcall.core;

import io.netty.channel.Channel;
import net.stackoverflow.fastcall.context.BeanContext;
import net.stackoverflow.fastcall.exception.BeanNotFoundException;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.proto.Message;
import net.stackoverflow.fastcall.transport.proto.MessageType;
import net.stackoverflow.fastcall.transport.proto.RpcRequest;
import net.stackoverflow.fastcall.transport.proto.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * rpc请求处理线程
 *
 * @author wormhole
 */
public class RpcRequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RpcRequestHandler.class);

    private final RpcRequest request;

    private final Channel channel;

    private final BeanContext beanContext;

    private final SerializeManager serializeManager;

    public RpcRequestHandler(RpcRequest request, Channel channel, BeanContext beanContext, SerializeManager serializeManager) {
        this.request = request;
        this.channel = channel;
        this.beanContext = beanContext;
        this.serializeManager = serializeManager;
    }

    @Override
    public void run() {
        RpcResponse response = handlerRequest(request);
        log.trace("[L:{} R:{}] RpcRequestHandler set response, responseId:{}, responseCode:{}", channel.localAddress(), channel.remoteAddress(), response.getId(), response.getCode());
        channel.writeAndFlush(new Message(MessageType.BUSINESS_RESPONSE, response));
    }

    private RpcResponse handlerRequest(RpcRequest request) {
        RpcResponse rpcResponse = null;

        List<Object> params = request.getParams();
        List<Class<?>> paramsType = request.getParamsType();

        Object obj = beanContext.getBean(request.getInterfaceType(), request.getGroup(), request.getVersion());
        if (obj == null) {
            return new RpcResponse(request.getId(), -1, null, null, BeanNotFoundException.class, serializeManager.serialize(new BeanNotFoundException()));
        }

        try {
            Method method = obj.getClass().getMethod(request.getMethod(), paramsType.toArray(new Class[0]));
            Object response = method.invoke(obj, params == null ? null : params.toArray());
            rpcResponse = new RpcResponse(request.getId(), 0, response.getClass(), serializeManager.serialize(response), null, null);
        } catch (InvocationTargetException e) {
            rpcResponse = new RpcResponse(request.getId(), -1, null, null, e.getTargetException().getClass(), serializeManager.serialize(e.getTargetException()));
        } catch (Throwable throwable) {
            rpcResponse = new RpcResponse(request.getId(), -1, null, null, throwable.getClass(), serializeManager.serialize(throwable));
        }
        return rpcResponse;
    }
}
