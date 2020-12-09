package net.stackoverflow.fastcall.exception;

/**
 * 未找到远程服务异常类
 *
 * @author wormhole
 */
public class ServiceNotFoundException extends RuntimeException {

    private final String interfaceName;

    private final String group;

    public ServiceNotFoundException(String interfaceName, String group) {
        super();
        this.interfaceName = interfaceName;
        this.group = group;
    }

    public ServiceNotFoundException(String interfaceName, String group, String msg) {
        super(msg);
        this.interfaceName = interfaceName;
        this.group = group;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getGroup() {
        return group;
    }

}
