package org.labs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class Order {
    private CountDownLatch lock = new CountDownLatch(1);
    private Boolean isRefilled;


    public void place() throws InterruptedException {
        lock.await();
    }

    public void fulfill(boolean isRefilled) {
        this.isRefilled = isRefilled;
        lock.countDown();
    }

    public boolean isRefilled() {
        return isRefilled;
    }
}
