package com.upwork.test.automation.browser;

import lombok.SneakyThrows;
import net.thucydides.core.webdriver.DriverSource;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.internal.ElementScrollBehavior;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
/*
Customize WebDriver to start in local Chrome, Firefox, remote on Selenoid
*/
public class CustomDriver implements DriverSource {

    public enum Browser {
        FIREFOX,
        CHROME
    }

    private static final String BROWSER_PROPERTY_NAME = "browser";
    private static final String SELENOID_HUB_URL_PROPERTY_NAME = "selenoid.hub.url";
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @SneakyThrows
    @Override
    public WebDriver newDriver() {

        WebDriver driver = createDriverInstance();

        Capabilities capabilities = ((RemoteWebDriver) driver).getCapabilities();

        logger.info("Browser: {}, version: {}, platform: {}",
                capabilities.getBrowserName(), capabilities.getVersion(), capabilities.getPlatform());

        Dimension dimension = driver.manage().window().getSize();
        logger.info("Browser's window size: width - {}, height: - {}", dimension.getWidth(), dimension.getHeight());

        return driver;
    }

    @Override
    public boolean takesScreenshots() {
        return true;
    }

    @SneakyThrows
    private WebDriver createDriverInstance() {

        Browser browser = getBrowser();

        Optional<URL> selenoidHubURL = getSelenoidHubUrl();

        //if we need to use Selenoid hub
        if (selenoidHubURL.isPresent()) {
            for (int i = 0; i < 3; i++) {
                try {
                    return getRemoteWebDriverInstance(selenoidHubURL.get(), browser);
                } catch (Exception e) {
                    logger.info("Driver init exception");
                    e.printStackTrace();
                    Thread.sleep(5000);
                }
            }
        }

        if (browser == Browser.FIREFOX) {
            return getFirefoxDriver();
        }

        //for now we have just 2 options  - Firefox or Chrome,
        //implementation of getBrowser() returns Firefox if it is not able to parse value from system setting
        return getChromeDriver();
    }

    private WebDriver getRemoteWebDriverInstance(URL url, Browser browser) {
        logger.info("Selenoid driver starting initialisation");

        DesiredCapabilities capability;

        if (browser == Browser.FIREFOX) {
            capability = DesiredCapabilities.firefox();
            capability.setCapability(CapabilityType.ELEMENT_SCROLL_BEHAVIOR, ElementScrollBehavior.BOTTOM);
            capability.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
        } else {

            /*ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--use-fake-ui-for-media-stream","--use-fake-device-for-media-stream");
            capability = DesiredCapabilities.chrome();
            capability.setCapability(ChromeOptions.CAPABILITY, chromeOptions);*/

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            capability = DesiredCapabilities.chrome();
            capability.setVersion("90.0");
            //capability.setCapability("enableVNC", true);
            //capability.setCapability("enableVideo", true);
            capability.setCapability(ChromeOptions.CAPABILITY, options);
            capability.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
            logger.info("Add all capabilities");
        }

        RemoteWebDriver remoteWebDriver = new RemoteWebDriver(url, capability);
        remoteWebDriver.setFileDetector(new LocalFileDetector());
        remoteWebDriver.manage().window().maximize();

        logger.info("Driver created");

        return remoteWebDriver;
    }

    private Browser getBrowser() {
        String browserPropertyValue = System.getProperty(BROWSER_PROPERTY_NAME);

        Optional<Browser> browserOptional = Arrays.asList(Browser.values()).stream().
                filter(b -> b.name().equalsIgnoreCase(browserPropertyValue)).
                findFirst();

        return browserOptional.orElse(Browser.FIREFOX);
    }

    private WebDriver getFirefoxDriver() {
        logger.info("Local Firefox driver starting initialisation");

        DesiredCapabilities desiredCapabilities = DesiredCapabilities.firefox();
        desiredCapabilities.setCapability(CapabilityType.ELEMENT_SCROLL_BEHAVIOR, ElementScrollBehavior.BOTTOM);
        desiredCapabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);

        FirefoxOptions firefoxOptions = new FirefoxOptions().merge(desiredCapabilities);

        WebDriver firefoxDriver = new FirefoxDriver(firefoxOptions);
        firefoxDriver.manage().window().maximize();

        return firefoxDriver;
    }

    private WebDriver getChromeDriver() {
        logger.info("Local Chrome driver starting initialisation");

        ChromeOptions options = new ChromeOptions();

        Set<String> chromeArgs = Stream.of(System.getProperty("chrome.args", "").split(",")).
                filter(arg -> !StringUtils.isEmpty(arg)).
                collect(Collectors.toSet());
        chromeArgs.add("--no-sandbox");
        chromeArgs.add("--use-fake-ui-for-media-stream");
        chromeArgs.add("--use-fake-device-for-media-stream");
        chromeArgs.forEach(options::addArguments);

//        options.addArguments("--no-sandbox");
//        options.addArguments("--disable-gpu");
//        options.addArguments("--disable-browser-side-navigation");
//        options.setPageLoadStrategy(PageLoadStrategy.NONE);

        String chromeMaximizeArgument = getChromeMaximizeArgument();

        options.addArguments(chromeMaximizeArgument);
        options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);

        return new ChromeDriver(options);
    }

    private Optional<URL> getSelenoidHubUrl() {

        String hubURL = System.getProperty(SELENOID_HUB_URL_PROPERTY_NAME);

        Optional<URL> hubUrlOptional = Optional.empty();

        if (hubURL != null && !hubURL.isEmpty()) {
            try {
                hubUrlOptional = Optional.of(new URL(hubURL));
            } catch (MalformedURLException e) {
                logger.error("Can't parse value of {} property", SELENOID_HUB_URL_PROPERTY_NAME, e);
            }
        }

        return hubUrlOptional;
    }

    private String getChromeMaximizeArgument() {
        if (IS_OS_MAC) {
            return "--start-fullscreen";
        }

        if (IS_OS_LINUX) {
            return "--kiosk";
        }

        return "--start-maximized";
    }
}
