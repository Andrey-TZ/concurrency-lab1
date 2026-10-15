package org.labs;

public class Programmer implements Runnable {
    private final int id;
    private final Spoon leftSpoon;
    private final Spoon rightSpoon;
    private final Restaurant restaurant;
    private volatile int mealsEaten;

    public Programmer(int id, Spoon leftSpoon, Spoon rightSpoon, Restaurant restaurant) {
        this.id = id;
        this.leftSpoon = leftSpoon;
        this.rightSpoon = rightSpoon;
        this.restaurant = restaurant;
    }

    public int getId() {
        return id;
    }

    public int getMealsEaten() {
        return mealsEaten;
    }

    @Override
    public void run() {
        try {
            restaurant.awaitOpening();
            while (!Thread.currentThread().isInterrupted()) {
                think();
                Order order = new Order(id, mealsEaten);
                restaurant.placeOrder(order);
                order.place();
                if (!order.isRefilled()) {
                    return;
                }

                takeSpoons();
                try {
                    eat();
                } finally {
                    putSpoons();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    void think() throws InterruptedException {
        Thread.sleep(2);
    }

    void takeSpoons() {
        if (id % 2 == 0) {
            leftSpoon.pickUp();
            rightSpoon.pickUp();
        } else {
            rightSpoon.pickUp();
            leftSpoon.pickUp();
        }
    }

    void eat() throws InterruptedException {
        Thread.sleep(2);
        mealsEaten++;
    }

    void putSpoons() {
        if (id % 2 == 0) {
            rightSpoon.putDown();
            leftSpoon.putDown();
        } else {
            leftSpoon.putDown();
            rightSpoon.putDown();
        }
    }
}
