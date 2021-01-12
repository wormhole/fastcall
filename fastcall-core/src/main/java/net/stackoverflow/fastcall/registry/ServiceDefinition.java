package net.stackoverflow.fastcall.registry;

/**
 * 服务元数据
 *
 * @author wormhole
 */
public class ServiceDefinition {

    private String group;

    private String version;

    private String interfaceName;

    private String host;

    private Integer port;

    public ServiceDefinition() {

    }

    /**
     * 构造方法
     *
     * @param group         分组
     * @param interfaceName 接口名称
     * @param version       版本号
     * @param host          服务ip
     * @param port          服务端口
     */
    public ServiceDefinition(String group, String version, String interfaceName, String host, Integer port) {
        this.group = group;
        this.version = version;
        this.interfaceName = interfaceName;
        this.host = host;
        this.port = port;
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

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ServiceDefinition{");
        sb.append("group='").append(group).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", interfaceName='").append(interfaceName).append('\'');
        sb.append(", host='").append(host).append('\'');
        sb.append(", port=").append(port);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        ServiceDefinition other = (ServiceDefinition) obj;
        return group.equals(other.group) && version.equals(other.version) && interfaceName.equals(other.interfaceName) && host.equals(other.host) && port.equals(other.port);
    }
}
