package com.example.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    public static Config load() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("WARN: Could not load config.properties, using defaults and system properties.");
        }

        String baseUrl = System.getProperty("base.url", props.getProperty("base.url", "https://example.com"));
        String browser = System.getProperty("browser", props.getProperty("browser", "chrome"));
        int implicit = Integer.parseInt(System.getProperty("implicit.seconds", props.getProperty("implicit.seconds", "0")));
        int explicit = Integer.parseInt(System.getProperty("explicit.seconds", props.getProperty("explicit.seconds", "10")));
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", props.getProperty("headless", "false")));

        return new Config(baseUrl, browser, implicit, explicit, headless);
    }
}
