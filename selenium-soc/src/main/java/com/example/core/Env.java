package com.example.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Env {
    public static Config load() {
        String env = System.getProperty("env", "local");
        Properties p = new Properties();
        try (FileInputStream fis = new FileInputStream("src/test/resources/env/" + env + ".properties")) {
            p.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load env properties for: " + env, e);
        }
        String base = p.getProperty("base.url");
        String browser = System.getProperty("browser", p.getProperty("browser", "chrome"));
        int imp = Integer.parseInt(p.getProperty("implicit.seconds", "0"));
        int exp = Integer.parseInt(p.getProperty("explicit.seconds", "10"));
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", p.getProperty("headless", "false")));
        return new Config(base, browser, imp, exp, headless);
    }
}
