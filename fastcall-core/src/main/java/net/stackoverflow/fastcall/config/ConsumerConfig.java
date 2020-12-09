package net.stackoverflow.fastcall.config;

/**
 * Consumer 配置类
 *
 * @author wormhole
 */
public class ConsumerConfig {
    private Integer timeout = 60;

    private Integer threads = 512;

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }
}
