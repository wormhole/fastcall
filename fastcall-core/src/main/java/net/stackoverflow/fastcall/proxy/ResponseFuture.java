package net.stackoverflow.fastcall.proxy;

/**
 * 异步调用Promise对象
 *
 * @author wormhole
 */
public class ResponseFuture {

    private static final Object lock = new Object();

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
                    e.printStackTrace();
                }
            }
            return response;
        }
    }
}
