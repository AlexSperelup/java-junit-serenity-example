package com.upwork.test.automation.steps.search;

import com.upwork.test.automation.pages.search.DuckSearchPage;
import com.upwork.test.automation.pages.search.SwisscowsSearchPage;
import com.upwork.test.automation.steps.BaseSteps;
import net.thucydides.core.annotations.Step;

import static com.upwork.test.automation.environment.EnvironmentHelper.*;
import static com.upwork.test.automation.environment.EnvironmentHelper.SearchEngine.*;

public class SearchSteps extends BaseSteps {

    private DuckSearchPage duckSearchPage;
    private SwisscowsSearchPage swisscowsSearchPage;

    @Step("Open search engine")
    public void openSearchEngine(SearchEngine engine) {
        if (engine == DUCK_SEARCH) {
            duckSearchPage.openSearchPage();
        } else {
            swisscowsSearchPage.openSearchPage();
        }
    }

    @Step("Search in engine by keyword")
    public void searchInEngineByKeyword(SearchEngine engine, String keyword) {
        if (engine == DUCK_SEARCH) {
            duckSearchPage.enterKeyword(keyword);
            duckSearchPage.clickSearch();
        } else {
            swisscowsSearchPage.enterKeyword(keyword);
            swisscowsSearchPage.clickSearch();
        }
    }
}
