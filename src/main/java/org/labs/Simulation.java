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
    private final ExecutorService programmerExecutor;
    private final ExecutorService waiterExecutor;
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
        programmerExecutor = Executors.newFixedThreadPool(Math.min(numProgrammers, workerLimit));
        waiterExecutor = Executors.newFixedThreadPool(Math.min(numWaiters, workerLimit));
    }

    public void createResources() {
        Spoon[] spoons = new Spoon[numProgrammers];
        for (int i = 0; i < numProgrammers; i++) {
            spoons[i] = new Spoon();
        }

        for (int i = 0; i < numWaiters; i++) {
            waiterExecutor.submit(new Waiter(restaurant));
        }

        for (int i = 0; i < numProgrammers; i++) {
            Programmer programmer = new Programmer(
                    i, spoons[i], spoons[(i + 1) % numProgrammers], restaurant);
            programmers.add(programmer);
            programmerExecutor.submit(programmer);
        }
    }

    public void startSimulation(long timeout, TimeUnit unit) throws InterruptedException {
        restaurant.open();
        try {
            programmerExecutor.shutdown();
            if (!programmerExecutor.awaitTermination(timeout, unit)) {
                System.out.println("Simulation did not finish in timeout");
                programmerExecutor.shutdownNow();
                waiterExecutor.shutdownNow();
                awaitWorkerTermination(timeout, unit);
                return;
            }

            restaurant.stopWaiters(numWaiters);
            waiterExecutor.shutdown();
            if (!awaitWorkerTermination(timeout, unit)) {
                System.out.println("Workers did not stop in timeout");
                programmerExecutor.shutdownNow();
                waiterExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            programmerExecutor.shutdownNow();
            waiterExecutor.shutdownNow();
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    private boolean awaitWorkerTermination(long timeout, TimeUnit unit) throws InterruptedException {
        boolean programmersStopped = programmerExecutor.awaitTermination(timeout, unit);
        boolean waitersStopped = waiterExecutor.awaitTermination(timeout, unit);
        return programmersStopped && waitersStopped;
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
