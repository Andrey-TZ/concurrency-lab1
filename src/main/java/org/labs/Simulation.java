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
    private final CountDownLatch programmersFinished;


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
        programmersFinished = new CountDownLatch(numProgrammers);
        executorService = Executors.newFixedThreadPool(numProgrammers + numWaiters);
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

            Programmer p = new Programmer(i, leftSpoon, rightSpoon, restaurant, programmersFinished);
            programmers.add(p);
            executorService.submit(p);
        }
    }

    public void startSimulation(long timeout, TimeUnit unit) throws InterruptedException {
        restaurant.open();
        try {
            if (!programmersFinished.await(timeout, unit)) {
                System.out.println("Simulation did not finish in timeout");
                executorService.shutdownNow();
                if (!executorService.awaitTermination(timeout, unit)) {
                    System.out.println("Workers did not stop after interruption");
                }
                return;
            }

            restaurant.stopWaiters(numWaiters);
            executorService.shutdown();
            if (!executorService.awaitTermination(timeout, unit)) {
                System.out.println("Workers did not stop in timeout");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            throw e;
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
