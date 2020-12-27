package net.stackoverflow.fastcall.balance;


import net.stackoverflow.fastcall.registry.ServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 轮询负载均衡策略
 *
 * @author wormhole
 */
public class PollBalanceManager implements BalanceManager {

    private final static Logger log = LoggerFactory.getLogger(PollBalanceManager.class);

    private final Map<String, ServiceDefinition> history;

    private final ReentrantLock lock;

    public PollBalanceManager() {
        this.history = new ConcurrentHashMap<>();
        lock = new ReentrantLock();
    }

    @Override
    public InetSocketAddress choose(List<ServiceDefinition> definitions) {
        ServiceDefinition definition = definitions.get(0);
        if (definitions.size() == 1) {
            return new InetSocketAddress(definition.getHost(), definition.getPort());
        }
        try {
            String key = getKey(definition.getInterfaceName(), definition.getGroup(), definition.getVersion());
            lock.lock();
            ServiceDefinition last = history.get(key);
            if (last != null) {
                int index = definitions.indexOf(last);
                if (index != -1) {
                    int next = (index + 1) % definitions.size();
                    definition = definitions.get(next);
                    history.put(key, definition);
                }
            } else {
                history.put(key, definition);
            }
        } catch (Exception e) {
            log.error("BalanceManager fail to choose", e);
        } finally {
            log.trace("BalanceManager choose remote {}", definition.getHost() + ":" + definition.getPort());
            lock.unlock();
        }
        return new InetSocketAddress(definition.getHost(), definition.getPort());
    }

    private String getKey(String interfaceName, String group, String version) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String source = interfaceName + group + version;
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(source.getBytes("UTF8"));
        byte[] bytes = digest.digest();
        StringBuilder key = new StringBuilder();
        for (byte b : bytes) {
            int number = b & 0xff;
            String str = Integer.toHexString(number);
            if (str.length() == 1) {
                key.append("0");
            }
            key.append(str);
        }
        return key.toString();
    }
}
