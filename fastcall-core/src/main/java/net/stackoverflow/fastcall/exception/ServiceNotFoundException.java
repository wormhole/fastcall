package net.stackoverflow.fastcall.exception;

/**
 * 未找到远程服务异常类
 *
 * @author wormhole
 */
public class ServiceNotFoundException extends RuntimeException {

    private final String interfaceName;

    private final String group;

    private final String version;

    public ServiceNotFoundException(String interfaceName, String group, String version) {
        super();
        this.interfaceName = interfaceName;
        this.group = group;
        this.version = version;
    }

    public ServiceNotFoundException(String interfaceName, String group, String version, String msg) {
        super(msg);
        this.interfaceName = interfaceName;
        this.group = group;
        this.version = version;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getGroup() {
        return group;
    }

    public String getVersion() {
        return version;
    }
}
