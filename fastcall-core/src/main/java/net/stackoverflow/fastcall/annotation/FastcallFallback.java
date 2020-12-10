package net.stackoverflow.fastcall.annotation;

import java.lang.annotation.*;

/**
 * 服务降级注解
 *
 * @author wormhole
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FastcallFallback {

    String method();
}
