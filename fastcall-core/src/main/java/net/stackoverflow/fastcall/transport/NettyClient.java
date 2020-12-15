package net.stackoverflow.fastcall.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.stackoverflow.fastcall.context.ResponseFuture;
import net.stackoverflow.fastcall.context.ResponseFutureContext;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.codec.MessageDecoder;
import net.stackoverflow.fastcall.transport.codec.MessageEncoder;
import net.stackoverflow.fastcall.transport.handler.client.ClientAuthHandler;
import net.stackoverflow.fastcall.transport.handler.client.ClientHeatBeatHandler;
import net.stackoverflow.fastcall.transport.handler.client.ClientRpcHandler;
import net.stackoverflow.fastcall.transport.proto.Message;
import net.stackoverflow.fastcall.transport.proto.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Netty客户端
 *
 * @author wormhole
 */
public class NettyClient {

    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);

    private final SerializeManager serializeManager;

    private final ResponseFutureContext responseFutureContext;

    private final String host;

    private final Integer port;

    private final int timeout;

    private volatile Channel channel;

    /**
     * 构造方法
     *
     * @param serializeManager      序列化管理器
     * @param responseFutureContext 响应上下文
     * @param host                  远程ip地址
     * @param port                  远程端口
     * @param timeout               心跳检测超时时间
     */
    public NettyClient(SerializeManager serializeManager, ResponseFutureContext responseFutureContext, String host, Integer port, Integer timeout) {
        this.serializeManager = serializeManager;
        this.responseFutureContext = responseFutureContext;
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    public void connect(CountDownLatch countDownLatch) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new MessageDecoder(serializeManager));
                            pipeline.addLast(new MessageEncoder(serializeManager));
                            pipeline.addLast(new ReadTimeoutHandler(timeout));
                            pipeline.addLast(new ClientAuthHandler());
                            pipeline.addLast(new ClientHeatBeatHandler());
                            pipeline.addLast(new ClientRpcHandler(responseFutureContext));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            this.channel = channelFuture.channel();
            countDownLatch.countDown();
            log.info("[L:{} R:{}] Client connect success", channel.localAddress(), channel.remoteAddress());
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("[R:{}] Client fail to connect", getHost() + ":" + getPort(), e);
        } finally {
            log.info("[L:{} R:{}] Client connection closed", channel.localAddress(), channel.remoteAddress());
            eventLoopGroup.shutdownGracefully();
        }
    }

    public ResponseFuture send(Message message) {
        RpcRequest request = (RpcRequest) message.getBody();
        ResponseFuture future = responseFutureContext.createFuture(request.getId());
        if (channel != null) {
            channel.writeAndFlush(message);
        }
        return future;
    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }
}
