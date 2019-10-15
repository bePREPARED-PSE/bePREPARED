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
public class EditEventTest {

    @Rule
    public BrowserWebDriverContainer firefox = ExposedBrowserWebDriverContainerFactory.create();
    private WebDriver driver;


    @Before
    public void setUp() {
        driver = firefox.getWebDriver();
        driver.get("http://host.testcontainers.internal:8080");
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        Wait<WebDriver> wait = new WebDriverWait(driver, 15);
        //Wait for timeline to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("vis-custom-time")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='leaflet-layer ']")));

        //Message Event -----------------------------------------------------------------------------------------------

        //Click create event
        driver.findElement(By.id("bp-createeventbtn")).click();
        //Select message event
        driver.findElement(By.id("sensorthings: Add Location")).click();
        //Set Location
        driver.findElement(By.id("geolat")).sendKeys("48.123");
        driver.findElement(By.id("geolon")).sendKeys("8.456");
        //Create the event
        driver.findElement(By.id("createEventButton")).click();
    }

    /**
     * This test checks, if the message event can be edited and if the correct value was loaded. (Table)
     */
    @Test
    public void clickEditOnTableTest() {
        List<WebElement> rows = driver.findElement(By.id("table-body")).findElements(By.tagName("td"));
        rows.get(0).click();

        String msg = driver.findElement(By.id("geolat")).getAttribute("value");
        Assert.assertEquals("Wrong value was loaded!", "48.123", msg);
    }

    /**
     * This test checks, if the message event can be edited and if the correct value was loaded. (Timeline)
     */
    @Test
    public void clickEditOnTimelineTest() {
        List<WebElement> items = driver.findElement(By.className("vis-timeline")).findElements(By.className("vis-item-content"));
        items.get(0).click();

        String msg = driver.findElement(By.id("geolat")).getAttribute("value");
        Assert.assertEquals("Wrong value was loaded!", "48.123", msg);
    }

    /**
     * This test checks, if the message event can be edited and if the correct value was loaded. (Map)
     */
    @Test
    public void clickEditOnMap() {
        List<WebElement> items = driver.findElements(By.xpath("//div[@class='leaflet-pane leaflet-marker-pane']/div"));
        items.get(0).click();

        String msg = driver.findElement(By.id("geolat")).getAttribute("value");
        Assert.assertEquals("Wrong value was loaded!", "48.123", msg);
    }

    @After
    public void tearDown() {
        driver.quit();
    }

}
