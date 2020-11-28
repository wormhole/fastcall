package net.stackoverflow.fastcall.task;

import io.netty.channel.ChannelHandlerContext;
import net.stackoverflow.fastcall.model.NettyMessage;
import net.stackoverflow.fastcall.model.MessageType;

/**
 * 心跳定时任务
 *
 * @author wormhole
 */
public class HeartBeatTask implements Runnable {
    private final ChannelHandlerContext ctx;

    public HeartBeatTask(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        NettyMessage ping = new NettyMessage(MessageType.HEARTBEAT_PING);
        ctx.writeAndFlush(ping);
    }
}
