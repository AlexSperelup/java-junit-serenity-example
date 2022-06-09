package com.upwork.test.automation.core;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacadeImpl;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static org.junit.Assert.fail;


public abstract class BasePage extends PageObject {

    public static final int COMPONENT_WAIT_TIMEOUT = 30;
    public static final int PAUSE_BETWEEN_CHECKS = 1;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public BasePage(WebDriver driver) {
        super(driver);
    }

    //To make sure that desired page is opened
    protected abstract By getUniqueElementLocator();

    public void isLoaded() throws Error {

        String failMessage = String.format("Unique element - '%s' is not displayed for '%s'",
                getUniqueElementLocator().toString(), getComponentName());
        waitActiveAjaxRequests();
        try {
            if (getDriver()
                    .findElements(getUniqueElementLocator())
                    .stream()
                    .noneMatch(WebElement::isDisplayed)) {
                fail(failMessage);
            }
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            fail(failMessage);
        }
    }

    //Wrapper on element
    public WebElementFacadeImpl element(By bySelector) {
        waitForLoad();
        for (int i = 0; i <= 5; i++) {
            try {
                return super.element(bySelector);
            } catch (StaleElementReferenceException e) {
            }
        }
        return super.element(bySelector);
    }

    public void waitForLoad() {
        try {
            new WebDriverWait(getDriver(), COMPONENT_WAIT_TIMEOUT).until((ExpectedCondition<Boolean>) webDriver -> {
                try {
                    isLoaded();
                    logger.debug("{} is loaded", getComponentName());
                    return true;
                } catch (Error | Exception e) {
                    logger.debug("{} is not loaded: {} - {}", getComponentName(), e.getClass(), e.getMessage());
                    Stream.of(e.getStackTrace()).forEach(el -> logger.debug(el.toString()));
                    return false;
                }
            });
        } catch (TimeoutException e) {
            fail(String.format("Can't wait till '%s' is loaded", getComponentName()));
        }
    }

    protected long getActiveAjaxRequests() {
        try {
            Long activeRequests = (Long) getJavascriptExecutorFacade().executeScript("return $.active");

            if (activeRequests == null) {
                return 0;
            }

            logger.debug("Active ajax requests {}", activeRequests);

            return activeRequests;
        } catch (WebDriverException e) {
            return 0;
        }
    }

    //Wait until all active Ajax requests are finished
    protected void waitActiveAjaxRequests() {
        try {
            waitForCondition().until((ExpectedCondition<Boolean>) webDriver -> getActiveAjaxRequests() > 0);
        } catch (TimeoutException ignore) {
        }
    }

    protected String getComponentName() {
        return this.getClass().getSimpleName();
    }

    public boolean isDisplayed(By locator) {
        try {
            return element(locator).isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            logger.error("Element " + element(locator).toString() + " is not displayed");
            return false;
        }
    }

    public void get(String url) {
        try {
            isLoaded();
        } catch (Error e) {
            getDriver().manage().deleteAllCookies();
            this.openUrl(url);
            logger.info("Page '{}' loaded, URL - {}", getComponentName(), getDriver().getCurrentUrl());
            waitForLoad();
        }
    }
}
