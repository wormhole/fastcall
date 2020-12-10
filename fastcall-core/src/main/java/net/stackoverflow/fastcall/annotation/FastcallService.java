package net.stackoverflow.fastcall.annotation;

import java.lang.annotation.*;

/**
 * 服务类注册
 *
 * @author wormhole
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FastcallService {

    String group() default "default";

    String fallback() default "";
}
