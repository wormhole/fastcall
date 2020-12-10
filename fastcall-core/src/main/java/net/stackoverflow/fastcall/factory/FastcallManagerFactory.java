package net.stackoverflow.fastcall.factory;

import net.stackoverflow.fastcall.FastcallManager;

import java.io.IOException;

/**
 * FastcallManager工厂
 *
 * @author wormhole
 */
public interface FastcallManagerFactory {

    FastcallManager getInstance() throws IOException, InterruptedException;
}
