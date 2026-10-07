package org.labs;

public class Programmer implements Runnable{
    private final int id;
    private final Spoon leftSpoon;
    private final Spoon rightSpoon;
    private final Restaurant restaurant;

    private int mealsEaten = 0;

    public Programmer(int id, Spoon leftSpoon, Spoon rightSpoon, Restaurant restaurant) {
        this.id = id;
        this.leftSpoon = leftSpoon;
        this.rightSpoon = rightSpoon;
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            restaurant.awaitOpening();
            while (true) {
                think();

                Order order = new Order();
                restaurant.placeOrder(order);
                order.place();

                if (!order.isRefilled()) {
                    break;
                }

                takeSpoons();
                try {
                    eat();
                } finally {
                    putSpoons();
                }


            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void think() throws InterruptedException {
        Thread.sleep(100);
    }

    private void eat() throws InterruptedException {
        mealsEaten++;
        //System.out.printf("Программист %d съел %d порцию \n", id, mealsEaten);
        Thread.sleep(100);
    }

    private void takeSpoons() {
        if (id % 2 == 0) {
            leftSpoon.pickUp();
            rightSpoon.pickUp();
        } else {
            rightSpoon.pickUp();
            leftSpoon.pickUp();
        }
    }

    private void putSpoons() {
        if (id % 2 == 0) {
            rightSpoon.putDown();
            leftSpoon.putDown();
        } else {
            leftSpoon.putDown();
            rightSpoon.putDown();
        }
    }

    public int getMealsEaten() {
        return mealsEaten;
    }

    public int getId() {
        return id;
    }
}
