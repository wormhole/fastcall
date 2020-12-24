package net.stackoverflow.fastcall.transport.fastcall.proto;

/**
 * Rpc响应
 *
 * @author wormhole
 */
public class RpcResponse {

    private String id;

    private Integer code;

    private Class<?> responseType;

    private byte[] responseBytes;

    private Class<?> throwableType;

    private byte[] throwableBytes;

    public RpcResponse() {

    }

    public RpcResponse(String id, Integer code, Class<?> responseType, byte[] responseBytes, Class<?> throwableType, byte[] throwableBytes) {
        this.id = id;
        this.code = code;
        this.responseType = responseType;
        this.responseBytes = responseBytes;
        this.throwableType = throwableType;
        this.throwableBytes = throwableBytes;
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

    public Class<?> getResponseType() {
        return responseType;
    }

    public void setResponseType(Class<?> responseType) {
        this.responseType = responseType;
    }

    public byte[] getResponseBytes() {
        return responseBytes;
    }

    public void setResponseBytes(byte[] responseBytes) {
        this.responseBytes = responseBytes;
    }

    public Class<?> getThrowableType() {
        return throwableType;
    }

    public void setThrowableType(Class<?> throwableType) {
        this.throwableType = throwableType;
    }

    public byte[] getThrowableBytes() {
        return throwableBytes;
    }

    public void setThrowableBytes(byte[] throwableBytes) {
        this.throwableBytes = throwableBytes;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RpcResponse{");
        sb.append("id='").append(id).append('\'');
        sb.append(", code=").append(code);
        sb.append(", responseType=").append(responseType);
        sb.append(", throwableType=").append(throwableType);
        sb.append('}');
        return sb.toString();
    }
}
