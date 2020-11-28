package net.stackoverflow.fastcall.handler.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import net.stackoverflow.fastcall.task.HeartBeatTask;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 客户端心跳检测handler
 *
 * @author wormhole
 */
public class ClientHeatBeatHandler extends ChannelHandlerAdapter {

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
            heartBeatFuture = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
        }
        ctx.fireChannelRead(msg);
    }
}
