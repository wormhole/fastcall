package net.stackoverflow.fastcall;

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

    private static final Map<String, ResponseFuture> futurePool = new HashMap<>();

    /**
     * 生成ResponseFuture
     *
     * @param requestId
     * @return
     */
    public static ResponseFuture createFuture(String requestId) {
        ResponseFuture future = new ResponseFuture();
        futurePool.put(requestId, future);
        return future;
    }

    /**
     * 设置rpc响应结果
     *
     * @param response
     */
    public static void setResponse(RpcResponse response) {
        ResponseFuture future = futurePool.get(response.getId());
        future.setResponse(response.getResponse());
        futurePool.remove(response.getId());
    }

    /**
     * 删除ResponseFuture
     *
     * @param requestId
     */
    public static void removeFuture(String requestId) {
        futurePool.remove(requestId);
    }
}