### 详细配置

|key|说明|默认值|当前可选值|单位|
|----|----|----|----|----|
|fastcall.serialize|序列化方式|json|json|-|
|fastcall.registry.type|服务注册中心类型|zookeeper|zookeeper|-|
|fastcall.registry.zookeeper.host|zookeeper地址|127.0.0.1|-|-|
|fastcall.registry.zookeeper.port|zookeeper端口|2181|-|-|
|fastcall.registry.zookeeper.session-timeout|zookeeper会话超时时间|5000|-|ms|
|fastcall.provider.enabled|是否启用provider模块|false|true/false|-|
|fastcall.provider.backlog|等待队列长度|1024|-|-|
|fastcall.provider.host|provider服务绑定地址|0.0.0.0|-|-|
|fastcall.provider.port|provider服务绑定端口|9966|-|-|
|fastcall.provider.timeout|provider心跳检测超时时间|60|-|s|
|fastcall.provider.threads|provider并发处理最大线程数|0x7fffffff|<=0x7fffffff|-|
|fastcall.consumer.timeout|consumer心跳检测超时时间|60|-|s|
|fastcall.consumer.max-connection|consumer最大连接数|0x7fffffff|<=0x7fffffff|-|
|fastcall.consumer.retry|服务调用失败重试次数|0|-|-|
|fastcall.consumer.balance|负载均衡策略|random|random/poll|-|