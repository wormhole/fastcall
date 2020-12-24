package net.stackoverflow.fastcall.transport;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * 传输层Manager接口
 *
 * @author wormhole
 */
public interface TransportManager {

    void bind(InetSocketAddress localSocketAddress);

    <T> void sendTo(InetSocketAddress remoteSocketAddress, T message);
}
