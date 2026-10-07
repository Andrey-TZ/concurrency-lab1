import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.labs.*;

import java.util.concurrent.TimeUnit;

public class FullTest {

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
