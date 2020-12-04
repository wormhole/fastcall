package net.stackoverflow.fastcall.transport.handler.client;

import io.netty.channel.*;
import net.stackoverflow.fastcall.proxy.ResponseFuture;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端业务Handler
 *
 * @author wormhole
 */
@ChannelHandler.Sharable
public class ClientRpcHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ClientRpcHandler.class);

    private volatile Map<String, ResponseFuture> futureMap;

    public ClientRpcHandler() {
        this.futureMap = new ConcurrentHashMap<>();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        if (header.getType() == MessageType.BUSINESS_RESPONSE.value()) {
            RpcResponse response = (RpcResponse) message.getBody();
            setResponse(response);
            log.debug("[L:{} R:{}] Client set response, responseId:{}, responseCode:{}", ctx.channel().localAddress(), ctx.channel().remoteAddress(), response.getId(), response.getCode());
        }
        super.channelRead(ctx, msg);
    }

    public synchronized void setResponse(RpcResponse response) {
        ResponseFuture future = futureMap.get(response.getId());
        future.setResponse(response.getResponse());
        futureMap.remove(response.getId());
    }

    public synchronized ResponseFuture getFuture(String id) {
        ResponseFuture future = new ResponseFuture();
        futureMap.put(id, future);
        return future;
    }
}
