package net.stackoverflow.fastcall.registry;

import net.stackoverflow.fastcall.exception.ServiceNotFoundException;

import java.util.List;

/**
 * 注册中心抽象类
 *
 * @author wormhole
 */
public abstract class AbstractRegistryManager implements RegistryManager {

    protected final RegistryCache cache;

    public AbstractRegistryManager() {
        this.cache = new RegistryCache();
    }

    /**
     * 获取服务地址
     *
     * @param clazz   接口Class对象
     * @param group   所属分组
     * @param version 版本号
     * @return
     */
    @Override
    public List<ServiceDefinition> getService(Class<?> clazz, String group, String version) {
        List<ServiceDefinition> definitions = cache.get(clazz.getName(), group, version);
        if (definitions != null && definitions.size() > 0) {
            return definitions;
        } else {
            throw new ServiceNotFoundException(clazz.getName(), group, String.format("Service not found, interfaceName:%s, group:%s, version:%s", clazz.getName(), group, version));
        }
    }
}
