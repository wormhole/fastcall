package net.stackoverflow.fastcall.transport;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * 传输层Manager接口
 *
 * @author wormhole
 */
public interface TransportManager {

    /**
     * 绑定本地端口
     *
     * @param localSocketAddress
     */
    void bind(InetSocketAddress localSocketAddress);

    /**
     * 发送报文给远程地址
     *
     * @param remoteSocketAddress
     * @param message
     * @param <T>
     */
    <T> void sendTo(InetSocketAddress remoteSocketAddress, T message);

    /**
     * 关闭所有连接与线程池
     */
    void close();
}
