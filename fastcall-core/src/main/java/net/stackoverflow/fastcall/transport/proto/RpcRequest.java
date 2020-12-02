package net.stackoverflow.fastcall.transport.proto;

import java.util.List;

/**
 * Rpc请求
 *
 * @author wormhole
 */
public class RpcRequest {

    /**
     * 所属分组
     */
    private String group;

    /**
     * 接口Class对象
     */
    private Class<?> clazz;

    /**
     * 方法名
     */
    private String method;

    /**
     * 参数类型
     */
    private List<Class<?>> paramsType;

    /**
     * 参数
     */
    private List<Object> params;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<Class<?>> getParamsType() {
        return paramsType;
    }

    public void setParamsType(List<Class<?>> paramsType) {
        this.paramsType = paramsType;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }
}
