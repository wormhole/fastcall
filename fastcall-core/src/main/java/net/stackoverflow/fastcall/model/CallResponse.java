package net.stackoverflow.fastcall.model;

import org.msgpack.annotation.Message;

/**
 * 业务处理响应
 *
 * @author wormhole
 */
@Message
public class CallResponse {

    private Object ret;

    public Object getRet() {
        return ret;
    }

    public void setRet(Object ret) {
        this.ret = ret;
    }
}
