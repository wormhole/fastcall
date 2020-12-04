package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.transport.NettyClient;

import java.net.InetSocketAddress;

public interface ConnectionManager {

    NettyClient getClient(InetSocketAddress address);
}
