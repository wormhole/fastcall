package net.stackoverflow.fastcall.context;

import net.stackoverflow.fastcall.exception.BeanNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Bean容器
 *
 * @author wormhole
 */
public class BeanContext {

    private static Map<String, Object> beans = new HashMap<>();

    public static void setBean(String name, Object bean) {
        BeanContext.beans.put(name, bean);
    }

    public static void setBean(Class<?> clazz, Object bean) {
        BeanContext.setBean(clazz.getName(), bean);
    }

    public static Object getBean(String name) {
        return beans.get(name);
    }

    public static <T> T getBean(Class<?> clazz) {
        return (T) BeanContext.getBean(clazz.getName());
    }
}
