package org.labs;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Restaurant {
    private final AtomicInteger totalFood;
    private final BlockingQueue<Order> orders;
    private final CountDownLatch start = new CountDownLatch(1);
    private final AtomicBoolean stop = new AtomicBoolean(false);
    private final CountDownLatch finishLatch = new CountDownLatch(1);

    public void open() {
        start.countDown();
    }

    public AtomicBoolean isStopped() {
        return stop;
    }

    public void awaitOpening() throws InterruptedException {
        start.await();
    }

    public  void emptyPot() {
        finishLatch.countDown();
    }

    public void awaitEmptyPot() throws InterruptedException {
        finishLatch.await();
    }

    public void closeRestaurant() {
        stop.set(true);
    }

    public Restaurant(int food) {
        totalFood = new AtomicInteger(food);
        orders = new LinkedBlockingQueue<>();
    }

    public boolean tryTakePortion() {
        int previous = totalFood.getAndUpdate(x -> x > 0 ? x - 1 : 0);
        return previous > 0;
    }

    public Order takeNextOrder() throws InterruptedException {
        return orders.take();
    }

    public void placeOrder(Order order) throws InterruptedException {
        orders.put(order);
    }
}
