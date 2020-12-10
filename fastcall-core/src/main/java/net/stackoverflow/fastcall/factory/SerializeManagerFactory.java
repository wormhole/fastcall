package net.stackoverflow.fastcall.factory;

import net.stackoverflow.fastcall.serialize.SerializeManager;

/**
 * SerializeManager工厂类接口
 *
 * @author wormhole
 */
public interface SerializeManagerFactory {

    SerializeManager getInstance();
}
