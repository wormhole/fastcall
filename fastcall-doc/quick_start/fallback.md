### 服务降级

当服务调用出现异常或服务调用方不可用时，如果不指定服务降级方法，将会抛出异常，为了防止异常的出现，我们可以在消费端本地，继承远程服务的接口，做一个本地的降级实现，
当服务调用出现异常时，框架会转到本地的服务实现类中，执行降级方法（降级方法应该尽量避免出现异常）

#### 一、在`Spring`应用中配置服务降级

1. 首先在服务消费端，继承调用接口，实现远程调用方法的本地实现
```
public class FallbackSayServiceImpl implements SayService {
    @Override
    public String sayWithFallback(String content) {
        return "fallback " + content;
    }
}
```

2. 在接口的引用上添加注解`@FastcallReference`注解，并将`fallback`属性，指定为接口本地实现的`Class`对象
```
@RestController
public class FastcallController {

    @FastcallReference(group = "group-1", version = "0.0.1", timeout = 5000, fallback = FallbackSayServiceImpl.class)
    private SayService sayService;

    @GetMapping("/say_with_fallback")
    public String sayWithFallback(@RequestParam("content") String content) {
        return sayService.sayWithFallback(content);
    }
}
```

#### 二、在`非Spring`应用中配置服务降级

对于非`Spring`应用，在创建代理对象时，指定本地实现的`Class`对象
```
//利用工厂类生成外观类对象
FastcallConfig config = new FastcallConfig();
FastcallManagerFactory factory = new ConfigFastcallManagerFactory(config);
FastcallManager manager = factory.getInstance();
//生成代理对象，指定分组，版本，超时时间，降级类
SayService proxy = manager.createProxy(SayService.class, "group-1", "0.0.1", 5000, FallbackSayServiceImpl.class);
```
