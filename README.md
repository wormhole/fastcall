![logo](logo.png)

[![release](https://img.shields.io/github/v/release/wormhole/fastcall)](https://github.com/wormhole/fastcall/releases)
[![build status](https://www.travis-ci.org/wormhole/fastcall.svg?branch=master)](https://www.travis-ci.org/wormhole/fastcall)
[![license](https://img.shields.io/github/license/wormhole/fastcall)](https://github.com/wormhole/fastcall/blob/master/LICENSE)
[![repo size](https://img.shields.io/github/repo-size/wormhole/fastcall)](https://github.com/wormhole/fastcall/archive/master.zip)
[![downloads](https://img.shields.io/github/downloads/wormhole/fastcall/total)](https://github.com/wormhole/fastcall/releases)
[![forks](https://img.shields.io/github/forks/wormhole/fastcall)](https://github.com/wormhole/fastcall/network/members)
[![stars](https://img.shields.io/github/stars/wormhole/fastcall)](https://github.com/wormhole/fastcall/stargazers) 

## 一、简介
`fastcall`是一款`java`开发的高性能、轻量级、低配置、无侵入的`RPC`框架，采用了类似`dubbo`的注解风格，支持`ZooKeeper`服务注册中心，对于`Spring`和`非Spring`应用都完美支持，并提供`fastcall-spring-boot-starter`，支持自动配置。

## 二、安装
```
$ git clone https://github.com/wormhole/fastcall
$ sh build.sh
```

## 三、使用
### 3.1. 在非spring应用中使用
#### 3.1.1. 服务提供者`Provider`工程

1. 新建`maven`项目，并添加依赖
```
<dependencies>
    <dependency>
        <groupId>net.stackoverflow.fastcall</groupId>
        <artifactId>fastcall-core</artifactId>
        <version>${fastcall.version}</version>
    </dependency>

    <dependency>
        <groupId>net.stackoverflow.fastcall</groupId>
        <artifactId>fastcall-demo-api</artifactId>
        <version>${fastcall.version}</version>
    </dependency>
</dependencies>
```

2. 服务类实现，`@FastcallService`标识这是一个需要暴露的服务，并指定了服务的分组，版本，响应超时等元数据
```
@FastcallService(group = "group-1", version="1.0", timeout=5000)
public class SayServiceImpl implements SayService {

    private static final Logger log = LoggerFactory.getLogger(SayServiceImpl.class);

    @Override
    public String say(String content) {
        return content;
    }
}
```

3. 利用工厂类，生成`FastcallManager`对象，`FastcallManager`是整个系统的`Facade`，封装了子系统`SerializeManager`、`RegistryManager`，`ProviderManager`，`ConsumerManager`，对外提供统一的接口。
```
public class FastcallDemoProviderApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
        //新建配置类，默认不会加载ProviderManager，除非将ProviderConfig的enabled值设为true
        FastcallConfig config = new FastcallConfig();
        config.getProvider().setEnabled(true);
        //生成工厂类
        FastcallManagerFactory factory = new ConfigFastcallManagerFactory(config);
        //由工厂类生成FastcallManager，默认实现为DefaultFastcallManager
        FastcallManager manager = factory.getInstance();
        //注册需要暴露的服务
        manager.register(SayService.class, new SayServiceImpl());
        //启动服务
        manager.start();
    }
}
```

#### 3.1.2. 服务消费者`Consumer`工程

1. 新建`maven`项目，添加依赖
```
<dependencies>
    <dependency>
        <groupId>net.stackoverflow.fastcall</groupId>
        <artifactId>fastcall-core</artifactId>
        <version>${fastcall.version}</version>
    </dependency>

    <dependency>
        <groupId>net.stackoverflow.fastcall</groupId>
        <artifactId>fastcall-demo-api</artifactId>
        <version>${fastcall.version}</version>
    </dependency>
</dependencies>
```

2. 利用工厂类，生成`FastcallManager`，将需要远程调用的接口，生成代理对象，之后就可以像调用本地方法一样调用
```
public class FastcallDemoConsumerApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
        //利用工厂类生成FastcallManager
        FastcallConfig config = new FastcallConfig();
        FastcallManagerFactory factory = new ConfigFastcallManagerFactory(config);
        FastcallManager manager = factory.getInstance();
        //生成代理对象，指定分组，版本，超时时间
        SayService proxy = manager.createProxy(SayService.class, "group-1", "1.0", 5000);
        //rpc调用
        proxy.say("hello world");
    }
}
```

### 3.2. 在spring应用中使用（spring boot为例）
#### 3.2.1. 服务提供者`Provider`工程 [【样例代码】](https://github.com/wormhole/fastcall/tree/master/fastcall-demo-provider)

1. 新建`Spring boot`项目，并添加`maven`依赖
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

2. `application.properties`定义配置，对于服务提供者，`fastcall.provider.enabled`必须指定为`true`
```
#序列化方式
fastcall.serialize=json
#注册中心配置
fastcall.registry.type=zookeeper
fastcall.registry.zookeeper.host=127.0.0.1
fastcall.registry.zookeeper.port=2181
fastcall.registry.zookeeper.session-timeout=5000
#Provider服务配置
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

3. 在服务实现类上，添加`@FastcallService`注解，标识这是需要暴露的服务，`group`可以指定分组。添加该注解后`fastcall-spring-boot-starter`会将它自动实例化注册成`bean`，并将接口向服务注册中心注册
```
@FastcallService(group = "group-1", version="1.0", timeout=5000)
public class SayServiceImpl implements SayService {

    private static final Logger log = LoggerFactory.getLogger(SayServiceImpl.class);

    @Override
    public String say(String content) {
        return "hello " + content;
    }
}
```

4. 在启动类上添加注解`@EnableFastcall`（必加）指定需要扫描注解的包的位置，如果没有指定`basePackages`或者`basePackageClasses`，则从启动类的包路径包括子包，作为扫描路径
```
@SpringBootApplication
@EnableFastcall(basePackages = {"net.stackoverflow.fastcall.demo.provider"})
public class FastcallDemoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastcallDemoProviderApplication.class, args);
    }

}
```

#### 3.2.2. 服务消费者`Consumer`工程 [【样例代码】](https://github.com/wormhole/fastcall/tree/master/fastcall-demo-consumer) 

1. 新建`Spring boot`项目，并添加`maven`依赖
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

2. `application.properties`定义配置
```
#序列化方式
fastcall.serialize=json
#服务注册中心配置
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

3. 在接口引用上，添加注解`@FastcallReference`，添加该注解后，`fastcall-spring-boot-starter`会自动生成动态代理对象，之后可以像调用本地方法一样调用接口
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

## 四、模块划分
|模块名|说明|
|----|----|
|fastcall-parent|对依赖版本进行统一管理|
|fastcall-core|核心模块，包括序列化管理，注册中心管理，连接管理，应用层协议等|
|fastcall-spring-boot-autoconfigure|spring boot自动化配置模块，用于spring boot项目|
|fastcall-spring-boot-starter|spring boot starter模块，对自动化配置模块及相关依赖进行统一管理|
|fastcall-demo-api|样例工程，公共接口|
|fastcall-demo-provider|样例工程，服务提供者|
|fastcall-demo-consumer|样例工程，服务消费者|

## 五、详细配置说明
|key|说明|默认值|当前可选值|单位|
|----|----|----|----|----|
|fastcall.serialize|序列化方式|json|json|-|
|fastcall.registry.type|服务注册中心类型|zookeeper|zookeeper|-|
|fastcall.registry.zookeeper.host|zookeeper地址|127.0.0.1|-|-|
|fastcall.registry.zookeeper.port|zookeeper端口|2181|-|-|
|fastcall.registry.zookeeper.session-timeout|zookeeper会话超时时间|5000|-|毫秒|
|fastcall.provider.enabled|是否启用provider模块|false|true/false|-|
|fastcall.provider.backlog|等待队列长度|1024|-|-|
|fastcall.provider.host|provider服务绑定地址|0.0.0.0|-|-|
|fastcall.provider.port|provider服务绑定端口|9966|-|-|
|fastcall.provider.timeout|provider心跳检测超时时间|60|-|秒|
|fastcall.provider.threads|provider并发处理最大线程数|0x7fffffff|<=0x7fffffff|-|
|fastcall.consumer.timeout|consumer心跳检测超时时间|60|-|秒|
|fastcall.consumer.max-connection|consumer最大连接数|0x7fffffff|<=0x7fffffff|-|
|fastcall.consumer.retry|服务调用失败重试次数|0|-|-|
|fastcall.consumer.balance|负载均衡策略|random|random/poll|-|

## 六、支持情况

|序列化类型|json|protobuf|msgpack|
|----|----|----|----|
|是否支持|✔|❌|❌|

|注册中心|zookeeper|redis|multicast|
|----|----|----|----|
|是否支持|✔|❌|❌|

|负载均衡|随机|轮询|权重|
|----|----|----|----|
|是否支持|✔|✔|❌|

|协议|fastcall|rmi|http|hessian|
|----|----|----|----|----|
|是否支持|✔|❌|❌|❌|

## 七、LICENSE
Fastcall software is licenced under the [MIT](LICENSE) License

