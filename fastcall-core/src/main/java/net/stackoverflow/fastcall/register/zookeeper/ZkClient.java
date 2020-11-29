package net.stackoverflow.fastcall.register.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Zookeeper客户端
 *
 * @author wormhole
 */
public class ZkClient {

    private static final Logger log = LoggerFactory.getLogger(ZkClient.class);

    private ZooKeeper zookeeper;

    private String host;

    private Integer port;

    private Integer sessionTimeout;

    public ZkClient(String host, Integer port, Integer sessionTimeout) throws IOException, InterruptedException {
        this.host = host;
        this.port = port;
        this.sessionTimeout = sessionTimeout;
        connect();
    }

    /**
     * 连接zookeeper集群
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void connect() throws IOException, InterruptedException {
        String connection = host + ":" + port;
        CountDownLatch connectedSignal = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(connection, sessionTimeout, watchedEvent -> {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                connectedSignal.countDown();
            }
        });
        connectedSignal.await();
        this.zookeeper = zooKeeper;
    }

    /**
     * 判断路径是否存在
     *
     * @param path
     * @return
     */
    public Stat exist(String path) {
        Stat stat = null;
        try {
            stat = zookeeper.exists(path, false);
        } catch (Exception e) {
            log.error("exist", e);
        }
        return stat;
    }

    /**
     * 创建新节点
     *
     * @param path
     * @param data
     * @param mode
     * @return
     */
    public boolean create(String path, byte[] data, CreateMode mode) {
        boolean ret = false;
        try {
            zookeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
            ret = true;
        } catch (Exception e) {
            log.error("create", e);
        }
        return ret;
    }

    /**
     * 获取数据
     *
     * @param path
     * @return
     */
    public String getData(String path) {
        String json = null;
        try {
            byte[] data = zookeeper.getData(path, false, new Stat());
            json = new String(data);
        } catch (Exception e) {
            log.error("getData", e);
        }
        return json;
    }

    /**
     * 设置数据
     *
     * @param path
     * @param data
     * @param version
     * @return
     */
    public boolean setData(String path, byte[] data, Integer version) {
        boolean ret = false;
        try {
            zookeeper.setData(path, data, version);
            ret = true;
        } catch (Exception e) {
            log.error("setData", e);
        }
        return ret;
    }
}
