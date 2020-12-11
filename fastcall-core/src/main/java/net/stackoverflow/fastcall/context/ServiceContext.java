package net.stackoverflow.fastcall.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Bean容器
 *
 * @author wormhole
 */
public class ServiceContext {

    private final Map<String, Object> beans;

    public ServiceContext() {
        this.beans = new HashMap<>();
    }

    public void setBean(String name, Object bean) {
        beans.put(name, bean);
    }

    public void setBean(Class<?> clazz, Object bean) {
        this.setBean(clazz.getName(), bean);
    }

    public Object getBean(String name) {
        return beans.get(name);
    }

    public <T> T getBean(Class<?> clazz) {
        return (T) this.getBean(clazz.getName());
    }
}
