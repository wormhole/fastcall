package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.config.ProviderConfig;
import net.stackoverflow.fastcall.register.RegistryManager;
import net.stackoverflow.fastcall.register.ServiceMetaData;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ProviderManager默认实现
 *
 * @author wormhole
 */
public class DefaultProviderManager implements ProviderManager {

    private static final Logger log = LoggerFactory.getLogger(DefaultFastcallManager.class);

    private SerializeManager serializeManager;

    private RegistryManager registryManager;

    private NettyServer server;

    private ProviderConfig config;

    private ExecutorService executorService;

    public DefaultProviderManager(ProviderConfig config, SerializeManager serializeManager, RegistryManager registryManager) {
        this.serializeManager = serializeManager;
        this.registryManager = registryManager;
        this.config = config;
        this.server = new NettyServer(config.getBacklog(), config.getTimeout(), config.getHost(), config.getPort(), config.getThreads(), serializeManager);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public ProviderConfig getConfig() {
        return config;
    }

    @Override
    public void start() {
        executorService.submit(() -> server.bind());
    }

    @Override
    public void stop() {
        executorService.shutdownNow();
    }

    @Override
    public void registerService(Class<?> clazz, Object bean, String group) {
        BeanContext.setBean(clazz, bean);
        registryManager.registerService(new ServiceMetaData(group, clazz.getName(), getIp(), config.getPort()));
    }

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
