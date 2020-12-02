package net.stackoverflow.fastcall.transport.handler.server;

import io.netty.channel.*;
import net.stackoverflow.fastcall.transport.proto.Header;
import net.stackoverflow.fastcall.transport.proto.Message;
import net.stackoverflow.fastcall.transport.proto.MessageType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端认证handler
 *
 * @author wormhole
 */
public class ServerAuthHandler extends ChannelInboundHandlerAdapter {

    private static Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        if (header.getType() == MessageType.AUTH_REQUEST.value()) {
            String nodeIndex = ctx.channel().remoteAddress().toString();
            Message response = null;

            if (nodeCheck.containsKey(nodeIndex)) {
                response = new Message(MessageType.AUTH_RESPONSE, (byte) -1);
            } else {
                response = new Message(MessageType.AUTH_RESPONSE, (byte) 0);
                nodeCheck.put(nodeIndex, true);
            }

            ctx.writeAndFlush(response);
        }
        super.channelRead(ctx, msg);
    }
}
