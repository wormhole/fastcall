package net.stackoverflow.fastcall.transport.handler.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.stackoverflow.fastcall.transport.proto.Message;
import net.stackoverflow.fastcall.transport.proto.Header;
import net.stackoverflow.fastcall.transport.proto.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端认证handler
 *
 * @author wormhole
 */
public class ClientAuthHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ClientAuthHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Message request = new Message(MessageType.AUTH_REQUEST);
        ctx.writeAndFlush(request);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        if (header.getType() == MessageType.AUTH_RESPONSE.value()) {
            byte body = (byte) message.getBody();
            if (!isAuthSuccess(body)) {
                ctx.close();
                log.error("[L:{} R:{}] Client fail to auth and closed", ctx.channel().localAddress(), ctx.channel().remoteAddress());
            } else {
                log.debug("[L:{} R:{}] Client auth success", ctx.channel().localAddress(), ctx.channel().remoteAddress());
            }
        }
        super.channelRead(ctx, msg);
    }

    private boolean isAuthSuccess(byte body) {
        return body == (byte) 0;
    }
}
