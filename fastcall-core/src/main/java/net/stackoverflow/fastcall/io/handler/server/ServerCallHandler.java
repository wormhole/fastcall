package net.stackoverflow.fastcall.io.handler.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import net.stackoverflow.fastcall.io.proto.Header;
import net.stackoverflow.fastcall.io.proto.Message;
import net.stackoverflow.fastcall.io.proto.MessageType;
import org.springframework.context.ApplicationContext;

/**
 * 服务端业务处理
 *
 * @author wormhole
 */
public class ServerCallHandler extends ChannelHandlerAdapter {

    private ApplicationContext context;

    public ServerCallHandler(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        if (header != null && header.getType() == MessageType.BUSINESS_REQUEST.value()) {

        }else {
            ctx.fireChannelRead(msg);
        }
    }
}
