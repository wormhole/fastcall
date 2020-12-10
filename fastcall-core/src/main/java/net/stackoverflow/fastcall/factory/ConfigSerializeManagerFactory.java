package net.stackoverflow.fastcall.factory;

import net.stackoverflow.fastcall.serialize.JsonSerializeManager;
import net.stackoverflow.fastcall.serialize.SerializeManager;

/**
 * Serialize工厂类
 *
 * @author wormhole
 */
public class ConfigSerializeManagerFactory implements SerializeManagerFactory {

    private final String type;

    private SerializeManager serializeManager;

    public ConfigSerializeManagerFactory() {
        this.type = "json";
    }

    public ConfigSerializeManagerFactory(String type) {
        this.type = type;
    }

    @Override
    public SerializeManager getInstance() {
        if (serializeManager == null) {
            synchronized (this) {
                if (serializeManager == null) {
                    if ("json".equals(type)) {
                        this.serializeManager = new JsonSerializeManager();
                    }
                }
            }
        }
        return this.serializeManager;
    }
}
