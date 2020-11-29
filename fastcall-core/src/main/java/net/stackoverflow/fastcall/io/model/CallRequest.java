package net.stackoverflow.fastcall.io.model;

import org.msgpack.annotation.Message;

import java.util.List;

/**
 * 业务请求
 *
 * @author wormhole
 */
@Message
public class CallRequest {

    private String itf;

    private List<Object> args;

    public String getItf() {
        return itf;
    }

    public void setItf(String itf) {
        this.itf = itf;
    }

    public List<Object> getArgs() {
        return args;
    }

    public void setArgs(List<Object> args) {
        this.args = args;
    }
}
