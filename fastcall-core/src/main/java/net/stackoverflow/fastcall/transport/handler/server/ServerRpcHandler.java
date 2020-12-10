package net.stackoverflow.fastcall.transport.handler.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.stackoverflow.fastcall.annotation.FastcallFallback;
import net.stackoverflow.fastcall.context.BeanContext;
import net.stackoverflow.fastcall.exception.BeanNotFoundException;
import net.stackoverflow.fastcall.transport.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 服务端业务处理
 *
 * @author wormhole
 */
public class ServerRpcHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ServerRpcHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        if (header.getType() == MessageType.BUSINESS_REQUEST.value()) {
            RpcRequest request = (RpcRequest) message.getBody();
            RpcResponse response = handlerRequest(request);
            log.trace("[L:{} R:{}] Server set response, responseId:{}, responseCode:{}", ctx.channel().localAddress(), ctx.channel().remoteAddress(), response.getId(), response.getCode());
            ctx.writeAndFlush(new Message(MessageType.BUSINESS_RESPONSE, response));
        }
        super.channelRead(ctx, msg);
    }

    private RpcResponse handlerRequest(RpcRequest request) {
        RpcResponse rpcResponse = null;

        List<Object> params = request.getParams();
        List<Class<?>> paramsType = request.getParamsType();

        Object obj = BeanContext.getBean(request.getInterfaceType());
        if (obj == null) {
            return new RpcResponse(request.getId(), -1, null, new BeanNotFoundException());
        }

        try {
            Method method = obj.getClass().getMethod(request.getMethod(), paramsType.toArray(new Class[0]));
            Object response = method.invoke(obj, params == null ? null : params.toArray());
            rpcResponse = new RpcResponse(request.getId(), 0, response, null);
        } catch (InvocationTargetException e) {
            try {
                Class<?> clazz = obj.getClass();
                Method method = clazz.getMethod(request.getMethod(), paramsType.toArray(new Class[0]));
                FastcallFallback fastcallFallback = method.getAnnotation(FastcallFallback.class);
                if (fastcallFallback != null) {
                    String fallback = fastcallFallback.method();
                    Method fallbackMethod = clazz.getMethod(fallback, paramsType.toArray(new Class[0]));
                    Object response = fallbackMethod.invoke(obj, params == null ? null : params.toArray());
                    rpcResponse = new RpcResponse(request.getId(), 0, response, null);
                } else {
                    rpcResponse = new RpcResponse(request.getId(), -1, null, e.getTargetException());
                }
            } catch (Throwable throwable) {
                rpcResponse = new RpcResponse(request.getId(), -1, null, throwable);
            }
        } catch (Throwable throwable) {
            rpcResponse = new RpcResponse(request.getId(), -1, null, throwable);
        }
        return rpcResponse;
    }
}
