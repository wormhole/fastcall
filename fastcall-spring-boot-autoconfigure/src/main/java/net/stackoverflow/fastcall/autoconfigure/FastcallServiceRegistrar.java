package net.stackoverflow.fastcall.autoconfigure;

import net.stackoverflow.fastcall.annotation.FastcallService;
import net.stackoverflow.fastcall.autoconfigure.annotation.EnableFastcall;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 注册@FastcallService注解的类
 *
 * @author wormhole
 */
public class FastcallServiceRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, false);
        AnnotationTypeFilter fastcallServiceType = new AnnotationTypeFilter(FastcallService.class);
        scanner.addIncludeFilter(fastcallServiceType);
        Set<String> packagesToScan = this.getScanPackage(importingClassMetadata);
        scanner.scan(packagesToScan.toArray(new String[]{}));
    }

    private Set<String> getScanPackage(AnnotationMetadata annotationMetadata) {
        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(EnableFastcall.class.getName());
        String[] basePackages = (String[]) attributes.get("basePackages");
        Class<?>[] basePackageClasses = (Class<?>[]) attributes.get("basePackageClasses");
        Set<String> packages = new HashSet<>(Arrays.asList(basePackages));
        for (Class<?> basePackageClass : basePackageClasses) {
            packages.add(ClassUtils.getPackageName(basePackageClass));
        }
        if (packages.isEmpty()) {
            packages.add(ClassUtils.getPackageName(annotationMetadata.getClassName()));
        }
        return packages;
    }
}
