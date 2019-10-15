package guitestutils;

import org.openqa.selenium.firefox.FirefoxOptions;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.io.File;

public class ExposedBrowserWebDriverContainerFactory {

    private ExposedBrowserWebDriverContainerFactory() {
        throw new IllegalAccessError();
    }

    public static BrowserWebDriverContainer create() {
        Testcontainers.exposeHostPorts(8080);
        return (BrowserWebDriverContainer) new BrowserWebDriverContainer()
                .withCapabilities(new FirefoxOptions())
                .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.RECORD_FAILING, new File("target"))
                .withExposedPorts(8080);
    }
}
