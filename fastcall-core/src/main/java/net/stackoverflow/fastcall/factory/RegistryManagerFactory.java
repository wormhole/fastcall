package net.stackoverflow.fastcall.factory;

import net.stackoverflow.fastcall.registry.RegistryManager;

/**
 * RegistryManager工厂类
 *
 * @author wormhole
 */
public interface RegistryManagerFactory {

    RegistryManager getInstance();
}
