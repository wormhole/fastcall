package net.stackoverflow.fastcall.balance;

import net.stackoverflow.fastcall.registry.ServiceDefinition;
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
    public InetSocketAddress choose(List<ServiceDefinition> definitions) {
        ServiceDefinition definition = definitions.get(0);
        if (definitions.size() == 1) {
            return new InetSocketAddress(definition.getHost(), definition.getPort());
        }
        Random random = new Random(System.currentTimeMillis());
        int index = random.nextInt(definitions.size());
        definition = definitions.get(index);
        log.trace("BalanceManager choose remote {}", definition.getHost() + ":" + definition.getPort());
        return new InetSocketAddress(definition.getHost(), definition.getPort());
    }
}
