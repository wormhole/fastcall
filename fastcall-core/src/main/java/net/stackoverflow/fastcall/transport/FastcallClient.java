package net.stackoverflow.fastcall.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.stackoverflow.fastcall.transport.codec.MessageDecoder;
import net.stackoverflow.fastcall.transport.codec.MessageEncoder;
import net.stackoverflow.fastcall.transport.handler.client.ClientAuthHandler;
import net.stackoverflow.fastcall.transport.handler.client.ClientCallHandler;
import net.stackoverflow.fastcall.transport.handler.client.ClientHeatBeatHandler;
import net.stackoverflow.fastcall.transport.proto.CallRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Netty客户端
 *
 * @author wormhole
 */
public class FastcallClient {

    private static final Logger log = LoggerFactory.getLogger(FastcallClient.class);

    /**
     * 服务端地址
     */
    private String remoteHost;

    /**
     * 服务端端口
     */
    private Integer remotePort;

    /**
     * 心跳检测超时时间
     */
    private Integer timeout;

    /**
     * 远程调用请求
     */
    private CallRequest request;


    /**
     * 构造方法
     *
     * @param remoteHost 远程地址
     * @param remotePort 远程端口
     * @param timeout    心跳检测超时时间
     */
    public FastcallClient(String remoteHost, Integer remotePort, Integer timeout, CallRequest request) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.timeout = timeout;
        this.request = request;
    }

    public void connect() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new MessageDecoder(1024 * 1024, 10, 4, 0));
                            socketChannel.pipeline().addLast(new MessageEncoder());
                            socketChannel.pipeline().addLast(new ReadTimeoutHandler(timeout));
                            socketChannel.pipeline().addLast(new ClientAuthHandler());
                            socketChannel.pipeline().addLast(new ClientHeatBeatHandler());
                            socketChannel.pipeline().addLast(new ClientCallHandler(request));
                        }
                    });
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(remoteHost, remotePort)).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("NettyClient.connect()", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
