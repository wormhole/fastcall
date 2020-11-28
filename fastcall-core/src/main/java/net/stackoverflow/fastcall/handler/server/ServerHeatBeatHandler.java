package net.stackoverflow.fastcall.handler.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import net.stackoverflow.fastcall.model.NettyMessage;
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
        NettyMessage message = (NettyMessage) msg;
        Header header = message.getHeader();
        if (header != null && header.getType() == MessageType.HEARTBEAT_PING.value()) {
            ctx.writeAndFlush(new NettyMessage(MessageType.HEARTBEAT_PONG));
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
