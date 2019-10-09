package com.github.pierre_ernst.github_contrib_stats;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Answers the question: <i>What are the programming languages we want to track?</i>
 */
public class SupportedLanguages extends ArrayList<String> {

    private static final String CONFIG_FILE_NAME = "supported-languages.json";
    private static SupportedLanguages SINGLETON = null;

    private SupportedLanguages() throws IOException {
        String[] values = new ObjectMapper().readValue(getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME), String[].class);
        addAll(Arrays.asList(values));
    }

    public static SupportedLanguages getInstance() throws IOException {
        if (SINGLETON == null) {
            SINGLETON = new SupportedLanguages();
        }
        return SINGLETON;
    }
}
