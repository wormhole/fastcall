package net.stackoverflow.fastcall.transport.fastcall.handler.client;

import io.netty.channel.*;
import net.stackoverflow.fastcall.core.ResponseFutureContext;
import net.stackoverflow.fastcall.transport.fastcall.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端Rpc Handler
 *
 * @author wormhole
 */
public class ClientRpcHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ClientRpcHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        if (header.getType() == MessageType.BUSINESS_RESPONSE.value()) {
            RpcResponse response = (RpcResponse) message.getBody();
            ResponseFutureContext context = ResponseFutureContext.getInstance();
            context.setResponse(response);
            log.trace("[L:{} R:{}] Client set response, responseId:{}, responseCode:{}", ctx.channel().localAddress(), ctx.channel().remoteAddress(), response.getId(), response.getCode());
        }
        super.channelRead(ctx, msg);
    }
}
