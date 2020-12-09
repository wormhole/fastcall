package net.stackoverflow.fastcall.registry.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * 初始化监听器
 *
 * @author wormhole
 */
public class InitWatcher implements Watcher {

    private final CountDownLatch countDownLatch;

    public InitWatcher(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
            countDownLatch.countDown();
        }
    }
}
