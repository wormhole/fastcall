package net.stackoverflow.fastcall.config;

/**
 * 注册中心配置
 *
 * @author wormhole
 */
public class RegistryConfig {

    private String type = "zookeeper";

    private ZooKeeperConfig zookeeper = new ZooKeeperConfig();

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

    /**
     * zookeeper配置
     *
     * @wormhole
     */
    public static class ZooKeeperConfig {
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
}
