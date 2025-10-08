package com.example.tests;

import com.example.core.Config;
import com.example.core.DriverFactory;
import com.example.core.Env;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseSpec {
    protected Config cfg;
    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void openBrowser(){
        cfg = Env.load();
        driver = DriverFactory.start(cfg);
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser(){
        DriverFactory.stop();
    }
}
