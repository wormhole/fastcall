package net.stackoverflow.fastcall.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.stackoverflow.fastcall.context.BeanContext;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.codec.MessageDecoder;
import net.stackoverflow.fastcall.transport.codec.MessageEncoder;
import net.stackoverflow.fastcall.transport.handler.server.ServerAuthHandler;
import net.stackoverflow.fastcall.transport.handler.server.ServerHeatBeatHandler;
import net.stackoverflow.fastcall.transport.handler.server.ServerRpcHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Netty服务端
 *
 * @author wormhole
 */
public class NettyServer {

    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private final Integer backlog;

    private final Integer timeout;

    private final String host;

    private final Integer port;

    private final Integer threads;

    private final SerializeManager serializeManager;

    private final BeanContext beanContext;

    private Channel channel;

    /**
     * 构造方法
     *
     * @param backlog          监听队列
     * @param timeout          心跳检测超时时间
     * @param host             监听地址
     * @param port             监听端口
     * @param threads          业务线程池大小
     * @param serializeManager 序列化管理器
     * @param beanContext      服务上下文
     */
    public NettyServer(Integer backlog, Integer timeout, String host, Integer port, Integer threads, SerializeManager serializeManager, BeanContext beanContext) {
        this.backlog = backlog;
        this.timeout = timeout;
        this.host = host;
        this.port = port;
        this.threads = threads;
        this.serializeManager = serializeManager;
        this.beanContext = beanContext;
    }

    public void bind(CountDownLatch countDownLatch) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        NioEventLoopGroup businessGroup = new NioEventLoopGroup(threads);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .option(ChannelOption.SO_BACKLOG, backlog)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new MessageDecoder(serializeManager));
                            pipeline.addLast(new MessageEncoder(serializeManager));
                            pipeline.addLast(new ReadTimeoutHandler(timeout));
                            pipeline.addLast(new ServerAuthHandler());
                            pipeline.addLast(new ServerHeatBeatHandler());
                            pipeline.addLast(businessGroup, new ServerRpcHandler(serializeManager, beanContext));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(host, port).sync();
            log.info("[L:{}] Server bind success", host + ":" + port);
            channel = channelFuture.channel();
            countDownLatch.countDown();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("[L:{}] Server fail to bind", host + ":" + port, e);
        } finally {
            log.info("[L:{}] Server stopped", host + ":" + port);
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
    }
}
