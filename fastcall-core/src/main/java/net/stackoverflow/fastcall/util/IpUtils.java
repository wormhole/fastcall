package net.stackoverflow.fastcall.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ip工具类
 *
 * @author wormhole
 */
public class IpUtils {

    private static final Logger log = LoggerFactory.getLogger(IpUtils.class);

    /**
     * 获取本机服务ip
     *
     * @return
     */
    public static String getIp(String ip) {
        if ("0.0.0.0".equals(ip.trim())) {
            InetAddress ip4 = null;
            try {
                ip4 = Inet4Address.getLocalHost();
            } catch (UnknownHostException e) {
                log.error("ProviderManager fail to get ip", e);
            }
            assert ip4 != null;
            return ip4.getHostAddress();
        } else {
            return ip.trim();
        }
    }
}
