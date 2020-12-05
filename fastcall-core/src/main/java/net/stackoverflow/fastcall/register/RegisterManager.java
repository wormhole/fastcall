package net.stackoverflow.fastcall.register;

import net.stackoverflow.fastcall.exception.ServiceNotFoundException;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 注册中心管理器
 *
 * @author wormhole
 */
public interface RegisterManager {

    /**
     * 注册服务元数据
     *
     * @param meta
     */
    void registerService(ServiceMetaData meta);

    /**
     * 获取服务
     * @param group
     * @param clazz
     * @return
     */
    List<InetSocketAddress> getServiceAddress(String group, Class<?> clazz) throws ServiceNotFoundException;
}
