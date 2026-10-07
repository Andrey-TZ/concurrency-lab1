package org.labs;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int numProgrammers = 7;
        int numWaiters = 4;
        int food = 100_000;

        Simulation simulation = new Simulation(numProgrammers, numWaiters, food);
        simulation.createResources();
        simulation.startSimulation(30, TimeUnit.MINUTES);
    }
}