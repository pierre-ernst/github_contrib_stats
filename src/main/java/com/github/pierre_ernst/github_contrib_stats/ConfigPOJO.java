package com.github.pierre_ernst.github_contrib_stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Answers the question: <i>What are the programming languages we want to track?</i>
 */
public class ConfigPOJO  {

    private static final String CONFIG_FILE_NAME = "config.json";
    private static ConfigPOJO SINGLETON = null;

    @JsonProperty()
    private int timeWindow;

    @JsonProperty()
    private ArrayList<String> languages;

    private ConfigPOJO() throws IOException {
    }

    public static ConfigPOJO getInstance() throws IOException {
        if (SINGLETON == null) {
             SINGLETON = new ObjectMapper().readValue(ConfigPOJO.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME), ConfigPOJO.class);
        }
        return SINGLETON;
    }

    public int getTimeWindow() {
        return timeWindow;
    }

    public ArrayList<String> getLanguages() {
        return languages;
    }
}
