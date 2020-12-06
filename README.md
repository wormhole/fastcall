![logo](logo.png)

[![downloads](https://img.shields.io/github/downloads/wormhole/fastcall/total)](https://github.com/wormhole/fastcall/releases)
[![forks](https://img.shields.io/github/forks/wormhole/fastcall)](https://github.com/wormhole/fastcall/network/members)
[![stars](https://img.shields.io/github/stars/wormhole/fastcall)](https://github.com/wormhole/fastcall/stargazers) 
[![repo size](https://img.shields.io/github/repo-size/wormhole/fastcall)](https://github.com/wormhole/fastcall/archive/master.zip)
[![release](https://img.shields.io/github/v/release/wormhole/fastcall)](https://github.com/wormhole/fastcall/releases)
[![license](https://img.shields.io/github/license/wormhole/fastcall)](https://github.com/wormhole/fastcall/blob/master/LICENSE)

## 一、简介
`fastcall`是一款`java`开发的低配置、无侵入的`RPC`框架，采用了类似`dubbo`的注解风格，支持`ZooKeeper`服务注册中心，对于`Spring`和`非Spring`应用都完美支持，并提供`fastcall-spring-boot-starter`，支持完全自动配置。

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
```

## 三、使用
### 3.1 启动服务注册中心
目前仅支持`ZooKeeper`，安装与启动过程略。

### 3.2 服务提供者`Provider`工程
[【样例代码】](https://github.com/wormhole/fastcall/tree/master/fastcall-demo-provider)

* 新建`Spring boot`项目，并添加依赖
```
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <dependency>
        <groupId>net.stackoverflow.fastcall</groupId>
        <artifactId>fastcall-spring-boot-starter</artifactId>
        <type>pom</type>
        <version>1.0</version>
    </dependency>

    <dependency>
        <groupId>net.stackoverflow.fastcall</groupId>
        <artifactId>fastcall-demo-api</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

* `application.properties`定义配置，对于服务提供者，`fastcall.provider.enabled`必须指定为`true`，其他除了`fastcall.registry.zookeeper.host`、`fastcall.registry.zookeeper.port`，都可以不配置，使用默认值
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

* 在服务实现类上，添加`@FastcallService`注解，标识这是需要暴露的服务，`group`可以指定分组。添加该注解后`fastcall-spring-boot-starter`会将它自动实例化注册成`bean`，并将接口向服务注册中心注册
```
@FastcallService(group = "group-1")
public class SayServiceImpl implements SayService {

    private static final Logger log = LoggerFactory.getLogger(SayServiceImpl.class);

    @Override
    public String say(String content) {
        return "hello " + content;
    }

    @Override
    public int say(int content) {
        return ++content;
    }

    @Override
    public String say() {
        return "hello";
    }
}
```

### 3.3 服务消费者`Consumer`工程
[【样例代码】](https://github.com/wormhole/fastcall/tree/master/fastcall-demo-consumer) 

* 新建`Spring boot`项目，并添加依赖
```
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>net.stackoverflow.fastcall</groupId>
        <artifactId>fastcall-spring-boot-starter</artifactId>
        <type>pom</type>
        <version>1.0</version>
    </dependency>

    <dependency>
        <groupId>net.stackoverflow.fastcall</groupId>
        <artifactId>fastcall-demo-api</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

* `application.properties`定义注解
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

* 在接口引用上，添加注解`@FastcallReference`，添加该注解后，`fastcall-spring-boot-starter`会自动生成动态代理对象
```
@RestController
public class FastcallController {

    @FastcallReference(group = "group-1")
    private SayService sayService;

    @GetMapping("/say1")
    public String say(@RequestParam("content") String content) {
        return sayService.say(content);
    }

    @GetMapping("/say2")
    public int say(@RequestParam("content") Integer content) {
        return sayService.say(content);
    }

    @GetMapping("/say3")
    public String say() {
        return sayService.say();
    }
}
```

## 四、支持情况
>未勾选的为待支持的类型

* 序列化方式
- [x] json
- [ ] protobuf
- [ ] msgpack 

* 注册中心
- [x] zookeeper
- [ ] redis
- [ ] multicast

* 协议
- [x] fastcall
- [ ] rmi
- [ ] http
- [ ] hessian

## 五、LICENSE
[MIT](LICENSE) © wormhole

