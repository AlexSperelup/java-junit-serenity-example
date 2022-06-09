package com.upwork.test.automation.pages.results;

import com.upwork.test.automation.core.BasePage;
import com.upwork.test.automation.models.web.SearchResultWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static org.openqa.selenium.By.*;

public class DuckResultsPage extends BasePage {

    private static final By resultsContainer = id("links");

    public DuckResultsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By getUniqueElementLocator() {
        return resultsContainer;
    }

    //Get all search results as DTO
    public List<SearchResultWeb> getSearchResults() {

        return element(resultsContainer)
                .findElements(By.tagName("article"))
                .stream()
                .map(this::parseSearchResult)
                .collect(Collectors.toList());
    }

    //Parse data from article element on search page
    private SearchResultWeb parseSearchResult(WebElement searchResult) {
        SearchResultWeb resultWeb = new SearchResultWeb();

        resultWeb.setUrl(searchResult.findElements(By.tagName("div")).get(0).getText());
        resultWeb.setTitle(searchResult.findElements(By.tagName("div")).get(1).getText());
        resultWeb.setDescription(searchResult.findElements(By.tagName("div")).get(2).getText());

        return resultWeb;
    }
}
