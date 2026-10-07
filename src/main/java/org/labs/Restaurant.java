package org.labs;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Restaurant {
    private final AtomicInteger totalFood;
    private final BlockingQueue<Order> orders = new LinkedBlockingQueue<>();
    private final CountDownLatch start = new CountDownLatch(1);
    private boolean acceptingOrders = true;

    public Restaurant(int food) {
        totalFood = new AtomicInteger(food);
        if (food == 0) {
            acceptingOrders = false;
        }
    }

    public void open() {
        start.countDown();
    }

    public void awaitOpening() throws InterruptedException {
        start.await();
    }

    public synchronized void placeOrder(Order order) throws InterruptedException {
        if (acceptingOrders) {
            orders.put(order);
        } else {
            order.fulfill(false);
        }
    }

    public Order takeNextOrder() throws InterruptedException {
        return orders.take();
    }

    public void fulfill(Order order) {
        int remaining = totalFood.decrementAndGet();
        if (remaining >= 0) {
            if (remaining == 0) {
                synchronized (this) {
                    acceptingOrders = false;
                }
            }
            order.fulfill(true);
        } else {
            totalFood.incrementAndGet();
            synchronized (this) {
                acceptingOrders = false;
            }
            order.fulfill(false);
        }
    }

    public void stopWaiters(int count) throws InterruptedException {
        for (int i = 0; i < count; i++) {
            orders.put(Order.stopSignal());
        }
    }
}
