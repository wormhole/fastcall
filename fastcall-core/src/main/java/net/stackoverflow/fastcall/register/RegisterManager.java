package net.stackoverflow.fastcall.register;

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
    void register(ServiceMeta meta);

    /**
     * 获取服务
     * @param group
     * @param className
     * @return
     */
    InetSocketAddress getRemoteAddr(String group, String className);
}
