package net.stackoverflow.fastcall.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.codec.MessageDecoder;
import net.stackoverflow.fastcall.transport.codec.MessageEncoder;
import net.stackoverflow.fastcall.transport.handler.server.ServerAuthHandler;
import net.stackoverflow.fastcall.transport.handler.server.ServerHeatBeatHandler;
import net.stackoverflow.fastcall.transport.handler.server.ServerRpcHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Netty服务端
 *
 * @author wormhole
 */
public class FastcallServer implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(FastcallServer.class);

    /**
     * 监听队列
     */
    private Integer backlog;

    /**
     * 心跳检测超时时间
     */
    private Integer timeout;

    /**
     * 绑定地址
     */
    private String host;

    /**
     * 绑定端口
     */
    private Integer port;

    /**
     * 序列化Manager
     */
    private SerializeManager serializeManager;

    /**
     * bean容器
     */
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 构造方法
     *
     * @param backlog          监听队列
     * @param timeout          心跳检测超时时间
     * @param host             监听地址
     * @param port             监听端口
     * @param serializeManager 序列化Manager
     */
    public FastcallServer(Integer backlog, Integer timeout, String host, Integer port, SerializeManager serializeManager) {
        this.backlog = backlog;
        this.timeout = timeout;
        this.host = host;
        this.port = port;
        this.serializeManager = serializeManager;
    }

    public void bind() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .option(ChannelOption.SO_BACKLOG, backlog)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new MessageDecoder(serializeManager));
                            socketChannel.pipeline().addLast(new MessageEncoder(serializeManager));
                            socketChannel.pipeline().addLast(new ReadTimeoutHandler(timeout));
                            socketChannel.pipeline().addLast(new ServerAuthHandler());
                            socketChannel.pipeline().addLast(new ServerHeatBeatHandler());
                            socketChannel.pipeline().addLast(new ServerRpcHandler(applicationContext));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(host, port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("NettyServer.bind()", e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}