package org.labs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Simulation {
    private final int numProgrammers;
    private final int numWaiters;
    private final int food;
    private final Restaurant restaurant;
    private ExecutorService executorService;
    private final List<Programmer> programmers;


    public Simulation(
            int numProgrammers,
            int numWaiters,
            int food
    ) {
        this.numProgrammers = numProgrammers;
        this.numWaiters = numWaiters;
        this.food = food;
        restaurant = new Restaurant(food);
        programmers = new ArrayList<>(numProgrammers);
        executorService = Executors.newFixedThreadPool(numProgrammers + numWaiters + 1);
    }

    public void createResources() {

        Spoon[] spoons = new Spoon[numProgrammers];
        for (int i = 0; i < numProgrammers; i++) {
            spoons[i] = new Spoon();
        }

        for (int i = 0; i < numWaiters; i++) {
            executorService.submit(new Waiter(restaurant));
        }

        for (int i = 0; i < numProgrammers; i++) {
            Spoon leftSpoon = spoons[i];
            Spoon rightSpoon = spoons[(i + 1) % numProgrammers];

            Programmer p = new Programmer(i, leftSpoon, rightSpoon, restaurant);
            programmers.add(p);
            executorService.submit(p);
        }
    }

    public void startSimulation(long timeout, TimeUnit unit) throws InterruptedException {

        Future<?> restaurantTask = executorService.submit(() -> {
            restaurant.open();
            try {
                restaurant.awaitEmptyPot();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            restaurant.closeRestaurant();
        });

        try {
            restaurantTask.get(timeout, unit);
        } catch (TimeoutException e) {
            System.out.println("Restaurant did not finish in timeout");
            restaurantTask.cancel(true);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdownNow();
        }
    }

    public long getTotalEaten() {
        int totalEaten = 0;
        for (Programmer p : programmers) {
            totalEaten += p.getMealsEaten();
            System.out.println("Программист " + p.getId() + " съел " + p.getMealsEaten() + " порций");
        }
//        System.out.println("Всего порций " + totalEaten);
        return totalEaten;
    }

    public List<Programmer> getProgrammers() {
        return programmers;
    }
}
