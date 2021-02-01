package net.stackoverflow.fastcall.transport.fastcall.handler.server;

import io.netty.channel.*;
import net.stackoverflow.fastcall.transport.fastcall.proto.Header;
import net.stackoverflow.fastcall.transport.fastcall.proto.Message;
import net.stackoverflow.fastcall.transport.fastcall.proto.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端认证handler
 *
 * @author wormhole
 */
public class ServerAuthHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ServerAuthHandler.class);

    private static final Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        log.warn("[L:{} R:{}] Server caught exception and remove auth info", ctx.channel().localAddress(), ctx.channel().remoteAddress());
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        log.warn("[L:{} R:{}] Server channel inactive and remove auth info", ctx.channel().localAddress(), ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        String nodeIndex = ctx.channel().remoteAddress().toString();
        if (header.getType() == MessageType.AUTH_REQUEST.value()) {
            Message response = null;
            if (nodeCheck.containsKey(nodeIndex)) {
                response = Message.from(MessageType.AUTH_RESPONSE).body((byte) -1);
                log.error("[L:{} R:{}] Server auth fail", ctx.channel().localAddress(), ctx.channel().remoteAddress());
            } else {
                response = Message.from(MessageType.AUTH_RESPONSE).body((byte) 0);
                nodeCheck.put(nodeIndex, true);
                log.debug("[L:{} R:{}] Server auth success", ctx.channel().localAddress(), ctx.channel().remoteAddress());
            }
            ctx.writeAndFlush(response);
        } else {
            if (!nodeCheck.containsKey(nodeIndex)) {
                ctx.writeAndFlush(Message.from(MessageType.AUTH_RESPONSE).body((byte) -1));
                log.error("[L:{} R:{}] Server receive message before auth", ctx.channel().localAddress(), ctx.channel().remoteAddress());
            }
        }
        super.channelRead(ctx, msg);
    }
}
