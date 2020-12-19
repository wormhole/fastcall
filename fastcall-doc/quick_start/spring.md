### 在Spring应用中使用

#### 一、对于服务提供者`Provider` [【样例代码】](https://github.com/wormhole/fastcall/tree/master/fastcall-demo-provider)

##### 1. 新建`Spring boot`项目，并添加`maven`依赖，`fastcall-demo-api`为`Provider`和`Consumer`公共依赖的服务调用接口
```
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <dependency>
        <groupId>net.stackoverflow.fastcall</groupId>
        <artifactId>fastcall-spring-boot-starter</artifactId>
        <version>${fastcall.version}</version>
    </dependency>

    <dependency>
        <groupId>net.stackoverflow.fastcall</groupId>
        <artifactId>fastcall-demo-api</artifactId>
        <version>${fastcall.version}</version>
    </dependency>
</dependencies>
```

##### 2. `application.properties`配置信息（具体含义在[3.6. 详细配置](properties.html)章节中介绍），对于服务提供者，`fastcall.provider.enabled`必须指定为`true`值
```
#序列化方式，可以不指定，使用默认值
fastcall.serialize=json

#注册中心类型，可以不指定，使用默认值zookeeper
fastcall.registry.type=zookeeper
fastcall.registry.zookeeper.host=127.0.0.1
fastcall.registry.zookeeper.port=2181
fastcall.registry.zookeeper.session-timeout=5000

#启用Provider，当应用作为服务提供者时必须指定为true
fastcall.provider.enabled=true
fastcall.provider.backlog=1024
fastcall.provider.host=0.0.0.0
fastcall.provider.port=9966
fastcall.provider.timeout=60
fastcall.provider.threads=1024

#日志配置
logging.level.root=INFO
logging.level.net.stackoverflow.fastcall=DEBUG
```

##### 3. 在服务实现类上，添加`@FastcallService`注解，标识这是需要暴露的服务，`group`可以指定分组，`version`可以指定版本。添加该注解后`fastcall-spring-boot-starter`会将它自动实例化注册成`bean`，并将接口向服务注册中心注册。
```
@FastcallService(group = "group-1", version="1.0")
public class SayServiceImpl implements SayService {

    private static final Logger log = LoggerFactory.getLogger(SayServiceImpl.class);

    @Override
    public String say(String content) {
        return "hello " + content;
    }
}
```

##### 4. 在启动类上添加注解`@EnableFastcall`（必加）指定需要扫描注解的包的位置，如果没有指定`basePackages`或者`basePackageClasses`，则从启动类的包路径包括子包，作为扫描路径
```
@SpringBootApplication
@EnableFastcall(basePackages = {"net.stackoverflow.fastcall.demo.provider"})
public class FastcallDemoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastcallDemoProviderApplication.class, args);
    }

}
```

#### 二、对于服务消费者`Consumer` [【样例代码】](https://github.com/wormhole/fastcall/tree/master/fastcall-demo-consumer) 

##### 1. 新建`Spring boot`项目，并添加`maven`依赖，`fastcall-demo-api`为`Provider`和`Consumer`公共依赖的服务调用接口
```
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>net.stackoverflow.fastcall</groupId>
        <artifactId>fastcall-spring-boot-starter</artifactId>
        <version>${fastcall.version}</version>
    </dependency>

    <dependency>
        <groupId>net.stackoverflow.fastcall</groupId>
        <artifactId>fastcall-demo-api</artifactId>
        <version>${fastcall.version}</version>
    </dependency>
</dependencies>
```

##### 2. `application.properties`配置信息
```
#序列化方式，应与`Provider`保持一致
fastcall.serialize=json

#注册中心类型，可以不指定，使用默认值`zookeeper`
fastcall.registry.type=zookeeper
fastcall.registry.zookeeper.host=127.0.0.1
fastcall.registry.zookeeper.port=2181
fastcall.registry.zookeeper.session-timeout=5000

#Consumer配置
fastcall.consumer.timeout=60
fastcall.consumer.max-connection=1024
fastcall.consumer.retry=0
fastcall.consumer.balance=random

#日志配置
logging.level.root=INFO
logging.level.net.stackoverflow.fastcall=DEBUG
```

##### 3. 在接口引用上，添加注解`@FastcallReference`，`group`可以指定分组，`version`可以指定版本，`timeout`可以指定调用超时时间。添加该注解后，`fastcall-spring-boot-starter`会自动生成动态代理对象，之后可以像调用本地方法一样调用接口
```
@RestController
public class FastcallController {

    @FastcallReference(group = "group-1", version="1.0", timeout=5000)
    private SayService sayService;

    @GetMapping("/say")
    public String say(@RequestParam("content") String content) {
        return sayService.say(content);
    }
}
```