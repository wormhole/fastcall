package net.stackoverflow.fastcall.config;

import net.stackoverflow.fastcall.util.JsonUtils;

/**
 * Fastcall配置建造者类
 *
 * @author wormhole
 */
public class FastcallConfigBuilder {

    private FastcallConfig config;

    public FastcallConfigBuilder() {
        this.config = new FastcallConfig();
    }

    public FastcallConfigBuilder setConfig(String json) {
        this.config = JsonUtils.json2bean(json, FastcallConfig.class);
        return this;
    }

    public FastcallConfig build() {
        return this.config;
    }
}
