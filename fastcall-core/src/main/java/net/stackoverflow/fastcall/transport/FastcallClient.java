package net.stackoverflow.fastcall.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.stackoverflow.fastcall.proxy.ResponseFuture;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.codec.MessageDecoder;
import net.stackoverflow.fastcall.transport.codec.MessageEncoder;
import net.stackoverflow.fastcall.transport.handler.client.ClientAuthHandler;
import net.stackoverflow.fastcall.transport.handler.client.ClientRpcHandler;
import net.stackoverflow.fastcall.transport.handler.client.ClientHeatBeatHandler;
import net.stackoverflow.fastcall.transport.proto.RpcRequest;
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
     * 序列化Manager
     */
    private SerializeManager serializeManager;


    /**
     * 构造方法
     *
     * @param remoteHost       远程地址
     * @param remotePort       远程端口
     * @param timeout          心跳检测超时时间
     * @param serializeManager 序列化Manager
     */
    public FastcallClient(String remoteHost, Integer remotePort, Integer timeout, SerializeManager serializeManager) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.timeout = timeout;
        this.serializeManager = serializeManager;
    }

    public ResponseFuture call(RpcRequest request) {
        ResponseFuture future = new ResponseFuture();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
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
                            socketChannel.pipeline().addLast(new ClientRpcHandler(request, future));
                        }
                    });
            bootstrap.connect(new InetSocketAddress(remoteHost, remotePort)).sync();
        } catch (Exception e) {
            log.error("NettyClient.connect()", e);
        } finally {
            group.shutdownGracefully();
        }
        return future;
    }
}
