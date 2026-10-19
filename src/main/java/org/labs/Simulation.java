package org.labs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Simulation {
    private final int numProgrammers;
    private final int numWaiters;
    private final Restaurant restaurant;
    private final ExecutorService executorService;
    private final List<Programmer> programmers;

    public Simulation(int numProgrammers, int numWaiters, int food) {
        if (numProgrammers < 2) {
            throw new IllegalArgumentException("At least two programmers are required");
        }
        if (numWaiters < 1) {
            throw new IllegalArgumentException("At least one waiter is required");
        }

        this.numProgrammers = numProgrammers;
        this.numWaiters = numWaiters;
        restaurant = new Restaurant(food);
        programmers = new ArrayList<>(numProgrammers);
        int workerLimit = Math.max(1, Runtime.getRuntime().availableProcessors());
        executorService = Executors.newFixedThreadPool(Math.min(numProgrammers + numWaiters + 1, workerLimit));
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
            Programmer programmer = new Programmer(
                    i, spoons[i], spoons[(i + 1) % numProgrammers], restaurant);
            programmers.add(programmer);
            executorService.submit(programmer);
        }
    }

    public void startSimulation(long timeout, TimeUnit unit) throws InterruptedException {
        restaurant.open();
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(timeout, unit)) {
                System.out.println("Simulation did not finish in timeout");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    public long getTotalEaten() {
        long totalEaten = 0;
        for (Programmer programmer : programmers) {
            totalEaten += programmer.getMealsEaten();
            System.out.println("Программист " + programmer.getId()
                    + " съел " + programmer.getMealsEaten() + " порций");
        }
        return totalEaten;
    }

    public List<Programmer> getProgrammers() {
        return programmers;
    }
}
