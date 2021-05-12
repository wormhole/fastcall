package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.annotation.FastcallService;
import net.stackoverflow.fastcall.balance.BalanceManager;
import net.stackoverflow.fastcall.config.FastcallConfig;
import net.stackoverflow.fastcall.config.TransportConfig;
import net.stackoverflow.fastcall.core.BeanContext;
import net.stackoverflow.fastcall.core.ResponseFuture;
import net.stackoverflow.fastcall.factory.RpcProxyFactory;
import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.registry.ServiceDefinition;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.TransportManager;
import net.stackoverflow.fastcall.transport.fastcall.proto.RpcRequest;
import net.stackoverflow.fastcall.util.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Fastcall对外统一外观
 *
 * @author wormhole
 */
public class FastcallFacade {

    private static final Logger log = LoggerFactory.getLogger(FastcallFacade.class);

    private final SerializeManager serializeManager;

    private final RegistryManager registryManager;

    private final TransportManager transportManager;

    private final BalanceManager balanceManager;

    private final FastcallConfig config;

    public FastcallFacade(FastcallConfig config, SerializeManager serializeManager, RegistryManager registryManager, BalanceManager balanceManager, TransportManager transportManager) {
        this.config = config;
        this.serializeManager = serializeManager;
        this.registryManager = registryManager;
        this.balanceManager = balanceManager;
        this.transportManager = transportManager;
    }

    /**
     * 获取配置
     *
     * @return fastcall配置
     */
    public FastcallConfig config() {
        return config;
    }

    /**
     * 生成代理对象
     *
     * @param clazz    接口Class对象
     * @param group    所属分组
     * @param version  版本号
     * @param timeout  rpc调用超时时间
     * @param fallback 服务降级
     * @param <T>      泛型
     * @return 代理对象
     */
    public <T> T createProxy(Class<T> clazz, String group, String version, Long timeout, Class<?> fallback) {
        return RpcProxyFactory.create(this, clazz, group, version, timeout, fallback);
    }

    /**
     * 注册服务（通过注解）
     *
     * @param clazz 需要暴露的接口
     * @param bean  服务bean对象
     */
    public void register(Class<?> clazz, Object bean) {
        Class<?> cls = bean.getClass();
        FastcallService fastcallService = cls.getAnnotation(FastcallService.class);
        if (fastcallService != null) {
            BeanContext context = BeanContext.getInstance();
            context.setBean(clazz, bean);
            String group = fastcallService.group();
            String version = fastcallService.version();
            TransportConfig transportConfig = config.getTransport();
            registryManager.register(new ServiceDefinition(group, version, clazz.getName(), IpUtils.getIp(transportConfig.getHost()), transportConfig.getPort()));
        }
    }

    /**
     * RPC调用
     *
     * @param method  方法
     * @param args    参数
     * @param group   所属分组
     * @param version 版本号
     * @return ResponseFuture对象
     */
    public ResponseFuture call(Method method, Object[] args, String group, String version) {
        RpcRequest request = new RpcRequest();
        request.setId(UUID.randomUUID().toString());
        request.setInterfaceType(method.getDeclaringClass());
        request.setMethod(method.getName());
        request.setGroup(group);
        request.setVersion(version);
        if (args == null) {
            request.setParams(null);
        } else {
            List<byte[]> bytes = new ArrayList<>();
            for (Object arg : args) {
                bytes.add(serializeManager.serialize(arg));
            }
            request.setParams(bytes);
        }
        request.setParamsType(Arrays.asList(method.getParameterTypes()));

        List<ServiceDefinition> definitions = registryManager.getService(request.getInterfaceType(), group, version);
        InetSocketAddress address = balanceManager.choose(definitions);
        return transportManager.sendTo(address, request);
    }

    /**
     * 启动服务
     */
    public void start() {
        if (BeanContext.getInstance().size() > 0) {
            TransportConfig transportConfig = config.getTransport();
            transportManager.bind(new InetSocketAddress(transportConfig.getHost(), transportConfig.getPort()));
        }
    }

    /**
     * 停止服务
     */
    public void stop() {
        transportManager.close();
        registryManager.close();
    }

    public SerializeManager serializeManager() {
        return serializeManager;
    }

    public BalanceManager balanceManager() {
        return balanceManager;
    }

    public RegistryManager registryManager() {
        return registryManager;
    }

    public TransportManager transportManager() {
        return transportManager;
    }
}
