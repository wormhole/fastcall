### 详细配置

|key|说明|默认值|可选值范围|单位|
|----|----|----|----|----|
|fastcall.serialize|序列化方式|json|json|-|
|fastcall.balance|负载均衡策略|random|random/poll|-|
|fastcall.retry|超时重试次数|0|<Integer.MAX_VALUE|-|
|fastcall.threads|rpc并发处理线程数|Integer.MAX_VALUE|<Integer.MAX_VALUE|-|
|fastcall.registry.type|服务注册中心类型|zookeeper|zookeeper/redis|-|
|fastcall.registry.zookeeper.host|zookeeper地址|127.0.0.1|-|-|
|fastcall.registry.zookeeper.port|zookeeper端口|2181|-|-|
|fastcall.registry.zookeeper.session-timeout|zookeeper会话超时时间|5000|-|ms|
|fastcall.registry.redis.host|redis地址|127.0.0.1|-|-|
|fastcall.registry.redis.port|redis端口|6379|-|-|
|fastcall.registry.redis.password|redis密码|-|-|-|
|fastcall.provider.host|provider服务绑定地址|0.0.0.0|-|-|
|fastcall.provider.port|provider服务绑定端口|9966|-|-|