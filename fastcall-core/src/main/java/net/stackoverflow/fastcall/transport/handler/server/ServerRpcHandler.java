package net.stackoverflow.fastcall.transport.handler.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.stackoverflow.fastcall.transport.proto.*;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 服务端业务处理
 *
 * @author wormhole
 */
public class ServerRpcHandler extends ChannelInboundHandlerAdapter {

    private ApplicationContext context;

    public ServerRpcHandler(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        if (header != null && header.getType() == MessageType.BUSINESS_REQUEST.value()) {
            RpcRequest request = (RpcRequest) message.getBody();
            Object obj = context.getBean(request.getClazz());
            List<Object> params = request.getParams();
            List<Class<?>> paramsType = request.getParamsType();
            Method method = obj.getClass().getMethod(request.getMethod(), paramsType.toArray(new Class[0]));
            Object ret = method.invoke(obj, params.toArray());
            ctx.writeAndFlush(new Message(MessageType.BUSINESS_RESPONSE, new RpcResponse(ret)));
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
