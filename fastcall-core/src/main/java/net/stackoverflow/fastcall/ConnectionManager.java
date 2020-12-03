package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.transport.FastcallClient;

import java.net.InetSocketAddress;

public interface ConnectionManager {

    FastcallClient getClient(InetSocketAddress address);
}
