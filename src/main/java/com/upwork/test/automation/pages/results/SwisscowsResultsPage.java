package com.upwork.test.automation.pages.results;

import com.upwork.test.automation.core.BasePage;
import com.upwork.test.automation.models.web.SearchResultWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.xpath;

public class SwisscowsResultsPage extends BasePage {

    private static final By resultsContainer = xpath("//div[@class='web-results']");

    public SwisscowsResultsPage(WebDriver driver) {
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

        resultWeb.setUrl(searchResult.findElements(By.tagName("a")).get(0).getAttribute("href"));
        resultWeb.setTitle(searchResult.findElement(By.tagName("h2")).getAttribute("title"));
        resultWeb.setDescription(searchResult.findElement(By.tagName("p")).getAttribute("title"));

        return resultWeb;
    }
}
