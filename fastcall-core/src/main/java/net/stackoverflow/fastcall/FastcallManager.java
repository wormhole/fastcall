package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.transport.proto.RpcRequest;

/**
 * Fastcall对外统一外观
 *
 * @author wormhole
 */
public interface FastcallManager {

    ResponseFuture call(RpcRequest request);

}
