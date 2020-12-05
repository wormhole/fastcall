package net.stackoverflow.fastcall;

import net.stackoverflow.fastcall.exception.ConnectionInActiveException;
import net.stackoverflow.fastcall.register.RegisterManager;
import net.stackoverflow.fastcall.serialize.SerializeManager;
import net.stackoverflow.fastcall.transport.ConnectionManager;
import net.stackoverflow.fastcall.transport.NettyClient;
import net.stackoverflow.fastcall.transport.proto.Message;
import net.stackoverflow.fastcall.transport.proto.MessageType;
import net.stackoverflow.fastcall.transport.proto.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * FastcallManager默认实现
 *
 * @author wormhole
 */
public class DefaultFastcallManager implements FastcallManager {

    private static final Logger log = LoggerFactory.getLogger(DefaultFastcallManager.class);

    private final RegisterManager registerManager;

    private final SerializeManager serializeManager;

    private ConnectionManager connectionManager;

    public DefaultFastcallManager(RegisterManager registerManager, SerializeManager serializeManager) {
        this.registerManager = registerManager;
        this.serializeManager = serializeManager;
    }

    public DefaultFastcallManager(RegisterManager registerManager, SerializeManager serializeManager, ConnectionManager connectionManager) {
        this.registerManager = registerManager;
        this.serializeManager = serializeManager;
        this.connectionManager = connectionManager;
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public ResponseFuture call(RpcRequest request) {
        List<InetSocketAddress> addresses = registerManager.getServiceAddress(request.getGroup(), request.getInterfaceType());
        ResponseFuture future = null;
        for (InetSocketAddress address : addresses) {
            try {
                NettyClient client = connectionManager.getConnection(address);
                future = ResponseFutureContext.createFuture(request.getId());
                client.send(new Message(MessageType.BUSINESS_REQUEST, request));
                break;
            } catch (ConnectionInActiveException e) {
                ResponseFutureContext.removeFuture(request.getId());
                log.error("[R:{}] Connection is inactive", e.getHost() + ":" + e.getPort());
            }
        }
        return future;
    }
}
