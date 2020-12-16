package net.stackoverflow.fastcall.context;

import net.stackoverflow.fastcall.transport.proto.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异步调用结果Future对象
 *
 * @author wormhole
 */
public class ResponseFuture {

    private static final Logger log = LoggerFactory.getLogger(ResponseFuture.class);

    private final String requestId;

    private volatile boolean success = false;

    private volatile RpcResponse response;

    public ResponseFuture(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public synchronized boolean isSuccess() {
        return success;
    }

    public synchronized void setResponse(RpcResponse response) {
        if (this.response != null) {
            return;
        }
        this.success = true;
        this.response = response;
        this.notifyAll();
    }

    public synchronized RpcResponse getResponse(long milliseconds) {
        if (!success) {
            try {
                if (milliseconds == -1) {
                    this.wait();
                } else {
                    this.wait(milliseconds);
                }
            } catch (InterruptedException e) {
                log.error("ResponseFuture fail to get response", e);
            }
        }
        return response;
    }
}
