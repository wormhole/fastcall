package net.stackoverflow.fastcall.io.handler.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import net.stackoverflow.fastcall.io.model.Message;

/**
 * 客户端业务Handler
 *
 * @author wormhole
 */
public class ClientCallHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
    }
}
