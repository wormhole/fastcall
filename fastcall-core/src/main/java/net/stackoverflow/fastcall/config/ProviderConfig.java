package net.stackoverflow.fastcall.config;

/**
 * Provider配置
 *
 * @author wormhole
 */
public class ProviderConfig {
    private Boolean enabled = false;

    private Integer backlog = 1024;

    private Integer timeout = 60;

    private String host = "0.0.0.0";

    private Integer port = 9966;

    private Integer threads = 100;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }
}
