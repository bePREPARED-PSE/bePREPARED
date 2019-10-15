package guitestutils;

import edu.kit.pse.beprepared.model.*;
import edu.kit.pse.beprepared.simulation.Simulation;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.io.File;
import java.lang.reflect.Field;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class CustomTestExecutionListener extends AbstractTestExecutionListener {

    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void beforeTestMethod(TestContext testContext) {
        ApplicationContext context = testContext.getApplicationContext();

        //Rest Repos + nextID counter

        //Get Repos
        ScenarioRepository scenarioRepository = context.getBean(ScenarioRepository.class);
        ConfigurationRepository configurationRepository = context.getBean(ConfigurationRepository.class);
        SimulationRepository simulationRepository = context.getBean(SimulationRepository.class);

        try {
            //Clear repos
            Field sRepo = ScenarioRepository.class.getDeclaredField("scenarios");
            Field cRepo = ConfigurationRepository.class.getDeclaredField("configs");
            Field simRepo = SimulationRepository.class.getDeclaredField("simulations");

            sRepo.setAccessible(true);
            cRepo.setAccessible(true);
            simRepo.setAccessible(true);

            sRepo.set(scenarioRepository, new LinkedList<Scenario>());
            cRepo.set(configurationRepository, new LinkedList<Configuration>());
            simRepo.set(simulationRepository, new LinkedList<Simulation>());

            //Rest nextID counters
            Field[] fields = {
                    Scenario.class.getDeclaredField("nextID"),
                    Phase.class.getDeclaredField("nextID"),
                    Event.class.getDeclaredField("nextID"),
                    Configuration.class.getDeclaredField("nextID"),
                    Simulation.class.getDeclaredField("nextId"),
            };

            for (Field f : fields) {
                f.setAccessible(true);
                f.set(null, 0);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}