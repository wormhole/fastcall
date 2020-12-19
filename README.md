![logo](logo.png)

[![release](https://img.shields.io/github/v/release/wormhole/fastcall)](https://github.com/wormhole/fastcall/releases)
[![build status](https://www.travis-ci.org/wormhole/fastcall.svg?branch=master)](https://www.travis-ci.org/wormhole/fastcall)
[![license](https://img.shields.io/github/license/wormhole/fastcall)](https://github.com/wormhole/fastcall/blob/master/LICENSE)
[![repo size](https://img.shields.io/github/repo-size/wormhole/fastcall)](https://github.com/wormhole/fastcall/archive/master.zip)
[![downloads](https://img.shields.io/github/downloads/wormhole/fastcall/total)](https://github.com/wormhole/fastcall/releases)
[![forks](https://img.shields.io/github/forks/wormhole/fastcall)](https://github.com/wormhole/fastcall/network/members)
[![stars](https://img.shields.io/github/stars/wormhole/fastcall)](https://github.com/wormhole/fastcall/stargazers) 

## 一、简介
&emsp;&emsp;`fastcall`是一款`java`开发的高性能、轻量级、低配置、无侵入的`RPC`框架。底层通信采用了`netty`的`nio`模式，应用层协议采用了自研的`fastcall`协议，支持长连接。  

&emsp;&emsp;在使用上，该框架采用了类似`dubbo`的注解风格，对业务入侵小，并且对`Spring`和`非Spring`应用都有很好的支持。特别的，对于`Spring boot`应用，提供`fastcall-spring-boot-autoconfigure`和`fastcall-spring-boot-starter`模块以支持应用的自动化配置。  

&emsp;&emsp;框架本身支持可配置化的多服务注册中心，多负载均衡策略，多序列化方式。但对于未支持的情况，框架有预留扩展接口，使用者完全可以根据自己的喜好，实现扩展接口，实现自己的服务注册中心、负载策略和序列化方式。  

&emsp;&emsp;与`grpc`等其他`rpc`框架不同，`fastcall`拥有基本的服务治理能力，支持服务发现，服务发现，服务降级，负载均衡策略等功能。相比于`grpc`的一大堆配置和生成代码的引入，`fastcall`继承了`dubbo`的优点，做到了低业务入侵性。

## 二、安装
```
$ git clone https://github.com/wormhole/fastcall
$ sh build.sh
```

## 三、使用

### 3.1. 服务提供者 [【样例代码】](https://github.com/wormhole/fastcall/tree/master/fastcall-demo-provider) 
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

3、定义接口
```
public interface SayService {
    String say(String content);
}
```

4. 在服务实现类上，添加`@FastcallService`注解，标识这是需要暴露的服务。
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

### 3.2. 服务消费者 [【样例代码】](https://github.com/wormhole/fastcall/tree/master/fastcall-demo-consumer) 

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

3. 在接口引用上，添加注解`@FastcallReference`，并指明分组，版本号，调用超时时间
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
|fastcall-doc|gitbook文档|
|fastcall-demo-api|样例工程，公共接口|
|fastcall-demo-provider|样例工程，服务提供者|
|fastcall-demo-consumer|样例工程，服务消费者|

## 五、支持情况

|序列化类型|json|protobuf|msgpack|
|----|----|----|----|
|是否支持|✔|❌|❌|

|注册中心|zookeeper|redis|eureka|
|----|----|----|----|
|是否支持|✔|❌|❌|

|负载均衡|random|poll|weight|
|----|----|----|----|
|是否支持|✔|✔|❌|

|协议|fastcall|rmi|http|
|----|----|----|----|
|是否支持|✔|❌|❌|

## 六、LICENSE
Fastcall software is licenced under the [MIT](LICENSE) License

<style>
table th:first-of-type {
    width: 25%;
}
table th:nth-of-type(2) {
    width: 25%;
}
table th:nth-of-type(3) {
    width: 25%;
}
table th:nth-of-type(3) {
    width: 25%;
}
</style>

