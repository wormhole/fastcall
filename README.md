![logo](logo.png)

[![downloads](https://img.shields.io/github/downloads/wormhole/fastcall/total)](https://github.com/wormhole/fastcall/releases)
[![forks](https://img.shields.io/github/forks/wormhole/fastcall)](https://github.com/wormhole/fastcall/network/members)
[![stars](https://img.shields.io/github/stars/wormhole/fastcall)](https://github.com/wormhole/fastcall/stargazers) 
[![repo size](https://img.shields.io/github/repo-size/wormhole/fastcall)](https://github.com/wormhole/fastcall/archive/master.zip)
[![release](https://img.shields.io/github/v/release/wormhole/fastcall)](https://github.com/wormhole/fastcall/releases)
[![license](https://img.shields.io/github/license/wormhole/fastcall)](https://github.com/wormhole/fastcall/blob/master/LICENSE)

## 一、简介
`fastcall`是一款`java`开发的低配置、无侵入的`RPC`框架，采用了类似`dubbo`的注解风格，支持`ZooKeeper`服务注册中心，对于`Spring`和`非Spring`应用都完美支持，并提供`fastcall-spring-boot-starter`，支持完全自动配置。

## 二、支持情况
>未勾选的为待支持的类型

### 2.1 序列化方式
- [x] json
- [ ] protobuf
- [ ] msgpack 

### 2.2 注册中心
- [x] zookeeper
- [ ] redis
- [ ] multicast

### 2.3 协议
- [x] fastcall
- [ ] rmi
- [ ] http
- [ ] hessian

## 三、安装
```
git clone https://github.com/wormhole/fastcall
cd fastcall-parent
mvn install
cd ../fastcall-core
mvn install
cd ../fastcall-spring-boot-autoconfigure
mvn install
cd ../fastcall-spring-boot-starter
```

