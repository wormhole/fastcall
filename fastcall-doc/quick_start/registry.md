### 服务注册

上一章中详细地介绍了框架使用的基础知识和`rpc`调用的基本流程，从这一章开始，我们将介绍如何使用`fastcall`框架来来进行服务注册和服务发现

#### 一、`@FastcallService`注解
`@FastcallService`是框架提供的一个注解，它注解在服务实现类上，标识这是一个需要暴露的服务，通过查看源码，可以发现他有以下几个属性
* group: 服务分组，当同时存在多个同样的服务实现类时，可以对服务进行分组，来区分具体对哪个服务进行调用
* version: 服务的版本，当有新的服务上线时，在过渡期间，我们往往需要老版本的服务和新版本的服务同时对外提供服务，我们可以通过指定服务的版本号来区分具体调用哪个服务
```
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FastcallService {

    String group() default "default";

    String version() default "";
}
```

#### 二、`Spring`应用中进行服务注册
上一章我们提到，在服务提供者的启动类上，我们需要添加`@EnableFastcall`注解，该注解的作用类似于`@ComponentScan`。
实际上，该注解会从指定的扫描路径中去扫描`@FastcallService`指定的服务类，并自动将他们注册为`bean`，同时将服务的元数据信息注册到
服务注册中心，告诉服务消费者，该应用中有哪些服务可供调用。一切都是自动配置的，就是那么简单，仅仅只需要在服务实现类上添加一个注解而已，
做到了对业务的***低入侵***。
```
@FastcallService(group = "group-1", version="1.0")
public class SayServiceImpl implements SayService {

    private static final Logger log = LoggerFactory.getLogger(SayServiceImpl.class);

    @Override
    public String say(String content) {
        return content;
    }
}
```

#### 三、在`非Spring`应用中进行服务注册
对于`非Spring`应用的支持一切来源于框架提供的外观类`FastcallManager`，外观类中提供了`void register(Class<?> clazz, Object bean);`方法，第一个参数指明了服务的接口，第二个参数是服务的实例化对象。
以下是在`非Spring`应用中进行服务注册的具体的代码。
```
//实例化配置类（亦可通过FastcallConfigBuilder建造者，一步一步生成配置类）
FastcallConfig config = new FastcallConfig();
//修改默认配置（此处启用了服务提供子系统）
config.getProvider().setEnabled(true);

//生成工厂类，并由工厂类生成DefaultFastcallManager的单例对象
FastcallManagerFactory factory = new ConfigFastcallManagerFactory(config);
FastcallManager manager = factory.getInstance();

//向服务注册中心注册需要暴露的服务
mamager.register(SayService.class, new SayServiceImpl());

//启动服务
manager.start();
```