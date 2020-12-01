package net.stackoverflow.fastcall.io.handler.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.stackoverflow.fastcall.io.proto.Message;
import net.stackoverflow.fastcall.io.proto.MessageType;

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
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (heartBeatFuture == null) {
            heartBeatFuture = ctx.executor().scheduleAtFixedRate(()->{
                Message ping = new Message(MessageType.HEARTBEAT_PING);
                ctx.writeAndFlush(ping);
            }, 0, 5000, TimeUnit.MILLISECONDS);
        }
        ctx.fireChannelRead(msg);
    }
}
