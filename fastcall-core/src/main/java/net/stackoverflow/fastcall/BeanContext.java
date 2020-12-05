package net.stackoverflow.fastcall;

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

    public static void setBeans(Map<String, Object> beans) {
        BeanContext.beans = beans;
    }

    public static Object getBean(String name) throws BeanNotFoundException {
        Object bean = beans.get(name);
        if (bean == null) {
            throw new BeanNotFoundException();
        } else {
            return bean;
        }
    }

    public static <T> T getBean(Class<?> clazz) throws BeanNotFoundException {
        return (T) BeanContext.getBean(clazz.getName());
    }
}
