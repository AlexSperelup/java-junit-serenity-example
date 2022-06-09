package com.upwork.test.automation.steps.search;

import com.upwork.test.automation.models.web.SearchResultWeb;
import com.upwork.test.automation.pages.results.DuckResultsPage;
import com.upwork.test.automation.pages.results.SwisscowsResultsPage;
import com.upwork.test.automation.steps.BaseSteps;
import net.thucydides.core.annotations.Step;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.upwork.test.automation.environment.EnvironmentHelper.*;
import static com.upwork.test.automation.environment.EnvironmentHelper.SearchEngine.DUCK_SEARCH;

public class ResultsSteps extends BaseSteps {

    private DuckResultsPage duckResultsPage;
    private SwisscowsResultsPage swisscowsResultsPage;

    @Step("Get search results from engine")
    public List<SearchResultWeb> getSearchResultsFromEngine(SearchEngine engine) {
        if (engine == DUCK_SEARCH) {
            return duckResultsPage.getSearchResults();
        } else {
            return swisscowsResultsPage.getSearchResults();
        }
    }

    //Compare one by one elements from both lists
    @Step("Find most popular resources")
    public List<String> findMostPopularResources(List<SearchResultWeb> duckResults, List<SearchResultWeb> swisscowsResults) {
        List<String> popularResourcesUrls = new ArrayList<>();

        duckResults.forEach(result -> {
            if (swisscowsResults
                    .stream()
                    .map(SearchResultWeb::getUrl)
                    .collect(Collectors.toList())
                    .contains(result.getUrl())) {
                popularResourcesUrls.add(result.getUrl());
            }
        });

        return popularResourcesUrls;
    }

    //Check that either url, title, description contains keyword
    @Step("Verify that each result contains keyword")
    public void verifyThatEachResultContainsKeyword(List<SearchResultWeb> resultWebs, String keyword) {

        resultWebs.forEach(resultWeb -> {
            boolean isResultContainsKeyword = resultWeb.getUrl().toLowerCase().contains(keyword.toLowerCase())
                    || resultWeb.getTitle().toLowerCase().contains(keyword.toLowerCase())
                    || resultWeb.getDescription().toLowerCase().contains(keyword.toLowerCase());
            Assert.assertTrue("Result with URL " + resultWeb.getUrl() + " not contains given keyword",
                    isResultContainsKeyword);
        });
    }
}
