package net.stackoverflow.fastcall.factory;

import net.stackoverflow.fastcall.FastcallFacade;

import java.io.IOException;

/**
 * FastcallManager工厂
 *
 * @author wormhole
 */
public interface FastcallFacadeFactory {

    FastcallFacade getFacade() throws IOException, InterruptedException;
}
