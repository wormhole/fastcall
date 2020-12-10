![logo](logo.png)

[![downloads](https://img.shields.io/github/downloads/wormhole/fastcall/total)](https://github.com/wormhole/fastcall/releases)
[![forks](https://img.shields.io/github/forks/wormhole/fastcall)](https://github.com/wormhole/fastcall/network/members)
[![stars](https://img.shields.io/github/stars/wormhole/fastcall)](https://github.com/wormhole/fastcall/stargazers) 
[![repo size](https://img.shields.io/github/repo-size/wormhole/fastcall)](https://github.com/wormhole/fastcall/archive/master.zip)
[![release](https://img.shields.io/github/v/release/wormhole/fastcall)](https://github.com/wormhole/fastcall/releases)
[![license](https://img.shields.io/github/license/wormhole/fastcall)](https://github.com/wormhole/fastcall/blob/master/LICENSE)

## 一、简介
`fastcall`是一款`java`开发的高性能、轻量级、低配置、无侵入的`RPC`框架，采用了类似`dubbo`的注解风格，支持`ZooKeeper`服务注册中心，对于`Spring`和`非Spring`应用都完美支持，并提供`fastcall-spring-boot-starter`，支持自动配置。

## 二、安装
```
$ git clone https://github.com/wormhole/fastcall
$ cd fastcall-parent
$ mvn install
$ cd ../fastcall-core
$ mvn install
$ cd ../fastcall-spring-boot-autoconfigure
$ mvn install
$ cd ../fastcall-spring-boot-starter
$ mvn install
```

## 三、使用
### 非spring应用

### spring应用（spring boot为例）
#### 服务提供者`Provider`工程 [【样例代码】](https://github.com/wormhole/fastcall/tree/master/fastcall-demo-provider)

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

2. `application.properties`定义配置，对于服务提供者，`fastcall.provider.enabled`必须指定为`true`，其他配置项中可以不配置使用默认值
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
fastcall.provider.threads=100
#日志配置
logging.level.root=INFO
logging.level.net.stackoverflow.fastcall=DEBUG
```

3. 在服务实现类上，添加`@FastcallService`注解，标识这是需要暴露的服务，`group`可以指定分组。添加该注解后`fastcall-spring-boot-starter`会将它自动实例化注册成`bean`，并将接口向服务注册中心注册
```
@FastcallService(group = "group-1")
public class SayServiceImpl implements SayService {

    private static final Logger log = LoggerFactory.getLogger(SayServiceImpl.class);

    @Override
    public String say(String content) {
        return "hello " + content;
    }

    @Override
    public String sayWithCheckException(String content) throws Exception {
        throw new IOException("check exception");
    }

    @Override
    public String sayWithUncheckException(String content) {
        throw new RuntimeException("uncheck exception");
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

#### 服务消费者`Consumer`工程 [【样例代码】](https://github.com/wormhole/fastcall/tree/master/fastcall-demo-consumer) 

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

* `application.properties`定义配置，都可以使用默认配置
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
fastcall.consumer.threads=512
#日志配置
logging.level.root=INFO
logging.level.net.stackoverflow.fastcall=DEBUG
```

* 在接口引用上，添加注解`@FastcallReference`，添加该注解后，`fastcall-spring-boot-starter`会自动生成动态代理对象，之后可以像调用本地方法一样调用接口
```
@RestController
public class FastcallController {

    @FastcallReference(group = "group-1")
    private SayService sayService;

    @GetMapping("/say")
    public String say(@RequestParam("content") String content) {
        return sayService.say(content);
    }

    @GetMapping("/say_with_check_exception")
    public String sayWithCheckException(@RequestParam("content") String content) throws Exception {
        return sayService.sayWithCheckException(content);
    }

    @GetMapping("/say_with_uncheck_exception")
    public String sayWithUncheckException(@RequestParam("content") String content) {
        return sayService.sayWithUncheckException(content);
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

## 五、支持情况

|序列化类型|json|protobuf|msgpack|
|----|----|----|----|
|是否支持|✔|❌|❌|

|注册中心|zookeeper|redis|multicast|
|----|----|----|----|
|是否支持|✔|❌|❌|

|协议|fastcall|rmi|http|hessian|
|----|----|----|----|----|
|是否支持|✔|❌|❌|❌|

## 六、LICENSE
Fastcall software is licenced under the [MIT](LICENSE) License

