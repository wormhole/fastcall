### 资源限制与线程池

#### 一、服务提供方资源限制
服务提供方采用线程池，来执行并发的远程调用处理，默认情况下，线程池的最大线程为`Integer.MAX_VALUE`，也就是不受限制，
只受进程和操作系统对线程的最大限制，为了控制并发的最大线程数（也就是同时能处理多少个远程调用），需要对**服务提供子系统**
的线程池参数进行配置，以下是**服务提供子系统**中，线程池的初始化过程
```
this.rpcExecutorService = new ThreadPoolExecutor(0, config.getThreads(), 60, TimeUnit.SECONDS, new SynchronousQueue<>(), new NameThreadFactory("Rpc"));
```

#### 二、服务消费方资源限制
服务消费方与服务提供方之间通过长连接进行通信，每个长连接都在一个线程中运行，默认情况下，线程池的最大线程数为`Integer.MAX_VALUE`，也就是不受限制，
可以维持的长连接数只受操作系统对线程的最大限制，为了控制最大连接数，需要对**服务消费子系统**的线程参数进行配置，以下是**服务消费子系统**中，线程池的初始化过程
```
this.connectionExecutorService = new ThreadPoolExecutor(0, config.getMaxConnection(), 60, TimeUnit.SECONDS, new SynchronousQueue<>(), new NameThreadFactory("Connection"));
```

#### 三、在`Springboot`应用中对资源进行限制
在`application.properties`配置文件中，配置`fastcall.provider.threads`和`fastcall.consumer.max-connection`的值即可。

#### 四、在`非Spring`应用中对资源进行限制
将`FastcallConfig`配置类的`provider`对象的`threads`属性，`consumer`对象的`maxConnection`属性配置需要的值即可。