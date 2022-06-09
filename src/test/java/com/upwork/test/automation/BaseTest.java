package com.upwork.test.automation;

import com.upwork.test.automation.environment.Environment;
import net.thucydides.core.annotations.Managed;
import org.junit.After;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTest {

    @Managed
    protected WebDriver driver;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    //Set environment
    private static Environment environment = Environment.of(System.getProperty("env"), Environment.EnvironmentName.QA);

    protected static void init() {

        //Chrome v101 need to be installed on your machine
        System.setProperty("browser", "chrome");
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");

        environment.setProperties();
    }

    @BeforeClass
    public static void beforeClass() {
        init();
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}