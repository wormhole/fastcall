package net.stackoverflow.fastcall.io.proto;

import org.msgpack.annotation.Message;

import java.util.List;

/**
 * 业务请求
 *
 * @author wormhole
 */
@Message
public class CallRequest {

    /**
     * 所属分组
     */
    private String group;

    /**
     * 接口全限定名
     */
    private String className;

    /**
     * 方法名
     */
    private String method;

    /**
     * 参数
     */
    private List<Object> parameters;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }
}
