package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.transport.NettyClient;

import java.net.InetSocketAddress;

/**
 * 连接管理器
 *
 * @author wormhole
 */
public interface ConnectionManager {

    /**
     * 获取连接客户端
     *
     * @param address 地址
     * @return
     */
    NettyClient getClient(InetSocketAddress address);
}
