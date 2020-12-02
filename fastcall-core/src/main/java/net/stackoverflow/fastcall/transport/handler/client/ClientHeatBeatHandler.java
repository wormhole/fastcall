package net.stackoverflow.fastcall.transport.handler.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.stackoverflow.fastcall.transport.proto.Message;
import net.stackoverflow.fastcall.transport.proto.MessageType;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 客户端心跳检测handler
 *
 * @author wormhole
 */
public class ClientHeatBeatHandler extends ChannelInboundHandlerAdapter {

    private volatile ScheduledFuture<?> heartBeatFuture;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heartBeatFuture != null) {
            heartBeatFuture.cancel(true);
        }
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (heartBeatFuture == null) {
            heartBeatFuture = ctx.executor().scheduleAtFixedRate(() -> {
                Message ping = new Message(MessageType.HEARTBEAT_PING);
                ctx.writeAndFlush(ping);
            }, 0, 5000, TimeUnit.MILLISECONDS);
        }
        super.channelRead(ctx, msg);
    }
}
