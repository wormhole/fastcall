![logo](image/logo.png)

[![release](https://img.shields.io/github/v/release/wormhole/fastcall)](https://github.com/wormhole/fastcall/releases)
[![build status](https://www.travis-ci.org/wormhole/fastcall.svg?branch=master)](https://www.travis-ci.org/wormhole/fastcall)
[![license](https://img.shields.io/github/license/wormhole/fastcall)](https://github.com/wormhole/fastcall/blob/master/LICENSE)
[![repo size](https://img.shields.io/github/repo-size/wormhole/fastcall)](https://github.com/wormhole/fastcall/archive/master.zip)
[![downloads](https://img.shields.io/github/downloads/wormhole/fastcall/total)](https://github.com/wormhole/fastcall/releases)
[![forks](https://img.shields.io/github/forks/wormhole/fastcall)](https://github.com/wormhole/fastcall/network/members)
[![stars](https://img.shields.io/github/stars/wormhole/fastcall)](https://github.com/wormhole/fastcall/stargazers) 

### 简介
&emsp;&emsp;`fastcall`是一款`java`开发的高性能、轻量级、低配置、无侵入的`RPC`框架。底层通信采用了`netty`的`nio`模式，应用层协议采用了自研的`fastcall`协议，支持长连接。

#### 易用性
&emsp;&emsp;在使用上，该框架采用了类似`dubbo`的注解风格，不仅对业务入侵小，且易于使用，并且对`Spring`和`非Spring`应用都有很好的支持。特别的，对于`Spring boot`应用，提供`fastcall-spring-boot-autoconfigure`和`fastcall-spring-boot-starter`模块以支持应用的自动化配置。

#### 可扩展性
&emsp;&emsp;框架本身支持可配置化的多服务注册中心，多负载均衡策略，多序列化方式。但对于未支持的情况，框架有预留扩展接口，使用者完全可以根据自己的喜好，实现扩展接口，实现自己的服务注册中心、负载策略和序列化方式。

#### 优势
&emsp;&emsp;与`grpc`等其他`rpc`框架不同，`fastcall`拥有基本的服务治理能力，支持服务发现，服务降级，负载均衡策略等功能。相比于`grpc`的一大堆配置和生成代码的引入，`fastcall`继承了`dubbo`的优点，做到了低业务入侵性。



