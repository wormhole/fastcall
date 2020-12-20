### 在非Spring应用中使用

#### 一、服务提供者`Provider`

##### 1. 新建`maven`项目，并添加依赖
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

##### 2. 在服务实现类上，添加`@FastcallService`注解，标识这是需要暴露的服务，`group`可以指定分组，`version`可以指定版本。
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

##### 3. 利用工厂类，生成`FastcallManager`对象，`FastcallManager`是整个系统的`Facade`，封装了子系统`SerializeManager`、`RegistryManager`，`BalanceManager`，`ProviderManager`，`ConsumerManager`，对外提供统一的接口。
```
public class FastcallDemoProviderApplication {

    public static void main(String[] args) {
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

#### 二、服务消费者`Consumer`工程

##### 1. 新建`maven`项目，添加依赖
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

##### 2. 利用工厂类，生成`FastcallManager`，将需要远程调用的接口，生成代理对象，之后就可以像调用本地方法一样调用
```
public class FastcallDemoConsumerApplication {

    public static void main(String[] args) {
        //利用工厂类生成FastcallManager
        FastcallConfig config = new FastcallConfig();
        FastcallManagerFactory factory = new ConfigFastcallManagerFactory(config);
        FastcallManager manager = factory.getInstance();
        //生成代理对象，指定分组，版本，超时时间，降级类
        SayService proxy = manager.createProxy(SayService.class, "group-1", "1.0", 5000, Void.class);
        //rpc调用
        proxy.say("hello world");
        //关闭服务释放连接
        manager.stop();
    }
}
```
