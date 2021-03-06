package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.FastcallFacade;
import net.stackoverflow.fastcall.annotation.FastcallService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * fastcall生命周期
 *
 * @author wormhole
 */
public class FastcallLifecycle implements DisposableBean, InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        this.registerService();
        this.start();
    }

    @Override
    public void destroy() {
        FastcallFacade fastcallFacade = applicationContext.getBean(FastcallFacade.class);
        fastcallFacade.stop();
    }

    /**
     * 向注册中心注册服务信息
     */
    private void registerService() {
        FastcallFacade fastcallFacade = applicationContext.getBean(FastcallFacade.class);
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(FastcallService.class);
        for (Object obj : map.values()) {
            Class<?> clazz = obj.getClass();
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> itf : interfaces) {
                fastcallFacade.register(itf, obj);
            }
        }
    }

    /**
     * 绑定服务端
     */
    private void start() {
        FastcallFacade fastcallFacade = applicationContext.getBean(FastcallFacade.class);
        fastcallFacade.start();
    }
}
