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
public class ConfigurationTest {

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

        jse = (JavascriptExecutor) driver;
    }

    /**
     * This test checks, if it is possible to edit the standard config.
     */
    @Test
    public void editStandardConfigTest() {
        jse.executeScript("clickConfig()"); //Click config and wait until its open
        Wait<WebDriver> wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modal-config")));

        //Enter stuff
        driver.findElement(By.id("frostServerUrl")).sendKeys("http://localhost");
        driver.findElement(By.id("telegramAuthToken")).sendKeys("abc");
        driver.findElement(By.id("tpUname")).sendKeys("Bouncy");
        driver.findElement(By.id("tpPswd")).sendKeys("Cats");

        //Click save
        driver.findElement(By.id("modal-config-save")).click();

        //Search for modal, check if 'show' class is removed
        WebElement modal = driver.findElement(By.id("modal-config"));
        Assert.assertEquals("Config modal was not closed, edit failed!", "modal fade", modal.getAttribute("class"));
    }

    /**
     * This test checks, if it is possible to create a new config
     */
    @Test
    public void createNewConfigTest() {
        jse.executeScript("clickConfig()"); //Click config and wait until its open
        Wait<WebDriver> wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modal-config")));

        //Open dropdown and select new config
        driver.findElement(By.id("bp-configselect")).click();
        driver.findElement(By.xpath("//select[@id='bp-configselect']/option[@value='-1']")).click();

        //Enter stuff
        driver.findElement(By.id("telegramAuthToken")).sendKeys("abc");
        driver.findElement(By.id("tpPswd")).sendKeys("Cats");

        //Choose time
        driver.findElement(By.id("datetime2")).sendKeys("08/14/2019 03:00:00 PM");

        //Click save
        driver.findElement(By.id("modal-config-save")).click();

        //Search for modal, check if 'show' class is removed
        WebElement modal = driver.findElement(By.id("modal-config"));
        Assert.assertEquals("Config modal was not closed, edit failed!", "modal fade", modal.getAttribute("class"));

        //Check if displayedconfigs was changed
        wait.until(ExpectedConditions.textToBe(By.id("curConfig"), "1"));
    }

    /**
     * This test checks, if the correct values are loaded for existing configs.
     */
    @Test
    public void loadConfigTest() {
        jse.executeScript("clickConfig()"); //Click config and wait until its open
        Wait<WebDriver> wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modal-config")));

        //Open dropdown and select new config
        driver.findElement(By.id("bp-configselect")).click();
        driver.findElement(By.xpath("//select[@id='bp-configselect']/option[@value='-1']")).click();

        //Enter stuff
        driver.findElement(By.id("telegramAuthToken")).sendKeys("abc");
        driver.findElement(By.id("tpPswd")).sendKeys("Cats");

        //Choose time
        driver.findElement(By.id("datetime2")).sendKeys("08/14/2019 03:00:00 PM");

        //Click save and reopen
        driver.findElement(By.id("modal-config-save")).click();
        jse.executeScript("clickConfig()");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modal-config")));

        //Check if values were loaded
        Assert.assertEquals("TelegramAuthToken is wrong!", "abc", driver.findElement(By.id("telegramAuthToken")).getAttribute("value"));
        Assert.assertEquals("Team position password is wrong!", "Cats", driver.findElement(By.id("tpPswd")).getAttribute("value"));

        //Switch config
        driver.findElement(By.id("bp-configselect")).click();
        driver.findElement(By.xpath("//select[@id='bp-configselect']/option[@value='0']")).click();

        //Check if values were loaded
        Assert.assertEquals("TelegramAuthToken is wrong!", "", driver.findElement(By.id("telegramAuthToken")).getAttribute("value"));
    }

    /**
     * This test checks, if invalid input is recognized.
     */
    @Test
    public void invalidInputForConfigTest() {
        jse.executeScript("clickConfig()"); //Click config and wait until its open
        Wait<WebDriver> wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modal-config")));

        //Click save
        driver.findElement(By.id("modal-config-save")).click();

        //Search for modal, check if 'show' class is still there
        WebElement modal = driver.findElement(By.id("modal-config"));
        Assert.assertEquals("Config modal was not closed, edit failed!", "modal fade show", modal.getAttribute("class"));
    }

    /**
     * This test checks, if the alert prompt (when deleting the last config) is shown.
     */
    @Test
    public void deleteLastConfigTest() {
        jse.executeScript("clickConfig()"); //Click config and wait until its open
        Wait<WebDriver> wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modal-config")));

        //Click delete
        driver.findElement(By.id("modal-config-delete")).click();

        WebElement alert = driver.findElement(By.id("noConfig"));
        Assert.assertEquals("Alert prompt did not open!", "display: block;", alert.getAttribute("style"));
    }

    @After
    public void tearDown() {
        driver.quit();
    }

}
