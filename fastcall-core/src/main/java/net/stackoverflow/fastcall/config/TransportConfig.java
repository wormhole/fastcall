package net.stackoverflow.fastcall.config;

public class TransportConfig {

    private String proto = "fastcall";

    private String host = "0.0.0.0";

    private Integer port = 9966;

    public String getProto() {
        return proto;
    }

    public void setProto(String proto) {
        this.proto = proto;
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

}
