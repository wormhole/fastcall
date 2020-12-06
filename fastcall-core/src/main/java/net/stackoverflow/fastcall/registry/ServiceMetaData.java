package net.stackoverflow.fastcall.registry;

/**
 * 服务类元数据
 *
 * @author wormhole
 */
public class ServiceMetaData {

    private String group;

    private String interfaceName;

    private String host;

    private Integer port;

    public ServiceMetaData() {

    }

    /**
     * 构造方法
     *
     * @param group         分组
     * @param interfaceName 接口名称
     * @param host          服务ip
     * @param port          服务端口
     */
    public ServiceMetaData(String group, String interfaceName, String host, Integer port) {
        this.group = group;
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
        final StringBuffer sb = new StringBuffer("ServiceMetaData{");
        sb.append("group='").append(group).append('\'');
        sb.append(", interfaceName='").append(interfaceName).append('\'');
        sb.append(", host='").append(host).append('\'');
        sb.append(", port=").append(port);
        sb.append('}');
        return sb.toString();
    }
}
