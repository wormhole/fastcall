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
public class RegistryData {

    private Map<String, List<RouteAddress>> route;

    public RegistryData() {
        route = new HashMap<>();
    }

    public Map<String, List<RouteAddress>> getRoute() {
        return route;
    }

    public void setRoute(Map<String, List<RouteAddress>> route) {
        this.route = route;
    }

    /**
     * 添加路由地址
     *
     * @param group 分组
     * @param host  ip地址
     * @param port  端口
     */
    public void addRouteAddress(String group, String host, Integer port) {
        List<RouteAddress> routeAddresses = null;
        if (route.get(group) == null) {
            routeAddresses = new ArrayList<>();
            route.put(group, routeAddresses);
        } else {
            routeAddresses = route.get(group);
        }
        RouteAddress routeAddress = new RouteAddress(host, port);
        if (!routeAddresses.contains(routeAddress)) {
            routeAddresses.add(new RouteAddress(host, port));
        }
    }

    /**
     * 路由地址
     */
    public static class RouteAddress {

        private String host;

        private Integer port;

        public RouteAddress() {

        }

        /**
         * 路由地址
         *
         * @param host IP地址
         * @param port 端口
         */
        public RouteAddress(String host, Integer port) {
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
            RouteAddress routeAddress = (RouteAddress) obj;
            if (this.host != null && this.port != null) {
                return this.host.equals(routeAddress.host) && this.port.equals(routeAddress.port);
            } else if (this.host != null && this.port == null) {
                return this.host.equals(routeAddress.host) && (routeAddress.port == null);
            } else if (this.host == null && this.port != null) {
                return (routeAddress.host == null) && this.port.equals(routeAddress.port);
            } else {
                return (routeAddress.host == null) && (routeAddress.port == null);
            }
        }
    }
}
