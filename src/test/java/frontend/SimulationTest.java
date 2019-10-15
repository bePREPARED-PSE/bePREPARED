package frontend;

import edu.kit.pse.beprepared.BepreparedApplication;
import guitestutils.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BepreparedApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ClearRepos
@Ignore
public class SimulationTest {

    @Rule
    public BrowserWebDriverContainer firefox = ExposedBrowserWebDriverContainerFactory.create();
    private WebDriver driver;

    private JavascriptExecutor jse;

    @Before
    public void setUp() throws Exception {
        driver = firefox.getWebDriver();
        driver.get("http://host.testcontainers.internal:8080");
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        Wait<WebDriver> wait = new WebDriverWait(driver, 15);
        //Wait for timeline to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("vis-custom-time")));

        jse = (JavascriptExecutor) driver;

        //Create config
        jse.executeScript("clickConfig()"); //Click config and wait until its open
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modal-config")));

        //Enter stuff
        driver.findElement(By.id("frostServerUrl")).sendKeys("http://localhost");
        driver.findElement(By.id("telegramAuthToken")).sendKeys("abc");
        driver.findElement(By.id("tpUname")).sendKeys("Bouncy");
        driver.findElement(By.id("tpPswd")).sendKeys("Cats");

        //Click save
        driver.findElement(By.id("modal-config-save")).click();

        Thread.sleep(3000);

        //Add event
        //Click create event
        driver.findElement(By.id("bp-createeventbtn")).click();
        //Select message event
        driver.findElement(By.id("Message Event")).click();

        WebElement msg = driver.findElement(By.id("message"));
        msg.clear();
        msg.sendKeys("Testmessage");

        WebElement rm = driver.findElement(By.id("rm"));
        rm.clear();
        rm.sendKeys("1");

        //Create the event
        driver.findElement(By.id("createEventButton")).click();
    }

    /**
     * This test checks, if the time marker is moved when the simulation is started.
     */
    @Test
    public void startSimulationTest() throws Exception {
        driver.findElement(By.id("bp-startpausebtn")).click();
        long stime = Long.parseLong(jse.executeScript("return configRepo.get(currentScenario).scenarioStartTime").toString());

        Thread.sleep(3000);

        long time = Long.parseLong(jse.executeScript("return timeline.currentTime").toString());
        Assert.assertTrue("Current time on timeline is not moved!", time >= stime);
    }

    /**
     * This test checks, if the time marker is reset to the start time when the simulation is stopped.
     */
    @Test
    public void stopSimulationTest() throws Exception {
        driver.findElement(By.id("bp-startpausebtn")).click();
        long stime = Long.parseLong(jse.executeScript("return configRepo.get(currentScenario).scenarioStartTime").toString());

        //Wait 5 seconds and click stop
        Thread.sleep(5000);
        driver.findElement(By.id("bp-stopbtn")).click();
        driver.switchTo().alert().dismiss();

        //Check if time marker is reset
        Wait<WebDriver> wait = new WebDriverWait(driver, 5);
        wait.until((ExpectedCondition<Boolean>) webDriver -> {
            long time = Long.parseLong(jse.executeScript("return timeline.currentTime").toString());
            return time == stime;
        });
    }

    /**
     * This test checks, if the time marker remains on its position when the simulation is stopped.
     */
    @Test
    public void pauseSimulationTest() throws Exception {
        driver.findElement(By.id("bp-startpausebtn")).click();

        //Wait and pause
        Thread.sleep(5000);
        driver.findElement(By.id("bp-startpausebtn")).click();
        Thread.sleep(1000); //wait here, because the timeline takes a short moment to pause
        long time1 = Long.parseLong(jse.executeScript("return timeline.currentTime").toString());
        Thread.sleep(5000);

        long time2 = Long.parseLong(jse.executeScript("return timeline.currentTime").toString());
        //Check if simulation is till paused
        Assert.assertEquals("Simulation was not paused!", time1, time2);
    }

    @After
    public void tearDown() {
        driver.quit();
    }

}
