package net.stackoverflow.fastcall.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * fastcall配置属性类
 *
 * @author wormhole
 */
@ConfigurationProperties(prefix = "fastcall")
public class FastcallProperties {

    private String serialize = "json";

    private Provider provider = new Provider();

    private Consumer consumer = new Consumer();

    private Registry registry = new Registry();

    public static class Provider {
        private Boolean enabled = false;

        private Integer backlog = 1024;

        private Integer timeout = 60;

        private String host = "0.0.0.0";

        private Integer port = 9966;

        private Integer threads = Integer.MAX_VALUE;

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

    public static class Consumer {
        private Integer timeout = 60;

        private Integer maxConnection = Integer.MAX_VALUE;

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

    public static class Registry {
        private String type = "zookeeper";

        private Zookeeper zookeeper = new Zookeeper();

        private Redis redis = new Redis();

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Zookeeper getZookeeper() {
            return zookeeper;
        }

        public void setZookeeper(Zookeeper zookeeper) {
            this.zookeeper = zookeeper;
        }

        public Redis getRedis() {
            return redis;
        }

        public void setRedis(Redis redis) {
            this.redis = redis;
        }
    }

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

    public static class Redis {
        private String host = "127.0.0.1";

        private Integer port = 6379;

        private Integer timeout = 10000;

        private String password;

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

        public Integer getTimeout() {
            return timeout;
        }

        public void setTimeout(Integer timeout) {
            this.timeout = timeout;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public String getSerialize() {
        return serialize;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
}
