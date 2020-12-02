package net.stackoverflow.fastcall.transport.proto;

/**
 * Rpc响应
 *
 * @author wormhole
 */
public class RpcResponse {

    private Object ret;

    public RpcResponse() {

    }

    public RpcResponse(Object ret) {
        this.ret = ret;
    }

    public Object getRet() {
        return ret;
    }

    public void setRet(Object ret) {
        this.ret = ret;
    }
}
