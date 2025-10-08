package com.example.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> HOLDER = new ThreadLocal<>();

    public static WebDriver start(Config cfg) {
        if (HOLDER.get() == null) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            if (cfg.isHeadless()) options.addArguments("--headless=new");
            options.addArguments("--window-size=1440,900");
            ChromeDriver driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(cfg.getImplicitSeconds()));
            HOLDER.set(driver);
        }
        return HOLDER.get();
    }

    public static WebDriver get() {
        if (HOLDER.get() == null) throw new IllegalStateException("Driver not started");
        return HOLDER.get();
    }

    public static void stop() {
        WebDriver d = HOLDER.get();
        if (d != null) {
            d.quit();
            HOLDER.remove();
        }
    }
}
