package com.example.pages.components;

import com.example.core.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FlashBanner extends Component {
    private final By root = By.id("flash");

    public FlashBanner(WebDriver driver, Waits waits) {
        super(driver, waits);
    }

    public boolean visible() {
        return el(root).isDisplayed();
    }

    public String text() {
        return el(root).getText();
    }
}
