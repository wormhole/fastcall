package net.stackoverflow.fastcall.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.stackoverflow.fastcall.codec.MessageDecoder;
import net.stackoverflow.fastcall.codec.MessageEncoder;
import net.stackoverflow.fastcall.handler.client.ClientAuthHandler;
import net.stackoverflow.fastcall.handler.client.ClientCallHandler;
import net.stackoverflow.fastcall.handler.client.ClientHeatBeatHandler;
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
     * 本地地址
     */
    private String localHost;

    /**
     * 本地端口
     */
    private Integer localPort;

    /**
     * 心跳检测超时时间
     */
    private Integer timeout;


    /**
     * 构造方法
     *
     * @param remoteHost 远程地址
     * @param remotePort 远程端口
     * @param localHost  本地地址
     * @param localPort  本地端口
     * @param timeout    心跳检测超时时间
     */
    public FastcallClient(String remoteHost, Integer remotePort, String localHost, Integer localPort, Integer timeout) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.localHost = localHost;
        this.localPort = localPort;
        this.timeout = timeout;
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
                            socketChannel.pipeline().addLast(new ClientCallHandler());
                        }
                    });
            if (localHost != null && localPort != null) {
                bootstrap.localAddress(localHost, localPort);
            } else if (localHost == null && localPort != null) {
                bootstrap.localAddress(localPort);
            }
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(remoteHost, remotePort)).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("NettyClient.connect()", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
