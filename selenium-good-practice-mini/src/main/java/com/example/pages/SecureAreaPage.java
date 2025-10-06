package com.example.pages;

import com.example.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SecureAreaPage extends BasePage {

    private final By flash = By.id("flash");

    public SecureAreaPage(WebDriver driver, int explicitSeconds) {
        super(driver, explicitSeconds);
    }

    public boolean isSuccessBannerVisible() {
        return waitVisible(flash).isDisplayed();
    }

    public String bannerText() {
        return waitVisible(flash).getText();
    }
}
