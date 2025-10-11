package com.example.pages;

import com.example.core.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Base Page class providing common behavior for all pages.
 * Implements Separation of Concerns:
 *   - WebDriver instance is passed from test setup
 *   - Waits utility handles synchronization logic
 *   - Derived page classes focus only on business interactions
 */
public abstract class Page {

    protected final WebDriver driver;
    protected final Waits waits;

    // Constructor injection: driver + waits utility
    public Page(WebDriver driver, Waits waits) {
        this.driver = driver;
        this.waits = waits;
    }

    /**
     * Common element finder — ensures visibility before returning element.
     */
    protected WebElement el(By locator) {
        return waits.visible(locator);
    }

    /**
     * Returns the current page title.
     */
    public String title() {
        return driver.getTitle();
    }

    /**
     * Helper: check if element exists and visible on page.
     */
    public boolean isVisible(By locator) {
        try {
            return waits.visible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}

