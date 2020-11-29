package net.stackoverflow.fastcall.model;

public class ServiceMeta {

    private String group;

    private String interfaces;

    public ServiceMeta() {

    }

    public ServiceMeta(String group, String interfaces) {
        this.group = group;
        this.interfaces = interfaces;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(String interfaces) {
        this.interfaces = interfaces;
    }
}
