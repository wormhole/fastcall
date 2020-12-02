package net.stackoverflow.fastcall.transport.proto;

import org.msgpack.annotation.Message;

/**
 * 业务处理响应
 *
 * @author wormhole
 */
@Message
public class CallResponse {

    private String ret;

    public CallResponse() {

    }

    public CallResponse(String ret) {
        this.ret = ret;
    }

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }
}
