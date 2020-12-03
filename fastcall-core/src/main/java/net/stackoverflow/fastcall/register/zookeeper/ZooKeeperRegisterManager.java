package net.stackoverflow.fastcall.register.zookeeper;

import net.stackoverflow.fastcall.register.RegisterData;
import net.stackoverflow.fastcall.register.RegisterManager;
import net.stackoverflow.fastcall.register.ServiceMeta;
import net.stackoverflow.fastcall.util.JsonUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * zookeeper注册中心管理器
 *
 * @author wormhole
 */
public class ZooKeeperRegisterManager implements RegisterManager {

    private ZkClient client;

    public ZooKeeperRegisterManager(ZkClient client) {
        this.client = client;
    }

    @Override
    public synchronized void register(ServiceMeta meta) {
        checkRootNode();
        String path = PathConst.root + "/" + meta.getInterfaces();
        Stat stat = client.exist(path);
        if (stat == null) {
            RegisterData data = new RegisterData();
            data.addRoute(meta.getGroup(), meta.getHost(), meta.getPort());
            String json = JsonUtils.bean2json(data);
            client.create(path, json.getBytes(), CreateMode.PERSISTENT);
        } else {
            String json = client.getData(path);
            RegisterData data = JsonUtils.json2bean(json, RegisterData.class);
            data.addRoute(meta.getGroup(), meta.getHost(), meta.getPort());
            json = JsonUtils.bean2json(data);
            client.setData(path, json.getBytes(), stat.getVersion());
        }
    }

    @Override
    public InetSocketAddress getRemoteAddr(String group, String className) {
        String json = client.getData(PathConst.root + "/" + className);
        RegisterData data = JsonUtils.json2bean(json, RegisterData.class);
        Map<String, List<RegisterData.Address>> map = data.getRoute();
        List<RegisterData.Address> addresses = map.get(group);
        if (addresses != null) {
            RegisterData.Address address = addresses.get(0);
            return new InetSocketAddress(address.getHost(), address.getPort());
        } else {
            return null;
        }
    }

    /**
     * 检查根节点是否存在，不存在则创建
     */
    private synchronized void checkRootNode() {
        if (client.exist(PathConst.root) == null) {
            client.create(PathConst.root, "".getBytes(), CreateMode.PERSISTENT);
        }
    }
}
