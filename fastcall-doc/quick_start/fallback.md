### 服务降级

#### 一、说明
当服务调用出现异常或服务调用方不可用时，如果不指定服务降级方法，将会抛出异常，如果指定了降级方法，则不会抛出异常，而是执行本地的降级方法。

#### 二、服务降级配置
框架提供`@FastcallFallback`注解，该注解可以加在需要降级的方法上，并指定属性`method`，该属性指明了发生异常后，执行的降级方法。
```
@Override
@FastcallFallback(method = "fallback")
public String methodWithFallback(String content) {
    throw new RuntimeException();
}

public String fallback(String content) {
    return "method fallback";
}
```