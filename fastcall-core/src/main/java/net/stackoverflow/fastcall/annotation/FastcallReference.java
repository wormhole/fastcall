package net.stackoverflow.fastcall.annotation;

import java.lang.annotation.*;

/**
 * 服务引用注解
 *
 * @author wormhole
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FastcallReference {

    String group() default "default";

    String version() default "";

    long timeout() default -1;
}
