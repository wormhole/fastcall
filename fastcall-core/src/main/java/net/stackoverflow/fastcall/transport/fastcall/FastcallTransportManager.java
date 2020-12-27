package net.stackoverflow.fastcall.transport.fastcall;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.stackoverflow.fastcall.core.ResponseFutureContext;
import net.stackoverflow.fastcall.core.ResponseFuture;
import net.stackoverflow.fastcall.factory.NameThreadFactory;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.TransportManager;
import net.stackoverflow.fastcall.transport.fastcall.codec.MessageDecoder;
import net.stackoverflow.fastcall.transport.fastcall.codec.MessageEncoder;
import net.stackoverflow.fastcall.transport.fastcall.handler.client.ClientAuthHandler;
import net.stackoverflow.fastcall.transport.fastcall.handler.client.ClientHeatBeatHandler;
import net.stackoverflow.fastcall.transport.fastcall.handler.client.ClientRpcHandler;
import net.stackoverflow.fastcall.transport.fastcall.handler.server.ServerAuthHandler;
import net.stackoverflow.fastcall.transport.fastcall.handler.server.ServerHeatBeatHandler;
import net.stackoverflow.fastcall.transport.fastcall.handler.server.ServerRpcHandler;
import net.stackoverflow.fastcall.transport.fastcall.proto.Message;
import net.stackoverflow.fastcall.transport.fastcall.proto.MessageType;
import net.stackoverflow.fastcall.transport.fastcall.proto.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;

/**
 * fastcall传输协议Manager实现类
 *
 * @author wormhole
 */
public class FastcallTransportManager implements TransportManager {

    private final static Logger log = LoggerFactory.getLogger(FastcallTransportManager.class);

    private static final Integer TIMEOUT = 60;

    private final SerializeManager serializeManager;

    private final ExecutorService serverExecutorService;

    private final ExecutorService rpcExecutorService;

    private final ExecutorService connectionExecutorService;

    private final Map<String, Channel> channelPool;

    private Channel sChannel;

    public FastcallTransportManager(SerializeManager serializeManager, int nThreads) {
        this.serializeManager = serializeManager;

        this.channelPool = new ConcurrentHashMap<>();
        this.serverExecutorService = Executors.newSingleThreadExecutor(new NameThreadFactory("Server"));
        this.rpcExecutorService = new ThreadPoolExecutor(0, nThreads, 60, TimeUnit.SECONDS, new SynchronousQueue<>(), new NameThreadFactory("Rpc"));
        this.connectionExecutorService = Executors.newCachedThreadPool(new NameThreadFactory("connection"));
    }

    @Override
    public void bind(InetSocketAddress localSocketAddress) {
        serverExecutorService.execute(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) {
                                ChannelPipeline pipeline = socketChannel.pipeline();
                                pipeline.addLast(new MessageDecoder(serializeManager));
                                pipeline.addLast(new MessageEncoder(serializeManager));
                                pipeline.addLast(new ReadTimeoutHandler(TIMEOUT));
                                pipeline.addLast(new ServerAuthHandler());
                                pipeline.addLast(new ServerHeatBeatHandler());
                                pipeline.addLast(new ServerRpcHandler(serializeManager, rpcExecutorService));
                            }
                        });
                ChannelFuture channelFuture = bootstrap.bind(localSocketAddress).sync();
                log.info("[L:{}] TransportManager bind success", localSocketAddress.getAddress().getHostAddress() + ":" + localSocketAddress.getPort());
                sChannel = channelFuture.channel();
                sChannel.closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("[L:{}] TransportManager fail to bind", localSocketAddress.getAddress().getHostAddress() + ":" + localSocketAddress.getPort(), e);
            } finally {
                log.info("[L:{}] TransportManager closed", localSocketAddress.getAddress().getHostAddress() + ":" + localSocketAddress.getPort());
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
            }
        });
    }

    @Override
    public ResponseFuture sendTo(InetSocketAddress remoteSocketAddress, RpcRequest request) {
        ResponseFutureContext context = ResponseFutureContext.getInstance();
        ResponseFuture future = context.createFuture(request.getId());
        String host = remoteSocketAddress.getAddress().getHostAddress();
        Integer port = remoteSocketAddress.getPort();
        String key = host + ":" + port;
        Channel channel = channelPool.get(key);
        if (channel == null || !channel.isActive()) {
            channel = connect(remoteSocketAddress);
        }
        channel.writeAndFlush(new Message(MessageType.BUSINESS_REQUEST, request));
        log.trace("[L:{} R:{}] TransportManager send request, responseId:{}", channel.localAddress(), channel.remoteAddress(), request.getId());
        return future;
    }

    @Override
    public synchronized void close() {
        for (Channel channel : channelPool.values()) {
            channel.close();
        }
        connectionExecutorService.shutdown();

        rpcExecutorService.shutdown();
        if (sChannel != null) {
            sChannel.close();
        }
        rpcExecutorService.shutdown();
    }

    /**
     * 初始化Channel
     *
     * @param remoteSocketAddress
     * @return
     */
    private synchronized Channel connect(InetSocketAddress remoteSocketAddress) {
        String host = remoteSocketAddress.getAddress().getHostAddress();
        Integer port = remoteSocketAddress.getPort();
        String key = host + ":" + port;

        Channel channel = channelPool.get(key);
        if (channel == null || !channel.isActive()) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            connectionExecutorService.execute(() -> {
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
                                    pipeline.addLast(new ReadTimeoutHandler(TIMEOUT));
                                    pipeline.addLast(new ClientAuthHandler());
                                    pipeline.addLast(new ClientHeatBeatHandler());
                                    pipeline.addLast(new ClientRpcHandler());
                                }
                            });
                    ChannelFuture channelFuture = bootstrap.connect(remoteSocketAddress).sync();
                    Channel chl = channelFuture.channel();
                    log.info("[L:{} R:{}] TransportManager connect success", chl.localAddress(), chl.remoteAddress());
                    channelPool.put(key, chl);
                    countDownLatch.countDown();
                    chl.closeFuture().sync();
                } catch (InterruptedException e) {
                    log.error("[R:{}] TransportManager fail to connect", host + ":" + port, e);
                } finally {
                    log.info("[R:{}] TransportManager closed", host + ":" + port);
                    eventLoopGroup.shutdownGracefully();
                }
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                log.error("[R:{}] TransportManager fail to init channel", host + ":" + port, e);
            }
        }
        return channelPool.get(key);
    }

}
