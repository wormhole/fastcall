package net.stackoverflow.fastcall.io.handler.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.stackoverflow.fastcall.io.proto.Header;
import net.stackoverflow.fastcall.io.proto.Message;
import net.stackoverflow.fastcall.io.proto.MessageType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端认证handler
 *
 * @author wormhole
 */
public class ServerAuthHandler extends ChannelHandlerAdapter {

    private static Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        if (header != null && header.getType() == MessageType.AUTH_REQUEST.value()) {
            String nodeIndex = ctx.channel().remoteAddress().toString();
            Message response = null;

            if (nodeCheck.containsKey(nodeIndex)) {
                response = new Message(MessageType.AUTH_RESPONSE, (byte) -1);
            } else {
                response = new Message(MessageType.AUTH_RESPONSE, (byte) 0);
                nodeCheck.put(nodeIndex, true);
            }

            ctx.writeAndFlush(response);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
    }
}
