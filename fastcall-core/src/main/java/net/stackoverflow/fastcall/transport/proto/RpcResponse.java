package net.stackoverflow.fastcall.transport.proto;

/**
 * Rpc响应
 *
 * @author wormhole
 */
public class RpcResponse {

    private String id;

    private Object response;

    public RpcResponse() {

    }

    public RpcResponse(String id, Object response) {
        this.id = id;
        this.response = response;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }
}
