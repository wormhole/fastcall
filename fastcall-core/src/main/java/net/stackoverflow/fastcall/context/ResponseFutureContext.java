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

    private static final Map<String, ResponseFuture> futurePool = new HashMap<>();

    /**
     * 生成ResponseFuture
     *
     * @param requestId 请求唯一标识
     * @return ResponseFuture对象
     */
    public static ResponseFuture createFuture(String requestId) {
        ResponseFuture future = new ResponseFuture();
        futurePool.put(requestId, future);
        return future;
    }

    /**
     * 设置rpc响应结果
     *
     * @param response rpc响应对象
     */
    public static void setResponse(RpcResponse response) {
        ResponseFuture future = futurePool.get(response.getId());
        future.setResponse(response);
        futurePool.remove(response.getId());
    }

    /**
     * 删除ResponseFuture
     *
     * @param requestId 请求唯一标识
     */
    public static void removeFuture(String requestId) {
        futurePool.remove(requestId);
    }
}
