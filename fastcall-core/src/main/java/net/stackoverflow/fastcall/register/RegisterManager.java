package net.stackoverflow.fastcall.register;

import net.stackoverflow.fastcall.exception.ServiceNotFoundException;

import java.net.InetSocketAddress;

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
    void register(ServiceMetaData meta);

    /**
     * 获取服务
     * @param group
     * @param className
     * @return
     */
    InetSocketAddress getServiceAddress(String group, String className) throws ServiceNotFoundException;
}
