package net.stackoverflow.fastcall.balance;

import net.stackoverflow.fastcall.registry.ServiceDefinition;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 负载均衡接口
 *
 * @author wormhole
 */
public interface BalanceManager {

    InetSocketAddress choose(List<ServiceDefinition> definitions);
}
