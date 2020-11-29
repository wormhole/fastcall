package net.stackoverflow.fastcall.handler.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import net.stackoverflow.fastcall.model.Message;
import net.stackoverflow.fastcall.model.Header;
import net.stackoverflow.fastcall.model.MessageType;

/**
 * 服务端心跳handler
 *
 * @author wormhole
 */
public class ServerHeatBeatHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        if (header != null && header.getType() == MessageType.HEARTBEAT_PING.value()) {
            ctx.writeAndFlush(new Message(MessageType.HEARTBEAT_PONG));
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
