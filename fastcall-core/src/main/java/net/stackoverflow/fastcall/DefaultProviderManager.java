package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.annotation.FastcallService;
import net.stackoverflow.fastcall.config.ProviderConfig;
import net.stackoverflow.fastcall.context.BeanContext;
import net.stackoverflow.fastcall.factory.NameThreadFactory;
import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.registry.ServiceMetaData;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.*;

/**
 * ProviderManager默认实现
 *
 * @author wormhole
 */
public class DefaultProviderManager implements ProviderManager {

    private static final Logger log = LoggerFactory.getLogger(DefaultFastcallManager.class);

    private final RegistryManager registryManager;

    private final BeanContext beanContext;

    private final NettyServer server;

    private final ProviderConfig config;

    private final ExecutorService serverExecutorService;

    private final ExecutorService rpcExecutorService;

    public DefaultProviderManager(ProviderConfig config, SerializeManager serializeManager, RegistryManager registryManager) {
        this.registryManager = registryManager;
        this.beanContext = new BeanContext();
        this.config = config;
        this.serverExecutorService = Executors.newSingleThreadExecutor(new NameThreadFactory("NettyServer"));
        this.rpcExecutorService = new ThreadPoolExecutor(0, config.getThreads(), 60, TimeUnit.SECONDS, new SynchronousQueue<>(), new NameThreadFactory("Rpc"));
        this.server = new NettyServer(config.getBacklog(), config.getTimeout(), config.getHost(), config.getPort(), serializeManager, beanContext, rpcExecutorService);
    }

    /**
     * 获取配置
     *
     * @return provider配置
     */
    @Override
    public ProviderConfig config() {
        return config;
    }

    /**
     * 启动服务
     */
    @Override
    public void start() {
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            serverExecutorService.execute(() -> server.bind(countDownLatch));
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("ProviderManager fail to start server", e);
        }
    }

    /**
     * 停止服务
     */
    @Override
    public void close() {
        server.close();
        serverExecutorService.shutdown();
        rpcExecutorService.shutdown();
    }

    /**
     * 注册服务
     *
     * @param clazz 需要暴露的接口
     * @param bean  服务bean对象
     */
    @Override
    public void register(Class<?> clazz, Object bean) {
        Class<?> cls = bean.getClass();
        FastcallService fastcallService = cls.getAnnotation(FastcallService.class);
        if (fastcallService != null) {
            beanContext.setBean(clazz, bean);
            String group = fastcallService.group();
            String version = fastcallService.version();
            registryManager.register(new ServiceMetaData(group, version, clazz.getName(), getIp(), config.getPort()));
        }
    }

    /**
     * 获取本机服务ip
     *
     * @return
     */
    private String getIp() {
        if (config.getHost().equals("0.0.0.0")) {
            InetAddress ip4 = null;
            try {
                ip4 = Inet4Address.getLocalHost();
            } catch (UnknownHostException e) {
                log.error("ProviderManager fail to get ip", e);
            }
            assert ip4 != null;
            return ip4.getHostAddress();
        } else {
            return config.getHost();
        }
    }
}
