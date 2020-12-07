package net.stackoverflow.fastcall.transport.handler.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.stackoverflow.fastcall.BeanContext;
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
            handlerRequest(request, ctx);
        }
        super.channelRead(ctx, msg);
    }

    private void handlerRequest(RpcRequest request, ChannelHandlerContext ctx) {
        RpcResponse rpcResponse = null;
        try {
            Object obj = BeanContext.getBean(request.getInterfaceType());
            List<Object> params = request.getParams();
            List<Class<?>> paramsType = request.getParamsType();
            Method method = obj.getClass().getMethod(request.getMethod(), paramsType.toArray(new Class[0]));
            Object response = method.invoke(obj, params == null ? null : params.toArray());
            rpcResponse = new RpcResponse(request.getId(), 0, response, null);
            log.debug("[L:{} R:{}] Server execute rpc handler success, requestId:{}", ctx.channel().localAddress(), ctx.channel().remoteAddress(), request.getId());
        } catch (Throwable e) {
            log.error("[L:{} R:{}] Server execute rpc handler fail, requestId:{}", ctx.channel().localAddress(), ctx.channel().remoteAddress(), request.getId(), e);
            if (e instanceof InvocationTargetException) {
                e = ((InvocationTargetException) e).getTargetException();
            }
            rpcResponse = new RpcResponse(request.getId(), -1, null, e);
        }
        ctx.writeAndFlush(new Message(MessageType.BUSINESS_RESPONSE, rpcResponse));
    }
}
