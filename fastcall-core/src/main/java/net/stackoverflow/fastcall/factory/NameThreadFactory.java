package net.stackoverflow.fastcall.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂
 *
 * @author wormhole
 */
public class NameThreadFactory implements ThreadFactory {

    private final AtomicInteger threadNumber = new AtomicInteger(0);

    public final String prefix;

    public NameThreadFactory(String name) {
        prefix = name + "-";
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, prefix + threadNumber.incrementAndGet());
    }
}
