package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.ConsumerManager;
import net.stackoverflow.fastcall.annotation.FastcallReference;
import net.stackoverflow.fastcall.factory.RpcProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;

/**
 * 将@FastcallReference注解的字段生成动态代理对象
 *
 * @author wormhole
 */
public class FastcallBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(FastcallBeanPostProcessor.class);

    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz;
        if (AopUtils.isAopProxy(bean)) {
            clazz = AopUtils.getTargetClass(bean);
        } else {
            clazz = bean.getClass();
        }
        ConsumerManager consumerManager = applicationContext.getBean(ConsumerManager.class);
        for (Field field : clazz.getDeclaredFields()) {
            FastcallReference reference = field.getAnnotation(FastcallReference.class);
            if (reference != null) {
                try {
                    field.setAccessible(true);
                    field.set(bean, RpcProxyFactory.create(field.getType(), reference.group(), reference.version(), reference.timeout(), reference.fallback(), consumerManager));
                } catch (IllegalAccessException e) {
                    log.error("BeanPostProcessor fail to set field", e);
                }
            }
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
