import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.labs.*;

import java.util.concurrent.TimeUnit;

public class FullTest {

    @Test
    void distributesFoodWithinTwentyPercentOfAverage() throws InterruptedException {
        int numProgrammers = 7;
        int numWaiters = 3;
        int food = 700;

        Simulation simulation = new Simulation(numProgrammers, numWaiters, food);
        simulation.createResources();
        simulation.startSimulation(1, TimeUnit.MINUTES);

        double averageMeals = (double) food / numProgrammers;
        double allowedDeviation = averageMeals * 0.20;

        for (Programmer programmer : simulation.getProgrammers()) {
            int meals = programmer.getMealsEaten();
            Assertions.assertTrue(
                    Math.abs(meals - averageMeals) <= allowedDeviation,
                    () -> "Programmer " + programmer.getId() + " ate " + meals
                            + " portions; expected about " + averageMeals
                            + " (allowed deviation: " + allowedDeviation + ")"
            );
        }
    }

    @ParameterizedTest
    @CsvSource({
            "2, 1, 100",
            "5, 2, 1000",
            "7, 2, 100000",
            "10, 4, 100000",
    })
    void checkAllFoodEaten(
            int numProgrammers,
            int numWaiters,
            int food
    ) throws InterruptedException {
        Simulation simulation = new Simulation(numProgrammers, numWaiters, food);
        simulation.createResources();
        simulation.startSimulation(2, TimeUnit.HOURS);

        for (Programmer p : simulation.getProgrammers()) {
            Assertions.assertNotEquals(0, p.getMealsEaten(), "Programmer didn't eat");
        }
        Assertions.assertEquals(food, simulation.getTotalEaten(), "total eaten portions are not equal number of portions");
    }
}
