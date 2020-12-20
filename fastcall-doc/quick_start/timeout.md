### 调用超时与重试

服务调用时可能会出现超时情况，超时的情况有很多，可能由于服务本身是个耗时的任务，也可能由于服务端业务线程池的参数设置过小，导致任务等待或抛弃，默认的超时策略是永不超时一直等待的，为了
性能考虑，建议设置超时时间和重试次数，一旦发生超时，如果有可用的重试次数，将会对`rpc`调用进行重试，直到重试次数用尽，抛出`RpcTimeoutException`。（每次重试，都会重新由负载均衡策略重新
选择地址，并不会一直重试同一个地址）

#### 一、在`Springboot`应用中设置超时与重试
对于`Springboot`应用，在注解`@FastcallReference`上指定`timeout`属性即可，单位为毫秒，并且在配置文件`application.properties`中指定`fastcall.consumer.retry`的值即可设置重试次数，
特别的，超时只对`@FastcallReference`注解的服务接口生效，而重试是对该服务消费者所引用的所有的远程调用生效。
```
@RestController
public class FastcallController {

    //在服务接口引用处添加注解，并设置timeout属性
    @FastcallReference(group = "group-1", version = "0.0.1", timeout = 5000)
    private SayService sayService;

    @GetMapping("/say")
    public String say(@RequestParam("content") String content) {
        return sayService.say(content);
    }
}
```

#### 二、在非`Spring`应用中设置超时与重试
```
//生成配置类，并设置重试次数
FastcallConfig config = new FastcallConfig();
config.getConsumer().setRetry(1);
//生成工厂类对象
FastcallManagerFactory factory = new ConfigFastcallManagerFactory(config);
//生成外观类对象
FastcallManager manager = factory.getInstance();
//生成代理对象，指定分组，版本，超时时间
SayService proxy = manager.createProxy(SayService.class, "group-1", "1.0", 5000);
```