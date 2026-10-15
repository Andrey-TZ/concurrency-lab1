package org.labs;

import java.util.concurrent.CountDownLatch;

public class Order {
    private static final Order STOP = new Order(true, -1, Integer.MAX_VALUE);
    private final CountDownLatch lock = new CountDownLatch(1);
    private final boolean stopSignal;
    private final int programmerId;
    private final int mealsEaten;
    private long sequence;
    private volatile boolean isRefilled;

    public Order() {
        this(-1, 0);
    }

    public Order(int programmerId, int mealsEaten) {
        this(false, programmerId, mealsEaten);
        if (mealsEaten < 0) {
            throw new IllegalArgumentException("Meals eaten cannot be negative");
        }
    }

    private Order(boolean stopSignal, int programmerId, int mealsEaten) {
        this.stopSignal = stopSignal;
        this.programmerId = programmerId;
        this.mealsEaten = mealsEaten;
    }

    public static Order stopSignal() {
        return STOP;
    }

    public boolean isStopSignal() {
        return stopSignal;
    }

    public int getProgrammerId() {
        return programmerId;
    }

    public int getMealsEaten() {
        return mealsEaten;
    }

    long getSequence() {
        return sequence;
    }

    void setSequence(long sequence) {
        this.sequence = sequence;
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
