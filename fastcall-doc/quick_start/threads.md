### 资源限制与线程池

服务提供方采用线程池，来执行并发的远程调用处理，默认情况下，线程池的最大线程为`Integer.MAX_VALUE`，也就是不受限制，
只受进程和操作系统对线程的最大限制，为了控制并发的最大线程数（也就是同时能处理多少个远程调用），需要对**服务提供子系统**
的线程池参数进行配置，以下是**传输层子系统**中，线程池的初始化过程
```
this.rpcExecutorService = new ThreadPoolExecutor(0, config.getThreads(), 60, TimeUnit.SECONDS, new SynchronousQueue<>(), new NameThreadFactory("Rpc"));
```

#### 一、在`Springboot`应用中对资源进行限制
在`application.properties`配置文件中，配置`fastcall.threads`的值即可。

#### 二、在`非Spring`应用中对资源进行限制
将`FastcallConfig`配置类的`threads`属性配置成需要的值即可。