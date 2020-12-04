package net.stackoverflow.fastcall.exception;

/**
 * 未找到远程服务
 *
 * @author wormhole
 */
public class ServiceNotFoundException extends Exception {

    public ServiceNotFoundException() {
        super();
    }

    public ServiceNotFoundException(String msg) {
        super(msg);
    }
}
