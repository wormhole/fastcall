package net.stackoverflow.fastcall.config;

/**
 * Fastcall总配置类
 *
 * @author wormhole
 */
public class FastcallConfig {

    private String serialize = "json";

    private ProviderConfig provider = new ProviderConfig();

    private ConsumerConfig consumer = new ConsumerConfig();

    private RegistryConfig registry = new RegistryConfig();

    public String getSerialize() {
        return serialize;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }

    public ProviderConfig getProvider() {
        return provider;
    }

    public void setProvider(ProviderConfig provider) {
        this.provider = provider;
    }

    public ConsumerConfig getConsumer() {
        return consumer;
    }

    public void setConsumer(ConsumerConfig consumer) {
        this.consumer = consumer;
    }

    public void setRegistry(RegistryConfig registry) {
        this.registry = registry;
    }

    public RegistryConfig getRegistry() {
        return registry;
    }
}
