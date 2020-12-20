package net.stackoverflow.fastcall.exception;

/**
 * 等待rpc超时
 *
 * @author wormhole
 */
public class RpcTimeoutException extends Exception {

    public RpcTimeoutException() {
        super();
    }

    public RpcTimeoutException(String msg) {
        super(msg);
    }
}
