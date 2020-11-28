package net.stackoverflow.fastcall.handler.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import net.stackoverflow.fastcall.model.NettyMessage;

/**
 * 服务端业务处理
 *
 * @author wormhole
 */
public class ServerCallHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
    }
}
