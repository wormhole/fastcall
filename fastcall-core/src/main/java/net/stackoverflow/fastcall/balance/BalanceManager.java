package net.stackoverflow.fastcall.balance;

import net.stackoverflow.fastcall.registry.ServiceMetaData;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

/**
 * 负载均衡接口
 *
 * @author wormhole
 */
public interface BalanceManager {

    InetSocketAddress choose(List<ServiceMetaData> serviceMetaDataList);
}
