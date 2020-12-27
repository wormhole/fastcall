package net.stackoverflow.fastcall.transport;

import net.stackoverflow.fastcall.core.ResponseFuture;
import net.stackoverflow.fastcall.transport.fastcall.proto.RpcRequest;

import java.net.InetSocketAddress;

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
     * @param request
     */
    ResponseFuture sendTo(InetSocketAddress remoteSocketAddress, RpcRequest request);

    /**
     * 关闭所有连接与线程池
     */
    void close();
}
