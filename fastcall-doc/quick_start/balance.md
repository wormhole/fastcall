### 负载均衡策略

框架默认提供了两种负载均衡策略，随机策略和轮询策略（权重策略即将支持），框架默认的策略是随机策略。这里的负载均衡都是消费端的负载均衡策略，和`Spring cloud`中的`ribbon`类似。
负载均衡由负载均衡子系统控制，发生在服务的远程调用之前。流程如下
* **服务注册子系统**通过分组，版本等过滤后获取可用的远程服务地址
* 上一步获取的地址列表传入**负载均衡子系统**，通过负载均衡策略选出一个地址
* 通过**传输层子系统**获取该地址连接，发送报文，进行远程调用

#### 一、在`Springboot`应用中配置负载均衡策略
对于`SpringBoot`应用，可以在`application.properties`配置文件中的`fastcall.balance`的值改为`random`（默认）或`poll`来改变负载均衡策略
```
fastcall.balance=random/poll
```

#### 二、在非`Spring`应用中配置负载均衡策略
在生成`FastcallManager`工厂类之前，我们传递了一个`FastcallConfig`配置类，我们通过改变配置类中的的`balance`属性，就可以改变负载均衡策略
```
//实例化配置类
FastcallConfig config = new FastcallConfig();
//修改默认配置（此处将负载均衡策略修改为轮询）
config.setBalance("poll");

//生成工厂类，并由工厂类生成DefaultFastcallManager的单例对象
FastcallManagerFactory factory = new ConfigFastcallManagerFactory(config);
FastcallManager manager = factory.getInstance();
```

#### 三、特别注意
对于轮询策略，轮询的粒度为同一个接口，同一个分组，同一个版本的服务。