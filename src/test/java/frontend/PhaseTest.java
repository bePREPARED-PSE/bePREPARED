package frontend;

import edu.kit.pse.beprepared.BepreparedApplication;
import guitestutils.ClearRepos;
import guitestutils.ExposedBrowserWebDriverContainerFactory;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BepreparedApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ClearRepos
@Ignore //This test class is buggy
public class PhaseTest {

    @Rule
    public BrowserWebDriverContainer firefox = ExposedBrowserWebDriverContainerFactory.create();
    private WebDriver driver;

    private JavascriptExecutor jse;

    @Before
    public void setUp() {
        driver = firefox.getWebDriver();
        driver.get("http://host.testcontainers.internal:8080");
        Wait<WebDriver> wait = new WebDriverWait(driver, 30);
        //Wait for timeline to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("vis-custom-time")));

        jse = (JavascriptExecutor) driver;
    }

    /**
     * This test checks if when a phase is shifted its events are also shifted
     */

    @Test
    public void shiftPhaseTest() throws InterruptedException {

        //Save Amount of phases/events before the test
        int numPhasesBefore = Math.toIntExact((long) jse.executeScript("return scenarioRepo.getAllPhases(0).length"));
        int numEventsBefore = Math.toIntExact((long) jse.executeScript("return scenarioRepo.getAllEvents(0).length"));

        //Click create event
        driver.findElement(By.id("bp-createeventbtn")).click();

        //Select message event
        driver.findElement(By.id("Message Event")).click();

        //Check eventtime before shifting
        long before = Long.parseLong((String) jse.executeScript("return moment($('#datetimepicker1').datetimepicker('viewDate')).format('x')"));

        //Click create phase
        driver.findElement(By.id("createPhaseButton")).click();

        //Input phase name
        WebElement inputPhaseName = driver.findElement(By.id("modal-phasename"));
        inputPhaseName.clear();
        inputPhaseName.sendKeys("Test");

        //Create phase
        Wait<WebDriver> wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("modal-phasename-btn")));

        Thread.sleep(3000);
        driver.findElement(By.id("modal-phasename-btn")).click();
        Thread.sleep(3000);

        //Save the phase ID
        int phaseID = Integer.parseInt(driver.findElement(By.id("bp-phaseselect")).getAttribute("value"));

        //Create the event
        wait.until(ExpectedConditions.elementToBeClickable(By.id("createEventButton")));

        driver.findElement(By.id("createEventButton")).click();

        //Click phase management
        driver.findElement(By.id("bp-phasebtn")).click();

        //Select phase "Test"
        driver.findElement(By.id("bp-phaseselect")).click();
        driver.findElement(By.xpath("//option[@value='" + phaseID + "']")).click();

        //Change relative time
        WebElement relativeTimeSec = driver.findElement(By.id("rs"));
        relativeTimeSec.clear();
        relativeTimeSec.sendKeys("10");

        //Shift phase
        driver.findElement(By.id("shiftBtn")).click();

        //Click edit event in table
        List<WebElement> rows = driver.findElement(By.id("table-body")).findElements(By.tagName("td"));
        rows.get(0).click();

        //Check eventtime after shifting
        long after = Long.parseLong((String) jse.executeScript("return moment($('#datetimepicker1').datetimepicker('viewDate')).format('x')"));

        //Check amount of phases/events after the test
        int numPhasesAfter = Math.toIntExact((long) jse.executeScript("return scenarioRepo.getAllPhases(0).length"));
        int numEventsAfter = Math.toIntExact((long) jse.executeScript("return scenarioRepo.getAllEvents(0).length"));

        //Check if absolute time has changed
        Assert.assertEquals("Absolute time does not change correctly when phase is shifted!", before + (10 * 1000), after);

        //Check if amount of phases/events is correct
        Assert.assertEquals("Number of events after test isn't what was expected!", numEventsBefore + 1, numEventsAfter);
        Assert.assertEquals("Number of phases after test isn't what was expected!", numPhasesBefore + 1, numPhasesAfter);

        //Delete Event for next tests
        driver.findElement(By.id("deleteEventBtn")).click();
    }

    /**
     * This test checks if when a phase is shifted its events are also shifted
     */

    @Test
    public void discardPhaseTest() throws InterruptedException {

        //Save Amount of phases/events before the test
        int numPhasesBefore = Math.toIntExact((long) jse.executeScript("return scenarioRepo.getAllPhases(0).length"));
        int numEventsBefore = Math.toIntExact((long) jse.executeScript("return scenarioRepo.getAllEvents(0).length"));

        //Click create event
        driver.findElement(By.id("bp-createeventbtn")).click();

        //Select message event
        driver.findElement(By.id("Message Event")).click();

        //Click create phase
        driver.findElement(By.id("createPhaseButton")).click();

        //Input phase name
        WebElement inputPhaseName = driver.findElement(By.id("modal-phasename"));
        inputPhaseName.clear();
        inputPhaseName.sendKeys("Test");

        //Save the phase ID of the standard phase
        int stdPhaseID = Integer.parseInt(driver.findElement(By.id("bp-phaseselect")).getAttribute("value"));

        //Create phase
        Wait<WebDriver> wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("modal-phasename-btn")));

        Thread.sleep(3000);
        driver.findElement(By.id("modal-phasename-btn")).click();
        Thread.sleep(3000);

        //Save the phase ID
        int phaseID = Integer.parseInt(driver.findElement(By.id("bp-phaseselect")).getAttribute("value"));

        //Create the event
        wait.until(ExpectedConditions.elementToBeClickable(By.id("createEventButton")));

        driver.findElement(By.id("createEventButton")).click();

        //Click phase management
        driver.findElement(By.id("bp-phasebtn")).click();

        //Select phase "Test"
        driver.findElement(By.id("bp-phaseselect")).click();
        driver.findElement(By.xpath("//option[@value='" + phaseID + "']")).click();

        //Discard Phase
        driver.findElement(By.id("bp-discardPhase")).click();

        //Accept
        driver.switchTo().alert().accept();

        //Click edit event in table
        List<WebElement> rows = driver.findElement(By.id("table-body")).findElements(By.tagName("td"));
        rows.get(0).click();

        //Check phase after shifting
        phaseID = Integer.parseInt(driver.findElement(By.id("bp-phaseselect")).getAttribute("value"));

        //Check amount of phases/events after the test
        int numPhasesAfter = Math.toIntExact((long) jse.executeScript("return scenarioRepo.getAllPhases(0).length"));
        int numEventsAfter = Math.toIntExact((long) jse.executeScript("return scenarioRepo.getAllEvents(0).length"));

        //Check if in standard phase
        Assert.assertEquals("Event isn't moved into the standard phase when it's phase is discarded!", stdPhaseID, phaseID);

        //Check if amount of phases/events is correct
        Assert.assertEquals("Number of events after test isn't what was expected!", numEventsBefore + 1, numEventsAfter);
        Assert.assertEquals("Number of phases after test isn't what was expected!", numPhasesBefore, numPhasesAfter);

        //Delete Event for next tests
        driver.findElement(By.id("deleteEventBtn")).click();
    }

    /**
     * This test checks if when a phase is deleted its events are also deleted
     */

    @Test
    public void deletePhaseTest() throws InterruptedException {

        //Save Amount of phases/events before the test
        int numPhasesBefore = Math.toIntExact((long) jse.executeScript("return scenarioRepo.getAllPhases(0).length"));
        int numEventsBefore = Math.toIntExact((long) jse.executeScript("return scenarioRepo.getAllEvents(0).length"));

        //Click create event
        driver.findElement(By.id("bp-createeventbtn")).click();

        //Select message event
        driver.findElement(By.id("Message Event")).click();

        //Click create phase
        driver.findElement(By.id("createPhaseButton")).click();

        //Input phase name
        WebElement inputPhaseName = driver.findElement(By.id("modal-phasename"));
        inputPhaseName.clear();
        inputPhaseName.sendKeys("Test");

        //Create phase
        Wait<WebDriver> wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("modal-phasename-btn")));

        Thread.sleep(3000);
        driver.findElement(By.id("modal-phasename-btn")).click();
        Thread.sleep(3000);

        //Save the phase ID
        int phaseID = Integer.parseInt(driver.findElement(By.id("bp-phaseselect")).getAttribute("value"));

        //Create the event
        wait.until(ExpectedConditions.elementToBeClickable(By.id("createEventButton")));

        driver.findElement(By.id("createEventButton")).click();

        //Click phase management
        driver.findElement(By.id("bp-phasebtn")).click();

        //Select phase "Test"
        driver.findElement(By.id("bp-phaseselect")).click();
        driver.findElement(By.xpath("//option[@value='" + phaseID + "']")).click();

        //Delete Phase
        driver.findElement(By.id("bp-deletePhase")).click();

        //Accept
        driver.switchTo().alert().accept();


        //Check amount of phases/events after the test
        int numPhasesAfter = Math.toIntExact((long) jse.executeScript("return scenarioRepo.getAllPhases(0).length"));
        int numEventsAfter = Math.toIntExact((long) jse.executeScript("return scenarioRepo.getAllEvents(0).length"));

        //Check if amount of phases/events is correct
        Assert.assertEquals("Number of events after test isn't what was expected!", numEventsBefore, numEventsAfter);
        Assert.assertEquals("Number of phases after test isn't what was expected!", numPhasesBefore, numPhasesAfter);
    }

    @After
    public void tearDown() {
        driver.quit();
    }

}
