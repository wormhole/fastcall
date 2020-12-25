### 服务发现与服务调用

#### 一、服务发现（了解即可）
不管是对于`Spring`应用，还是`非Spring`应用，服务发现的过程对于使用者来说是完全屏蔽和自动化的。服务发现过程由**服务注册子系统**控制，通过框架源码，
我们可以看到在框架实例化**服务注册子系统**的过程中，调用了**服务注册子系统**的`subscribe()`方法，该方法会遍历注册中心上的所有服务缓存到本地，
并对服务进行订阅，这样有任何改动，就会通知**服务注册子系统**，对本地缓存进行更新。

#### 二、服务调用
框架采用了代理模式，在服务消费端，会生成远程服务调用接口的动态代理对象，接下来我们就可以调用这个代理对象，就像调用本地方法一样来进行远程调用了，所以我们要知道如何通过框架生成这个代理对象。

##### 1、在`Spring`应用中使用
框架提供了注解`@FastcallReference`，功能有点类似于`@Autowired`，不过`@Autowired`是将容器中的`bean`赋值给字段，而`@FastcallReference`是生成动态代理对象后，赋值给字段。
将它注解在类的字段上面，框架启动时，`fastcall-spring-boot-autoconfigure`模块会自动扫描该注解，生成动态代理对象，并将代理对象赋值给字段引用，接下来我们就可以通过该引用使用代理对象，查看源码可以看到该注解有以下几个属性。
* group: 分组，指明远程服务的分组
* version: 版本，指明远程服务的版本
* timeout: 超时时间，指明调用的超时时间（具体使用会在`调用超时与重试`章节中说明）
* fallback: 服务降级类（具体使用会在`服务降级`章节中说明）
```
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FastcallReference {

    String group() default "default";

    String version() default "";

    long timeout() default -1;

    Class<?> fallback() default Void.class;
}
```

##### 2、在`非Spring`应用中生成代理对象
依旧需要用到我们强大的外观类`FastcallManager`，外观类中提供了生成代理对象的方法`createProxy`，我们来看一下源码，它是一个泛型方法，实际上它调用了`RpcProxyFactory`工厂类，我们来对参数进行说明。
* clazz: 远程调用的服务接口
* group: 指定远程服务的分组
* version: 指定远程服务的版本
* timeout: 指定调用的超时时间（具体使用会在`调用超时与重试`章节中说明）
* fallback: 服务降级类（具体使用会在`服务降级`章节中说明）
```
@Override
public <T> T createProxy(Class<T> clazz, String group, String version, Long timeout, Class<?> fallback) {
    return RpcProxyFactory.create(this, clazz, group, version, timeout, fallback);
}
```

生成代理对象，并进行远程调用
```
//实例化配置类（亦可通过FastcallConfigBuilder建造者，一步一步生成配置类）
FastcallConfig config = new FastcallConfig();
//如有必要，修改默认配置（序列化方式，负载均衡策略，注册中心类型地址等）
...

//生成工厂类，并由工厂类生成DefaultFastcallManager的单例对象
FastcallManagerFactory factory = new ConfigFastcallManagerFactory(config);
FastcallManager manager = factory.getInstance();

//生成代理对象
SayService proxy = manager.createProxy(SayService.class, "group-1", "1.0", 5000, Void.class);

//远程调用
proxy.say("hello world");
```