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

import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BepreparedApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ClearRepos
@Ignore
public class CreateEventTest {

    @Rule
    public BrowserWebDriverContainer firefox = ExposedBrowserWebDriverContainerFactory.create();
    private WebDriver driver;

    private JavascriptExecutor jse;

    @Before
    public void setUp() {
        driver = firefox.getWebDriver();
        driver.get("http://host.testcontainers.internal:8080");
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        Wait<WebDriver> wait = new WebDriverWait(driver, 15);
        //Wait for timeline to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("vis-custom-time")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='leaflet-layer ']")));

        jse = (JavascriptExecutor) driver;
    }

    /**
     * This tests creates 3 events and checks, if they are displayed on timeline, table and map.
     */
    @Test
    public void creatEventTest() {
        //Message Event -----------------------------------------------------------------------------------------------

        //Click create event
        driver.findElement(By.id("bp-createeventbtn")).click();
        //Select message event
        driver.findElement(By.id("Message Event")).click();
        //Create the event
        driver.findElement(By.id("createEventButton")).click();

        //Sensor Event ------------------------------------------------------------------------------------------------

        //Click create event
        driver.findElement(By.id("bp-createeventbtn")).click();
        //Select message event
        driver.findElement(By.id("sensorthings: Create Observation")).click();
        //Create the event
        driver.findElement(By.id("createEventButton")).click();

        //Location Event ----------------------------------------------------------------------------------------------

        //Click create event
        driver.findElement(By.id("bp-createeventbtn")).click();
        //Select message event
        driver.findElement(By.id("sensorthings: Add Location")).click();
        //Set Location
        driver.findElement(By.id("geolat")).sendKeys("48.123");
        driver.findElement(By.id("geolon")).sendKeys("8.456");
        //Create the event
        driver.findElement(By.id("createEventButton")).click();

        // ------------------------------------------------------------------------------------------------------------

        Wait<WebDriver> wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.numberOfElementsToBe(By.xpath("//tbody[@id='table-body']/tr"), 3));
        wait.until(ExpectedConditions.numberOfElementsToBe(By.xpath("//div[@class='vis-item vis-line vis-readonly']"), 3));
        wait.until(ExpectedConditions.numberOfElementsToBe(By.xpath("//div[@class='leaflet-pane leaflet-marker-pane']/div"), 1));
    }

    /**
     * This test checks, if the inpudescriptors are loaded.
     */
    @Test
    public void inputDescriptorTest() {
        //Click create event
        driver.findElement(By.id("bp-createeventbtn")).click();
        //Select message event
        driver.findElement(By.id("sensorthings: Add Location")).click();

        driver.findElement(By.id("name"));
        driver.findElement(By.id("description"));
        driver.findElement(By.id("thingId"));
        driver.findElement(By.id("geolat"));
        driver.findElement(By.id("geolon"));
        //If we reach this line every element was found
    }

    /**
     * This test checks, if when the relative time changes, the absolute time is set correctly.
     */
    @Test
    public void setEventTimeTest() {
        //Click create event
        driver.findElement(By.id("bp-createeventbtn")).click();
        //Select message event
        driver.findElement(By.id("Message Event")).click();

        long before = Long.parseLong((String) jse.executeScript("return moment($('#datetimepicker1').datetimepicker('viewDate')).format('x')"));

        //Change relative time
        WebElement relativeTimeSec = driver.findElement(By.id("rs"));
        relativeTimeSec.clear();
        relativeTimeSec.sendKeys("10");

        long after = Long.parseLong((String) jse.executeScript("return moment($('#datetimepicker1').datetimepicker('viewDate')).format('x')"));

        //Check if absolute time has changed
        Assert.assertEquals("Absolute time does not change correctly when relative time is changed!", before + (10 * 1000), after);
    }

    /**
     * This test checks, if you can create a new phase while creating an event.
     */
    @Test
    public void createMsgEventWithPhase() {
        //Click create event
        driver.findElement(By.id("bp-createeventbtn")).click();
        //Select message event
        driver.findElement(By.id("Message Event")).click();
        //Click create Phase Button
        driver.findElement(By.id("createPhaseButton")).click();

        //Set phasename to 'Testphase'
        WebElement phasename = driver.findElement(By.id("modal-phasename"));
        phasename.sendKeys("Testphase");

        //Create phase
        driver.findElement(By.id("modal-phasename-btn")).click();

        //Get options of phase
        WebElement select = driver.findElement(By.id("bp-phaseselect"));
        List<WebElement> options = select.findElements(By.tagName("option"));

        //Check if phase was created
        Assert.assertEquals("Got wrong amount of phases!", 2, options.size());

        //Check if phasename is correct
        Assert.assertTrue("Phasename is wrong: " + options.get(1).getText(), options.get(1).getText().startsWith("Testphase"));
    }

    /**
     * This test checks, if the middle panel remains open when a wrong value is submitted.
     */
    @Test
    public void wrongValueTest() {
        //Click create event
        driver.findElement(By.id("bp-createeventbtn")).click();
        //Select message event
        driver.findElement(By.id("Message Event")).click();
        //Clear message field
        driver.findElement(By.id("message")).clear();

        driver.findElement(By.id("createEventButton")).click();
        WebElement middle = driver.findElement(By.id("middlePanel"));
        String classes = middle.getAttribute("class");

        //Check if middle panel is still visible
        Assert.assertEquals("Wrong value was accepted!", "col", classes);
    }

    @After
    public void tearDown() {
        driver.quit();
    }

}
