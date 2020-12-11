package net.stackoverflow.fastcall.transport.proto;

import java.util.List;

/**
 * Rpc请求
 *
 * @author wormhole
 */
public class RpcRequest {

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 所属分组
     */
    private String group;

    /**
     * 版本号
     */
    private String version;

    /**
     * 接口Class对象
     */
    private Class<?> interfaceType;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Class<?> getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(Class<?> interfaceType) {
        this.interfaceType = interfaceType;
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RpcRequest{");
        sb.append("id='").append(id).append('\'');
        sb.append(", group='").append(group).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", interfaceType=").append(interfaceType);
        sb.append(", method='").append(method).append('\'');
        sb.append(", paramsType=").append(paramsType);
        sb.append(", params=").append(params);
        sb.append('}');
        return sb.toString();
    }
}
