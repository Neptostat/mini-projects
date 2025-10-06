package com.example.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> THREAD_DRIVER = new ThreadLocal<>();

    public static WebDriver getDriver(Config config) {
        if (THREAD_DRIVER.get() == null) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            if (config.isHeadless()) {
                options.addArguments("--headless=new");
            }
            options.addArguments("--window-size=1440,900");
            WebDriver driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitSeconds()));
            driver.manage().window().maximize();
            THREAD_DRIVER.set(driver);
        }
        return THREAD_DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = THREAD_DRIVER.get();
        if (driver != null) {
            driver.quit();
            THREAD_DRIVER.remove();
        }
    }
}
