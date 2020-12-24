package net.stackoverflow.fastcall.config;

/**
 * Fastcall总配置类
 *
 * @author wormhole
 */
public class FastcallConfig {

    private String serialize = "json";

    private String balance = "random";

    private Integer retry = 0;

    private Integer threads = Integer.MAX_VALUE;

    private RegistryConfig registry = new RegistryConfig();

    private TransportConfig transport = new TransportConfig();

    public String getSerialize() {
        return serialize;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public Integer getRetry() {
        return retry;
    }

    public void setRetry(Integer retry) {
        this.retry = retry;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public void setRegistry(RegistryConfig registry) {
        this.registry = registry;
    }

    public RegistryConfig getRegistry() {
        return registry;
    }

    public TransportConfig getTransport() {
        return transport;
    }

    public void setTransport(TransportConfig transport) {
        this.transport = transport;
    }
}
