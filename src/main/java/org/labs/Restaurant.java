package org.labs;

import java.util.concurrent.CountDownLatch;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class Restaurant {
    private int remainingFood;
    private final Queue<Order> orders = new PriorityQueue<>(
            Comparator.comparingInt(Order::getMealsEaten)
                    .thenComparingLong(Order::getSequence));
    private final CountDownLatch start = new CountDownLatch(1);
    private boolean acceptingOrders = true;
    private int waitingStopSignals;
    private long nextOrderSequence;

    public Restaurant(int food) {
        if (food < 0) {
            throw new IllegalArgumentException("Food count cannot be negative");
        }
        remainingFood = food;
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
            order.setSequence(nextOrderSequence++);
            orders.add(order);
            notifyAll();
        } else {
            order.fulfill(false);
        }
    }

    public synchronized Order takeNextOrder() throws InterruptedException {
        while (orders.isEmpty() && waitingStopSignals == 0) {
            wait();
        }

        if (orders.isEmpty()) {
            waitingStopSignals--;
            return Order.stopSignal();
        }

        Order order = orders.remove();
        boolean refilled = remainingFood > 0;
        if (refilled) {
            remainingFood--;
            if (remainingFood == 0) {
                acceptingOrders = false;
            }
        }
        order.fulfill(refilled);
        return order;
    }

    public synchronized void stopWaiters(int count) {
        waitingStopSignals += count;
        notifyAll();
    }
}
