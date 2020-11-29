package net.stackoverflow.fastcall.register;

import net.stackoverflow.fastcall.register.ServiceMeta;

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
}
