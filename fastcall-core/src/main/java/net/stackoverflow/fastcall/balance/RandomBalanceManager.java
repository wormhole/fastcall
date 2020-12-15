package net.stackoverflow.fastcall.balance;

import net.stackoverflow.fastcall.registry.ServiceMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

/**
 * 随机负载均衡策略
 *
 * @author wormhole
 */
public class RandomBalanceManager implements BalanceManager {

    private static final Logger log = LoggerFactory.getLogger(RandomBalanceManager.class);

    @Override
    public InetSocketAddress choose(List<ServiceMetaData> serviceMetaDataList) {
        ServiceMetaData meta = serviceMetaDataList.get(0);
        if (serviceMetaDataList.size() == 1) {
            return new InetSocketAddress(meta.getHost(), meta.getPort());
        }
        Random random = new Random(System.currentTimeMillis());
        int index = random.nextInt(serviceMetaDataList.size());
        meta = serviceMetaDataList.get(index);
        log.trace("BalanceManager choose remote {}", meta.getHost() + ":" + meta.getPort());
        return new InetSocketAddress(meta.getHost(), meta.getPort());
    }
}
