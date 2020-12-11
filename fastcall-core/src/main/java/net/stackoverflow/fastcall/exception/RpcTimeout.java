package net.stackoverflow.fastcall.exception;

/**
 * 等待rpc超时
 *
 * @author wormhole
 */
public class RpcTimeout extends Exception {

    public RpcTimeout() {
        super();
    }

    public RpcTimeout(String msg) {
        super(msg);
    }
}
