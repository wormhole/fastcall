package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.config.ProviderConfig;
import net.stackoverflow.fastcall.registry.RegistryManager;
import net.stackoverflow.fastcall.registry.ServiceMetaData;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ProviderManager默认实现
 *
 * @author wormhole
 */
public class DefaultProviderManager implements ProviderManager {

    private static final Logger log = LoggerFactory.getLogger(DefaultFastcallManager.class);

    private final SerializeManager serializeManager;

    private final RegistryManager registryManager;

    private final NettyServer server;

    private final ProviderConfig config;

    private final ExecutorService executorService;

    public DefaultProviderManager(ProviderConfig config, SerializeManager serializeManager, RegistryManager registryManager) {
        this.serializeManager = serializeManager;
        this.registryManager = registryManager;
        this.config = config;
        this.server = new NettyServer(config.getBacklog(), config.getTimeout(), config.getHost(), config.getPort(), config.getThreads(), serializeManager);
        this.executorService = Executors.newFixedThreadPool(1);
    }

    /**
     * 获取配置
     *
     * @return provider配置
     */
    @Override
    public ProviderConfig getConfig() {
        return config;
    }

    /**
     * 启动服务
     */
    @Override
    public void start() {
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            executorService.execute(() -> server.bind(countDownLatch));
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
        executorService.shutdown();
    }

    /**
     * 注册服务
     *
     * @param clazz 需要暴露的接口
     * @param bean  服务bean对象
     * @param group 所属分组
     */
    @Override
    public void registerService(Class<?> clazz, Object bean, String group) {
        BeanContext.setBean(clazz, bean);
        registryManager.registerService(new ServiceMetaData(group, clazz.getName(), getIp(), config.getPort()));
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
                log.error("Get ip error", e);
            }
            assert ip4 != null;
            return ip4.getHostAddress();
        } else {
            return config.getHost();
        }
    }
}
