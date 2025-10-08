package com.example.core;

public class Config {
    private final String baseUrl;
    private final String browser;
    private final int implicitSeconds;
    private final int explicitSeconds;
    private final boolean headless;

    public Config(String baseUrl, String browser, int implicitSeconds, int explicitSeconds, boolean headless) {
        this.baseUrl = baseUrl;
        this.browser = browser;
        this.implicitSeconds = implicitSeconds;
        this.explicitSeconds = explicitSeconds;
        this.headless = headless;
    }

    public String getBaseUrl() { return baseUrl; }
    public String getBrowser() { return browser; }
    public int getImplicitSeconds() { return implicitSeconds; }
    public int getExplicitSeconds() { return explicitSeconds; }
    public boolean isHeadless() { return headless; }
}
