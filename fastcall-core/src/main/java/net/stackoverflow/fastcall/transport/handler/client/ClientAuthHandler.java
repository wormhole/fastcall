package net.stackoverflow.fastcall.transport.handler.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.stackoverflow.fastcall.transport.proto.Message;
import net.stackoverflow.fastcall.transport.proto.Header;
import net.stackoverflow.fastcall.transport.proto.MessageType;

/**
 * 客户端认证handler
 *
 * @author wormhole
 */
public class ClientAuthHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
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
            }
        }
        super.channelRead(ctx, msg);
    }

    private boolean isAuthSuccess(byte body) {
        return body == (byte) 0;
    }
}
