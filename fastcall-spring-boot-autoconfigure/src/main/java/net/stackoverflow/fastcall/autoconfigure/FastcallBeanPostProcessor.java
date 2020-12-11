package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.FastcallManager;
import net.stackoverflow.fastcall.annotation.FastcallReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * 将@FastcallReference注解的字段生成动态代理对象
 *
 * @author wormhole
 */
public class FastcallBeanPostProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(FastcallBeanPostProcessor.class);

    private final FastcallManager fastcallManager;

    public FastcallBeanPostProcessor(FastcallManager fastcallManager) {
        this.fastcallManager = fastcallManager;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz;
        if (AopUtils.isAopProxy(bean)) {
            clazz = AopUtils.getTargetClass(bean);
        } else {
            clazz = bean.getClass();
        }

        for (Field field : clazz.getDeclaredFields()) {
            FastcallReference reference = field.getAnnotation(FastcallReference.class);
            if (reference != null) {
                try {
                    field.setAccessible(true);
                    field.set(bean, fastcallManager.createProxy(field.getType(), reference.group(), reference.version(), reference.timeout()));
                } catch (IllegalAccessException e) {
                    log.error("BeanPostProcessor fail to set field", e);
                }
            }
        }
        return bean;
    }
}
