package net.stackoverflow.fastcall.config;

/**
 * 注册中心配置类
 *
 * @author wormhole
 */
public class RegistryConfig {

    private String type = "zookeeper";

    private ZooKeeperConfig zookeeper = new ZooKeeperConfig();

    private RedisConfig redis = new RedisConfig();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ZooKeeperConfig getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(ZooKeeperConfig zookeeper) {
        this.zookeeper = zookeeper;
    }

    public RedisConfig getRedis() {
        return redis;
    }

    public void setRedis(RedisConfig redis) {
        this.redis = redis;
    }

    /**
     * zookeeper配置
     */
    public static class ZooKeeperConfig {
        private String address = "127.0.0.1:2181";

        private Integer sessionTimeout = 5000;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Integer getSessionTimeout() {
            return sessionTimeout;
        }

        public void setSessionTimeout(Integer sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
        }
    }

    /**
     * redis配置
     */
    public static class RedisConfig {
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
}
