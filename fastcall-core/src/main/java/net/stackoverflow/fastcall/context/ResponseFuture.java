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

    private final Object lock = new Object();

    private volatile boolean success = false;

    private volatile RpcResponse response;

    public boolean isSuccess() {
        synchronized (lock) {
            return success;
        }
    }

    public void setResponse(RpcResponse response) {
        synchronized (lock) {
            if (this.response != null) {
                return;
            }
            this.success = true;
            this.response = response;
            lock.notifyAll();
        }
    }

    public RpcResponse getResponse(long milliseconds) {
        synchronized (lock) {
            if (!success) {
                try {
                    if (milliseconds == -1) {
                        lock.wait();
                    } else {
                        lock.wait(milliseconds);
                    }
                } catch (InterruptedException e) {
                    log.error("ResponseFuture fail to get response", e);
                }
            }
            return response;
        }
    }
}
