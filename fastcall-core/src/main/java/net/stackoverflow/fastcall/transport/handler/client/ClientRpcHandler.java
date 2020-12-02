package net.stackoverflow.fastcall.transport.handler.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.stackoverflow.fastcall.proxy.ResponseFuture;
import net.stackoverflow.fastcall.transport.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端业务Handler
 *
 * @author wormhole
 */
public class ClientRpcHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ClientRpcHandler.class);

    private RpcRequest request;

    private ResponseFuture future;

    public ClientRpcHandler(RpcRequest request, ResponseFuture future) {
        this.request = request;
        this.future = future;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        if (header != null && header.getType() == MessageType.AUTH_RESPONSE.value()) {
            ctx.writeAndFlush(new Message(MessageType.BUSINESS_REQUEST, request));
        } else if (header != null && header.getType() == MessageType.BUSINESS_RESPONSE.value()) {
            RpcResponse response = (RpcResponse) message.getBody();
            future.setResponse(response.getRet());
            ctx.close();
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
