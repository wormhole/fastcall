package net.stackoverflow.fastcall.exception;

/**
 * 未找到bean，用于Provider端
 *
 * @author wormhole
 */
public class BeanNotFoundException extends Exception {

    public BeanNotFoundException() {
        super();
    }

    public BeanNotFoundException(String msg) {
        super(msg);
    }
}
