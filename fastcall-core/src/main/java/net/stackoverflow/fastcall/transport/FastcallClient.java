package net.stackoverflow.fastcall.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.stackoverflow.fastcall.proxy.ResponseFuture;
import net.stackoverflow.fastcall.register.RegisterManager;
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

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Netty客户端
 *
 * @author wormhole
 */
public class FastcallClient {

    private static final Logger log = LoggerFactory.getLogger(FastcallClient.class);

    private SerializeManager serializeManager;

    private RegisterManager registerManager;

    private ClientRpcHandler clientRpcHandler;


    /**
     * 构造方法
     *
     * @param serializeManager 序列化器
     * @param registerManager  注册管理器
     */
    public FastcallClient(SerializeManager serializeManager, RegisterManager registerManager) {
        this.serializeManager = serializeManager;
        this.registerManager = registerManager;
        this.clientRpcHandler = new ClientRpcHandler(serializeManager);
    }

    private void connect(String host, int port, int timeout, RpcRequest request) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new MessageDecoder(serializeManager));
                            socketChannel.pipeline().addLast(new MessageEncoder(serializeManager));
                            socketChannel.pipeline().addLast(new ReadTimeoutHandler(timeout));
                            socketChannel.pipeline().addLast(new ClientAuthHandler());
                            socketChannel.pipeline().addLast(new ClientHeatBeatHandler());
                            socketChannel.pipeline().addLast(clientRpcHandler);
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(new Message(MessageType.BUSINESS_REQUEST, request)).sync();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("NettyClient.connect()", e);
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public ResponseFuture call(RpcRequest request) {
        clientRpcHandler.putFuture(request.getId());
        InetSocketAddress address = registerManager.getRemoteAddr(request.getGroup(), request.getClazz().getName());
        connect(address.getHostName(), address.getPort(), 60, request);
        return clientRpcHandler.getResponse(request.getId());
    }
}
