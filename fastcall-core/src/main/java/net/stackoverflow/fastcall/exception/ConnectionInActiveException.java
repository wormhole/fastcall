package net.stackoverflow.fastcall.exception;

/**
 * 连接不活跃异常
 *
 * @author wormhole
 */
public class ConnectionInActiveException extends Exception {

    private final String host;

    private final Integer port;

    public ConnectionInActiveException(String host, Integer port) {
        super();
        this.host = host;
        this.port = port;
    }

    public ConnectionInActiveException(String host, Integer port, String msg) {
        super(msg);
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

}
