package com.upwork.test.automation.environment;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
/*
Environments might be specified as much as we want
*/
public class Environment {

    public enum EnvironmentName {
        QA,
        PROD
    }

    @SerializedName("name")
    public EnvironmentName name;

    @SerializedName("properties")
    public Map<String, String> properties;

    private static transient List<Environment> environmentList = new ArrayList<>();
    private final transient Logger logger = LoggerFactory.getLogger(Environment.class);

    public EnvironmentName getName() {
        return this.name;
    }


    public static Environment of(EnvironmentName name) {
        if (name == null) {
            throw new IllegalArgumentException("Name of the environment should be provided");
        }

        Optional<Environment> environment = getAllEnvironments().stream().
                filter(e -> name.equals(e.name)).
                findFirst();

        if (!environment.isPresent()) {
            throw new IllegalArgumentException("Environment with name '" + name + "' is not described in the file");
        }

        return environment.get();
    }

    public static Environment of(String environmentName, EnvironmentName defaultEnvName) {
        EnvironmentName name;

        //if environmentName as string is not provided, we have to use defaultEnvName
        if (environmentName == null || environmentName.trim().isEmpty()) {
            name = defaultEnvName;
        }
        else {
            try {
                name = EnvironmentName.valueOf(environmentName.trim().toUpperCase());
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Environment with '" + environmentName +
                        " doesn't exist. Supported: " + EnvironmentName.values());
            }
        }

        return of(name);
    }

    public void setProperties() {

        List<String> output = new ArrayList<>();
        output.add("");
        output.add("------------------------------------------------------------------------------------");
        output.add("------------------------------- ENVIRONMENT SETTINGS -------------------------------");

        for (Map.Entry<String, String> entry : properties.entrySet()) {

            String propertyValue = System.getProperty(entry.getKey());

            if (propertyValue != null) {
                output.add(String.format("%s already set to '%s'", entry.getKey(), propertyValue));
                continue;
            }

            System.setProperty(entry.getKey(), entry.getValue());
            output.add(String.format("%s set to '%s'", entry.getKey(), entry.getValue()));
        }

        output.add("------------------------------------------------------------------------------------");

        logger.info(output.stream().collect(Collectors.joining(System.lineSeparator())));
    }

    private static List<Environment> getAllEnvironments() {

        if (!environmentList.isEmpty()) {
            return environmentList;
        }

        String filePath = "environments.json";

        InputStream inputStream = Environment.class.getClassLoader().getResourceAsStream(filePath);

        if (inputStream == null) {
            throw new RuntimeException("File " + filePath + " doesn't exist");
        }

        try {
            String json = IOUtils.toString(inputStream, "UTF-8");

            environmentList = new Gson().fromJson(json, new TypeToken<List<Environment>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return environmentList;
    }
}