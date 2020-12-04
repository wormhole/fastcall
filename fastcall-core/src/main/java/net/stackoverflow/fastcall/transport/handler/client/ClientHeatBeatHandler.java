package net.stackoverflow.fastcall.transport.handler.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.stackoverflow.fastcall.transport.proto.Message;
import net.stackoverflow.fastcall.transport.proto.MessageType;
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

    private static final Logger log = LoggerFactory.getLogger(ClientAuthHandler.class);

    private volatile ScheduledFuture<?> heartBeatFuture;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heartBeatFuture != null) {
            heartBeatFuture.cancel(true);
            ctx.close();
            log.error("cancel heartbeat and close channel, remote address:{}, local address:{}", ctx.channel().remoteAddress(), ctx.channel().localAddress(), cause);
        }
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (heartBeatFuture == null) {
            log.debug("start heartbeat task, remote address:{}, local address:{}", ctx.channel().remoteAddress(), ctx.channel().localAddress());
            heartBeatFuture = ctx.executor().scheduleAtFixedRate(() -> {
                Message ping = new Message(MessageType.HEARTBEAT_PING);
                ctx.writeAndFlush(ping);
            }, 0, 5000, TimeUnit.MILLISECONDS);
        }
        super.channelRead(ctx, msg);
    }
}
