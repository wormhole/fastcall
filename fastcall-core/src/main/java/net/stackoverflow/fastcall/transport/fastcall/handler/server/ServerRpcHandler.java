package net.stackoverflow.fastcall.transport.fastcall.handler.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.stackoverflow.fastcall.context.BeanContext;
import net.stackoverflow.fastcall.core.RpcRequestHandler;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.fastcall.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


}
