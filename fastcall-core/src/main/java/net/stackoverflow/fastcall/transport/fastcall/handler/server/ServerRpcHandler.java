package net.stackoverflow.fastcall.transport.fastcall.handler.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.stackoverflow.fastcall.core.BeanContext;
import net.stackoverflow.fastcall.exception.BeanNotFoundException;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.fastcall.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 服务端业务处理
 *
 * @author wormhole
 */
public class ServerRpcHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ServerRpcHandler.class);

    private final SerializeManager serializeManager;

    private final ExecutorService rpcExecutorService;

    public ServerRpcHandler(SerializeManager serializeManager, ExecutorService rpcExecutorService) {
        this.serializeManager = serializeManager;
        this.rpcExecutorService = rpcExecutorService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        if (header.getType() == MessageType.BUSINESS_REQUEST.value()) {
            RpcRequest request = (RpcRequest) message.getBody();
            Runnable runnable = new RpcRequestHandler(request, ctx.channel(), serializeManager);
            rpcExecutorService.execute(runnable);
        }
        super.channelRead(ctx, msg);
    }

    private static class RpcRequestHandler implements Runnable{
        private static final Logger log = LoggerFactory.getLogger(RpcRequestHandler.class);

        private final RpcRequest request;

        private final Channel channel;

        private final SerializeManager serializeManager;

        public RpcRequestHandler(RpcRequest request, Channel channel, SerializeManager serializeManager) {
            this.request = request;
            this.channel = channel;
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

            BeanContext context = BeanContext.getInstance();
            Object obj = context.getBean(request.getInterfaceType(), request.getGroup(), request.getVersion());
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
}
