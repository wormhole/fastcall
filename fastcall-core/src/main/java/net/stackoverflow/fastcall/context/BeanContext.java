package net.stackoverflow.fastcall.context;

import net.stackoverflow.fastcall.annotation.FastcallService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Bean容器
 *
 * @author wormhole
 */
public class BeanContext {

    private final Map<String, Set<Object>> beans;

    public BeanContext() {
        this.beans = new HashMap<>();
    }

    public synchronized void setBean(String name, Object bean) {
        Set<Object> set = beans.get(name);
        if (set == null) {
            set = new HashSet<>();
        }
        set.add(bean);
        beans.put(name, set);
    }

    public synchronized void setBean(Class<?> clazz, Object bean) {
        this.setBean(clazz.getName(), bean);
    }

    public Object getBean(String name, String group, String version) {
        Set<Object> set = beans.get(name);
        if (set != null) {
            for (Object obj : set) {
                Class<?> clazz = obj.getClass();
                FastcallService fastcallService = clazz.getAnnotation(FastcallService.class);
                if (fastcallService != null) {
                    String g = fastcallService.group();
                    String v = fastcallService.version();
                    if (g.equals(group) && v.equals(version)) {
                        return obj;
                    }
                }
            }
        }
        return null;
    }

    public <T> T getBean(Class<?> clazz, String group, String version) {
        return (T) this.getBean(clazz.getName(), group, version);
    }
}
