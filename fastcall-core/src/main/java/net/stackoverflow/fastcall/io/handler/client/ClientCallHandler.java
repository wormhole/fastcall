package net.stackoverflow.fastcall.io.handler.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.stackoverflow.fastcall.io.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端业务Handler
 *
 * @author wormhole
 */
public class ClientCallHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ClientCallHandler.class);

    private CallRequest request;

    public ClientCallHandler(CallRequest request) {
        this.request = request;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        if (header != null && header.getType() == MessageType.AUTH_RESPONSE.value()) {
            ctx.writeAndFlush(new Message(MessageType.BUSINESS_REQUEST, request));
        } else if (header != null && header.getType() == MessageType.BUSINESS_RESPONSE.value()) {
            CallResponse response = (CallResponse) message.getBody();
            log.info((String) response.getRet());
            ctx.close();
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
