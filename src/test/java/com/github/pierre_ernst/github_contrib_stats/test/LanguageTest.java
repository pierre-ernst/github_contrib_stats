package com.github.pierre_ernst.github_contrib_stats.test;

import com.github.pierre_ernst.github_contrib_stats.SupportedLanguages;
import org.junit.Test;

import static org.junit.Assert.*;

public class LanguageTest {

    @Test
    public void matchTest() {
        try {
            // ["a", "B", "c", "d" ]
            assertTrue(SupportedLanguages.getInstance().contains("a"));
            assertTrue(SupportedLanguages.getInstance().contains("B"));
            assertFalse(SupportedLanguages.getInstance().contains("C"));
            assertTrue(SupportedLanguages.getInstance().contains("d"));
            assertFalse(SupportedLanguages.getInstance().contains("Pierre Ernst"));
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            fail(ex.getMessage());
        }
    }
}
