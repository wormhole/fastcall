package net.stackoverflow.fastcall.balance;

import net.stackoverflow.fastcall.registry.ServiceMetaData;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

/**
 * 随机负载均衡策略
 *
 * @author wormhole
 */
public class RandomBalanceManager implements BalanceManager {

    @Override
    public InetSocketAddress choose(List<ServiceMetaData> serviceMetaDataList) {
        ServiceMetaData meta = serviceMetaDataList.get(0);
        if (serviceMetaDataList.size() == 1) {
            return new InetSocketAddress(meta.getHost(), meta.getPort());
        }
        Random random = new Random(System.currentTimeMillis());
        int index = random.nextInt(serviceMetaDataList.size());
        meta = serviceMetaDataList.get(index);
        return new InetSocketAddress(meta.getHost(), meta.getPort());
    }
}
