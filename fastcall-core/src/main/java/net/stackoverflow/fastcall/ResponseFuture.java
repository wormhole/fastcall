package net.stackoverflow.fastcall;

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

    private volatile Object response;

    public boolean isSuccess() {
        synchronized (lock) {
            return success;
        }
    }

    public void setResponse(Object response) {
        synchronized (lock) {
            if (this.response != null) {
                return;
            }
            this.success = true;
            this.response = response;
            lock.notifyAll();
        }
    }

    public Object getResponse() {
        synchronized (lock) {
            while (!success) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    log.error("Get response fail", e);
                }
            }
            return response;
        }
    }
}
