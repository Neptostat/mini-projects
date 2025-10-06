package com.example.tests;

import com.example.core.Config;
import com.example.core.ConfigLoader;
import com.example.core.DriverFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import org.openqa.selenium.WebDriver;

public abstract class BaseTest {
    protected WebDriver driver;
    protected Config config;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        config = ConfigLoader.load();
        driver = DriverFactory.getDriver(config);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
