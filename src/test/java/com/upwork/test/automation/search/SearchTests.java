package com.upwork.test.automation.search;

import com.upwork.test.automation.BaseTest;
import com.upwork.test.automation.models.web.SearchResultWeb;
import com.upwork.test.automation.steps.search.ResultsSteps;
import com.upwork.test.automation.steps.search.SearchSteps;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.upwork.test.automation.environment.EnvironmentHelper.SearchEngine.*;

@RunWith(SerenityRunner.class)
public class SearchTests extends BaseTest {

    @Steps
    private SearchSteps searchSteps;

    @Steps
    private ResultsSteps resultsSteps;

    private static final String keyword = "Test";

    @Test
    public void verifyThatEachSearchResultFromDuckDuckGoContainsSearchKeyword() {

        searchSteps.openSearchEngine(DUCK_SEARCH);
        searchSteps.searchInEngineByKeyword(DUCK_SEARCH, keyword);
        List<SearchResultWeb> duckResults = resultsSteps.getSearchResultsFromEngine(DUCK_SEARCH);

        resultsSteps.verifyThatEachResultContainsKeyword(duckResults, keyword);
    }

    @Test
    public void verifyThatEachSearchResultFromSwisscowsContainsSearchKeyword() {

        searchSteps.openSearchEngine(SWISSCOWS_SEARCH);
        searchSteps.searchInEngineByKeyword(SWISSCOWS_SEARCH, keyword);
        List<SearchResultWeb> swisscowsResults = resultsSteps.getSearchResultsFromEngine(SWISSCOWS_SEARCH);

        resultsSteps.verifyThatEachResultContainsKeyword(swisscowsResults, keyword);
    }

    @Test
    public void verifyMostPopularResourcesFromBothEngines() {
        searchSteps.openSearchEngine(DUCK_SEARCH);
        searchSteps.searchInEngineByKeyword(DUCK_SEARCH, keyword);
        List<SearchResultWeb> duckResults = resultsSteps.getSearchResultsFromEngine(DUCK_SEARCH);

        searchSteps.openSearchEngine(SWISSCOWS_SEARCH);
        searchSteps.searchInEngineByKeyword(SWISSCOWS_SEARCH, keyword);
        List<SearchResultWeb> swisscowsResults = resultsSteps.getSearchResultsFromEngine(SWISSCOWS_SEARCH);

        List<String> mostPopularResources = resultsSteps.findMostPopularResources(duckResults, swisscowsResults);
        if (!mostPopularResources.isEmpty()) {
            mostPopularResources.forEach(resource -> logger.info("URL of resource " + resource + " belongs to most popular"));
        }
    }
}
