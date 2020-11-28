package net.stackoverflow.fastcall.handler.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import net.stackoverflow.fastcall.model.NettyMessage;

/**
 * 客户端业务Handler
 *
 * @author wormhole
 */
public class ClientCallHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
    }
}
