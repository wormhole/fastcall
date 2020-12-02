package net.stackoverflow.fastcall.transport.handler.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.stackoverflow.fastcall.transport.proto.Header;
import net.stackoverflow.fastcall.transport.proto.Message;
import net.stackoverflow.fastcall.transport.proto.MessageType;

/**
 * 服务端心跳handler
 *
 * @author wormhole
 */
public class ServerHeatBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        if (header.getType() == MessageType.HEARTBEAT_PING.value()) {
            ctx.writeAndFlush(new Message(MessageType.HEARTBEAT_PONG));
        }
        super.channelRead(ctx, msg);
    }
}
