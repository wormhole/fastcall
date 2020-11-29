package net.stackoverflow.fastcall.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZkData {

    Map<String, List<Address>> route;

    public ZkData() {
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
        } else {
            addresses = route.get(group);
        }
        addresses.add(new Address(host, port));
    }
}
