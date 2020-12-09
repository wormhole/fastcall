package net.stackoverflow.fastcall.config;

import net.stackoverflow.fastcall.registry.JsonUtils;

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

    public FastcallConfigBuilder setSerialize(String serialize) {
        this.config.setSerialize(serialize);
        return this;
    }

    public FastcallConfigBuilder setProviderConfig(ProviderConfig providerConfig) {
        this.config.setProvider(providerConfig);
        return this;
    }

    public FastcallConfigBuilder setConsumerConfig(ConsumerConfig consumerConfig) {
        this.config.setConsumer(consumerConfig);
        return this;
    }

    public FastcallConfigBuilder setRegistry(RegistryConfig registryConfig) {
        this.config.setRegistry(registryConfig);
        return this;
    }

    public FastcallConfig build() {
        return this.config;
    }
}
