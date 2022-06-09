package com.upwork.test.automation.pages.search;

import com.upwork.test.automation.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.upwork.test.automation.environment.EnvironmentHelper.SearchEngine.*;
import static com.upwork.test.automation.environment.EnvironmentHelper.getUiUrl;

public class DuckSearchPage extends BasePage {

    private static final By searchInput = By.id("search_form_input_homepage");
    private static final By searchButton = By.id("search_button_homepage");

    public DuckSearchPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By getUniqueElementLocator() {
        return searchInput;
    }

    public void openSearchPage() {
        this.get(getUiUrl(DUCK_SEARCH));
    }

    public void enterKeyword(String keyword) {
        element(searchInput).clear();
        element(searchInput).sendKeys(keyword);
    }

    public void clickSearch() {
        element(searchButton).click();
    }
}
