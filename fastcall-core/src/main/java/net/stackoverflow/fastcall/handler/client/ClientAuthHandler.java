package net.stackoverflow.fastcall.handler.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import net.stackoverflow.fastcall.model.NettyMessage;
import net.stackoverflow.fastcall.model.Header;
import net.stackoverflow.fastcall.model.MessageType;

/**
 * 客户端认证handler
 *
 * @author wormhole
 */
public class ClientAuthHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyMessage request = new NettyMessage(MessageType.AUTH_REQUEST);
        ctx.writeAndFlush(request);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        Header header = message.getHeader();
        if (header != null && header.getType() == MessageType.AUTH_RESPONSE.value()) {
            byte body = (byte) message.getBody();
            if (!isAuthSuccess(body)) {
                ctx.close();
            } else {
                ctx.fireChannelRead(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private boolean isAuthSuccess(byte body) {
        return body == (byte) 0;
    }
}
