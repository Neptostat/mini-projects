package com.example.pages.components;

import com.example.core.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class Component {
    protected final WebDriver driver;
    protected final Waits waits;

    protected Component(WebDriver driver, Waits waits) {
        this.driver = driver;
        this.waits = waits;
    }

    protected WebElement el(By locator) {
        return waits.visible(locator);
    }
}
