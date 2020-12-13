package net.stackoverflow.fastcall.balance;


import net.stackoverflow.fastcall.registry.ServiceMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 轮询负载均衡策略
 *
 * @author wormhole
 */
public class PollBalanceManager implements BalanceManager {

    private final static Logger log = LoggerFactory.getLogger(PollBalanceManager.class);

    private final Map<String, ServiceMetaData> history;

    public PollBalanceManager() {
        this.history = new ConcurrentHashMap<>();
    }

    @Override
    public InetSocketAddress choose(List<ServiceMetaData> serviceMetaDataList) {
        ServiceMetaData meta = serviceMetaDataList.get(0);
        if (serviceMetaDataList.size() == 1) {
            return new InetSocketAddress(meta.getHost(), meta.getPort());
        }
        try {
            String key = getKey(meta.getInterfaceName(), meta.getGroup(), meta.getVersion());
            ServiceMetaData last = history.get(key);
            if (last != null) {
                int index = serviceMetaDataList.indexOf(last);
                if (index != -1) {
                    int next = (index + 1) % serviceMetaDataList.size();
                    meta = serviceMetaDataList.get(next);
                    history.put(key, meta);
                }
            } else {
                history.put(key, meta);
            }
        } catch (Exception e) {
            log.error("BalanceManager fail to choose", e);
        }
        return new InetSocketAddress(meta.getHost(), meta.getPort());
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
