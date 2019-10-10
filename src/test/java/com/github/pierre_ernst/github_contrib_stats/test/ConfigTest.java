package com.github.pierre_ernst.github_contrib_stats.test;

import com.github.pierre_ernst.github_contrib_stats.ConfigPOJO;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigTest {

    @Test
    public void languageMatchTest() {
        try {
            // ["a", "B", "c", "d" ]
            assertTrue(ConfigPOJO.getInstance().getLanguages().contains("a"));
            assertTrue(ConfigPOJO.getInstance().getLanguages().contains("B"));
            assertFalse(ConfigPOJO.getInstance().getLanguages().contains("C"));
            assertTrue(ConfigPOJO.getInstance().getLanguages().contains("d"));
            assertFalse(ConfigPOJO.getInstance().getLanguages().contains("Pierre Ernst"));
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            fail(ex.getMessage());
        }
    }

    @Test
    public void timeWindowTest() {
        try {
            assertEquals(ConfigPOJO.getInstance().getTimeWindow(), 10);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            fail(ex.getMessage());
        }
    }
}
