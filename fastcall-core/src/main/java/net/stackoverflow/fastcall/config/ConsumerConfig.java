package net.stackoverflow.fastcall.config;

/**
 * Consumer 配置类
 *
 * @author wormhole
 */
public class ConsumerConfig {
    private Integer timeout = 60;

    private Integer maxConnection = 512;

    private Integer retry = 0;

    private String balance = "random";

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getMaxConnection() {
        return maxConnection;
    }

    public void setMaxConnection(Integer maxConnection) {
        this.maxConnection = maxConnection;
    }

    public Integer getRetry() {
        return retry;
    }

    public void setRetry(Integer retry) {
        this.retry = retry;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
