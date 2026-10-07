package org.labs;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Waiter implements Runnable{
    private final Restaurant restaurant;

    public Waiter(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            restaurant.awaitOpening();
            while (!restaurant.isStopped().get()) {
                var order = restaurant.takeNextOrder();

                boolean hasFood = restaurant.tryTakePortion();
                if (!hasFood) {
                    restaurant.emptyPot();
                }

                order.fulfill(hasFood);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }




}
