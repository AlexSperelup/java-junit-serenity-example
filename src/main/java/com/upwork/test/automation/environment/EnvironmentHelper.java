package com.upwork.test.automation.environment;

public class EnvironmentHelper {

    public enum SearchEngine {
        DUCK_SEARCH,
        SWISSCOWS_SEARCH

        //TODO: to be added if such
    }

    public static String getUiUrl(SearchEngine userInterface) {
        return System.getProperty(userInterface.name());
    }
}
