package net.stackoverflow.fastcall.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * fastcall配置属性类
 *
 * @author wormhole
 */
@ConfigurationProperties(prefix = "fastcall")
public class FastcallProperties {

    private Integer backlog = 1024;

    private Integer timeout = 60;

    private String host = "0.0.0.0";

    private Integer port = 9966;

    private String register = "zookeeper";

    private Zookeeper zookeeper = new Zookeeper();

    public static class Zookeeper {
        private String host = "127.0.0.1";

        private Integer port = 2181;

        private Integer sessionTimeout = 5000;

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

        public Integer getSessionTimeout() {
            return sessionTimeout;
        }

        public void setSessionTimeout(Integer sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
        }
    }

    public Integer getBacklog() {
        return backlog;
    }

    public void setBacklog(Integer backlog) {
        this.backlog = backlog;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
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

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public Zookeeper getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(Zookeeper zookeeper) {
        this.zookeeper = zookeeper;
    }
}