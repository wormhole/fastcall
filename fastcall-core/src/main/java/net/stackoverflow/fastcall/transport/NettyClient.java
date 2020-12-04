package net.stackoverflow.fastcall.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.stackoverflow.fastcall.exception.ConnectionInActiveException;
import net.stackoverflow.fastcall.proxy.ResponseFuture;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.codec.MessageDecoder;
import net.stackoverflow.fastcall.transport.codec.MessageEncoder;
import net.stackoverflow.fastcall.transport.handler.client.ClientAuthHandler;
import net.stackoverflow.fastcall.transport.handler.client.ClientRpcHandler;
import net.stackoverflow.fastcall.transport.handler.client.ClientHeatBeatHandler;
import net.stackoverflow.fastcall.transport.proto.Message;
import net.stackoverflow.fastcall.transport.proto.MessageType;
import net.stackoverflow.fastcall.transport.proto.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * Netty客户端
 *
 * @author wormhole
 */
public class NettyClient {

    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);

    private final SerializeManager serializeManager;

    private final ClientRpcHandler clientRpcHandler;

    private final int timeout;

    private InetSocketAddress inetSocketAddress;

    private Channel channel;

    /**
     * 构造方法
     *
     * @param serializeManager 序列化器
     * @param clientRpcHandler rpc处理器
     * @param timeout          超时时间
     */
    public NettyClient(int timeout, SerializeManager serializeManager, ClientRpcHandler clientRpcHandler, InetSocketAddress inetSocketAddress) {
        this.timeout = timeout;
        this.serializeManager = serializeManager;
        this.clientRpcHandler = clientRpcHandler;
        this.inetSocketAddress = inetSocketAddress;
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
                            pipeline.addLast(clientRpcHandler);
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(inetSocketAddress).sync();
            this.channel = channelFuture.channel();
            countDownLatch.countDown();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("fail to connect service ip:{}, port:{}", getHost(), getPort(), e);
        } finally {
            channel = null;
            eventLoopGroup.shutdownGracefully();
            log.debug("connect closed ip:{}, port:{}", getHost(), getPort());
        }
    }

    public boolean isActive() {
        return channel != null && channel.isActive();
    }

    public ResponseFuture call(RpcRequest request) throws ConnectionInActiveException {
        if (isActive()) {
            ResponseFuture future = clientRpcHandler.getFuture(request.getId());
            channel.writeAndFlush(new Message(MessageType.BUSINESS_REQUEST, request));
            return future;
        } else {
            throw new ConnectionInActiveException(getHost(), getPort());
        }
    }

    public String getHost() {
        return inetSocketAddress.getAddress().getHostAddress();
    }

    public Integer getPort() {
        return inetSocketAddress.getPort();
    }
}
