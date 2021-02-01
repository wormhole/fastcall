package net.stackoverflow.fastcall.transport.fastcall.handler.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.stackoverflow.fastcall.transport.fastcall.proto.Message;
import net.stackoverflow.fastcall.transport.fastcall.proto.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 客户端心跳检测handler
 *
 * @author wormhole
 */
public class ClientHeatBeatHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ClientHeatBeatHandler.class);

    private volatile ScheduledFuture<?> heartBeatFuture;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heartBeatFuture != null) {
            heartBeatFuture.cancel(true);
            ctx.close();
            log.error("[L:{} R:{}] Client closed and cancel heartbeat", ctx.channel().localAddress(), ctx.channel().remoteAddress());
        }
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (heartBeatFuture == null) {
            heartBeatFuture = ctx.executor().scheduleAtFixedRate(() -> {
                ctx.writeAndFlush(Message.from(MessageType.HEARTBEAT_PING));
            }, 0, 5000, TimeUnit.MILLISECONDS);
        }
        super.channelRead(ctx, msg);
    }
}
