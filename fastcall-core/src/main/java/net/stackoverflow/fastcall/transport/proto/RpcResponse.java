package net.stackoverflow.fastcall.transport.proto;

/**
 * Rpc响应
 *
 * @author wormhole
 */
public class RpcResponse {

    private String id;

    private Integer code;

    private Object response;

    private Throwable throwable;

    public RpcResponse() {

    }

    public RpcResponse(String id, Integer code, Object response, Throwable throwable) {
        this.id = id;
        this.code = code;
        this.response = response;
        this.throwable = throwable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RpcResponse{");
        sb.append("id='").append(id).append('\'');
        sb.append(", code=").append(code);
        sb.append(", response=").append(response);
        sb.append(", throwable=").append(throwable);
        sb.append('}');
        return sb.toString();
    }
}
