package com.example.pages;

import com.example.core.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SecureAreaPage extends Page {
    private final By header = By.cssSelector("div.example h2");

    public SecureAreaPage(WebDriver driver, Waits waits) {
        super(driver, waits);
    }

    public String headerText() {
        return el(header).getText();
    }
}
