package net.stackoverflow.fastcall.register;

/**
 * 服务类元数据
 *
 * @author wormhole
 */
public class ServiceMeta {

    /**
     * 分组
     */
    private String group;

    /**
     * 接口全限定名
     */
    private String interfaces;

    /**
     * ip
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    public ServiceMeta() {

    }

    public ServiceMeta(String group, String interfaces, String host, Integer port) {
        this.group = group;
        this.interfaces = interfaces;
        this.host = host;
        this.port = port;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(String interfaces) {
        this.interfaces = interfaces;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
