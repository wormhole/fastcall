package net.stackoverflow.fastcall;

import java.io.IOException;

/**
 * FastcallManager工厂
 *
 * @author wormhole
 */
public interface FastcallManagerFactory {

    FastcallManager getInstance() throws IOException, InterruptedException;
}
