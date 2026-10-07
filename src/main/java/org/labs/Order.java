package org.labs;

import java.util.concurrent.CountDownLatch;

public class Order {
    private static final Order STOP = new Order(true);
    private final CountDownLatch lock = new CountDownLatch(1);
    private final boolean stopSignal;
    private volatile boolean isRefilled;

    public Order() {
        this(false);
    }

    private Order(boolean stopSignal) {
        this.stopSignal = stopSignal;
    }

    public static Order stopSignal() {
        return STOP;
    }

    public boolean isStopSignal() {
        return stopSignal;
    }


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
