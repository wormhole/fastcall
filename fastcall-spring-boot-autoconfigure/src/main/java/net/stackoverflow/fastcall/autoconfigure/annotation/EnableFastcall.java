package net.stackoverflow.fastcall.autoconfigure.annotation;

import net.stackoverflow.fastcall.autoconfigure.FastcallServiceRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FastcallServiceRegistrar.class)
public @interface EnableFastcall {

    @AliasFor("basePackages") String[] value() default {};

    @AliasFor("value") String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};
}
