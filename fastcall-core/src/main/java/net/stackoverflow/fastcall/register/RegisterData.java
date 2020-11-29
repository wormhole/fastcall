package net.stackoverflow.fastcall.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注册中心数据
 *
 * @author wormhole
 */
public class RegisterData {

    private Map<String, List<Address>> route;

    public RegisterData() {
        route = new HashMap<>();
    }

    public Map<String, List<Address>> getRoute() {
        return route;
    }

    public void setRoute(Map<String, List<Address>> route) {
        this.route = route;
    }

    public void addRoute(String group, String host, Integer port) {
        List<Address> addresses = null;
        if (route.get(group) == null) {
            addresses = new ArrayList<>();
            route.put(group, addresses);
        } else {
            addresses = route.get(group);
        }
        Address address = new Address(host, port);
        if (!addresses.contains(address)) {
            addresses.add(new Address(host, port));
        }
    }

    /**
     * 路由地址
     */
    private static class Address {
        /**
         * ip地址
         */
        private String host;

        /**
         * 端口
         */
        private Integer port;

        public Address() {

        }

        public Address(String host, Integer port) {
            this.host = host;
            this.port = port;
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

        @Override
        public boolean equals(Object obj) {
            Address address = (Address) obj;
            if (this.host != null && this.port != null) {
                return this.host.equals(address.host) && this.port.equals(address.port);
            } else if (this.host != null && this.port == null) {
                return this.host.equals(address.host) && (address.port == null);
            } else if (this.host == null && this.port != null) {
                return (address.host == null) && this.port.equals(address.port);
            } else {
                return (address.host == null) && (address.port == null);
            }
        }
    }
}
