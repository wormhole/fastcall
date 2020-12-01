package net.stackoverflow.fastcall.io.handler.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.stackoverflow.fastcall.io.proto.*;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务端业务处理
 *
 * @author wormhole
 */
public class ServerCallHandler extends ChannelInboundHandlerAdapter {

    private ApplicationContext context;

    public ServerCallHandler(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        if (header != null && header.getType() == MessageType.BUSINESS_REQUEST.value()) {
            CallRequest request = (CallRequest) message.getBody();
            Object obj = context.getBean(Class.forName(request.getClassName()));
            List<String> args = request.getParameters();
            List<Class<?>> argClasses = args.stream().map(Object::getClass).collect(Collectors.toList());
            Method method = obj.getClass().getMethod(request.getMethod(), argClasses.toArray(new Class[0]));
            Object ret = method.invoke(obj, args);
            ctx.writeAndFlush(new Message(MessageType.BUSINESS_RESPONSE, new CallResponse((String) ret)));
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
