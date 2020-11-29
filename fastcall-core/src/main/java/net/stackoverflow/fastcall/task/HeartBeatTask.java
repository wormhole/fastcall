package net.stackoverflow.fastcall.task;

import io.netty.channel.ChannelHandlerContext;
import net.stackoverflow.fastcall.model.Message;
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
        Message ping = new Message(MessageType.HEARTBEAT_PING);
        ctx.writeAndFlush(ping);
    }
}
