package net.stackoverflow.fastcall.context;

import net.stackoverflow.fastcall.transport.proto.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * ResponseFuture上下文
 *
 * @author wormhole
 */
public class ResponseFutureContext {

    private static final Logger log = LoggerFactory.getLogger(ResponseFutureContext.class);

    private final Map<String, ResponseFuture> futurePool;

    public ResponseFutureContext() {
        this.futurePool = new HashMap<>();
    }

    /**
     * 生成ResponseFuture
     *
     * @param requestId 请求唯一标识
     * @return ResponseFuture对象
     */
    public ResponseFuture createFuture(String requestId) {
        ResponseFuture future = new ResponseFuture();
        futurePool.put(requestId, future);
        return future;
    }

    /**
     * 设置rpc响应结果
     *
     * @param response rpc响应对象
     */
    public synchronized void setResponse(RpcResponse response) {
        ResponseFuture future = futurePool.get(response.getId());
        if (future != null) {
            future.setResponse(response);
        }
    }

    /**
     * 移除ResponseFuture
     *
     * @param requestId 请求唯一标识
     */
    public synchronized void removeFuture(String requestId) {
        futurePool.remove(requestId);
    }

    /**
     * 移除ResponseFuture
     *
     * @param future ResponseFuture
     */
    public synchronized void removeFuture(ResponseFuture future) {
        for (Map.Entry<String, ResponseFuture> entry : futurePool.entrySet()) {
            if (entry.getValue() == future) {
                futurePool.remove(entry.getKey());
                break;
            }
        }
    }
}