package org.labs;

public class Waiter implements Runnable {
    private final Restaurant restaurant;

    public Waiter(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            restaurant.awaitOpening();
            while (true) {
                Order order = restaurant.takeNextOrder();
                if (order.isStopSignal()) {
                    return;
                }
                restaurant.fulfill(order);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
