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
public class PageLoadTest {

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
    }

    @Test
    public void pageLoadTest() {
        driver.findElement(By.id("mapid"));                     //Map
        driver.findElement(By.className("vis-custom-time"));    //Timeline
        driver.findElement(By.id("mydatatable"));               //Table
        driver.findElement(By.className("slider"));             //Slider
        //if we reach this line everything is fine
    }

    @After
    public void tearDown() {
        driver.quit();
    }

}
