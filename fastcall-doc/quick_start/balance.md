### 负载均衡策略

#### 一、如何配置负载均衡策略
当前框架支持随机和轮询的负载均衡策略，默认的策略为随机，那么如何修改默认的负载均衡策略呢

* `Springboot`应用
对于`SpringBoot`应用，可以在配置文件中的将`fastcall.consumer.balance`的值改为`random`（默认）或`poll`来改变负载均衡策略
```
fastcall.consumer.balance=random/poll
```

* 非`Spring`应用
对于非`Spring`应用，在生成工厂类之前，可将`FastcallConfig`中的`consumer`字段的`balance`值改为`random`（默认）或`poll`即可
```
FastcallConfig config = new FastcallConfig();
config.getConsumer().setBalance("poll");
FastcallManagerFactory factory = new ConfigFastcallManagerFactory(config);
FastcallManager manager = factory.getInstance();
```

#### 二、随机策略（默认策略）
假如有三个服务提供者，分别在`192.168.1.1`,`192.168.1.2`,`192.168.1.3`上启动，服务消费者调用同一个服务时，会默认从地址缓存中，随机选取一个进行调用。

#### 三、轮询策略
同上假如有`192.168.1.1`,`192.168.1.2`,`192.168.1.3`三个服务提供者，这时候服务消费者调用同一个服务时，会从地址缓存中，选择上一次调用的后一个地址进行调用，轮询的顺序为服务注册的顺序。

#### 四、权重策略
尚未支持

#### 五、特别注意
对于以上所说的同一个服务的定义为同一个接口、同一个分组、同一个版本的服务，如果有其中一个不满足则不符合同一个服务的定义。特别的，如果调用的是同一个接口，同一个分组，同一个版本的服务的不同方法，也算同一个服务。