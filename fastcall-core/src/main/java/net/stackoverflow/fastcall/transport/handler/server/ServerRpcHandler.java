package net.stackoverflow.fastcall.transport.handler.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.stackoverflow.fastcall.transport.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 服务端业务处理
 *
 * @author wormhole
 */
public class ServerRpcHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(ServerRpcHandler.class);

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        if (header.getType() == MessageType.BUSINESS_REQUEST.value()) {
            RpcRequest request = (RpcRequest) message.getBody();
            RpcResponse response = handlerRequest(request);
            ctx.writeAndFlush(new Message(MessageType.BUSINESS_RESPONSE, response));
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private RpcResponse handlerRequest(RpcRequest request) {
        Object obj = context.getBean(request.getInterfaceType());
        List<Object> params = request.getParams();
        List<Class<?>> paramsType = request.getParamsType();

        RpcResponse rpcResponse = null;
        try {
            Method method = obj.getClass().getMethod(request.getMethod(), paramsType.toArray(new Class[0]));
            Object response = method.invoke(obj, params.toArray());
            rpcResponse = new RpcResponse(request.getId(), 0, response, null);
            log.debug("success execute server rpc handler, request:{}", request);
        } catch (Exception e) {
            log.error("fail to execute server rpc handler, request:{}", request, e);
            rpcResponse = new RpcResponse(request.getId(), -1, null, e);
        }
        return rpcResponse;
    }
}
