package com.example.core;

import java.time.Duration;
import java.util.Objects;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

/**
 * Lightweight explicit-waits helper.
 * - No TestNG/JUnit annotations here.
 * - No driver creation here.
 * Construct this in your test setup and inject into pages.
 *
 * Example:
 *   WebDriver driver = new ChromeDriver();
 *   Waits waits = new Waits(driver, 30);
 *   new LoginPage(driver, waits)....
 */
public class Waits {

    private final WebDriver driver;
    private final Wait<WebDriver> wait;

    /**
     * @param driver           an existing WebDriver instance
     * @param timeoutSeconds   max wait time in seconds
     */
    public Waits(WebDriver driver, long timeoutSeconds) {
        this(driver, timeoutSeconds, 500);
    }

    /**
     * @param driver           an existing WebDriver instance
     * @param timeoutSeconds   max wait time in seconds
     * @param pollingMillis    polling interval in milliseconds
     */
    public Waits(WebDriver driver, long timeoutSeconds, long pollingMillis) {
        this.driver = Objects.requireNonNull(driver, "driver");
        this.wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(pollingMillis))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    /** Waits until an element located by the locator is visible and returns it. */
    public WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Waits until the given element is visible and returns it. */
    public WebElement visible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /** Waits until an element is present in the DOM (not necessarily visible). */
    public WebElement present(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /** Waits until an element is clickable and returns it. */
    public WebElement clickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /** Returns true when the element located by the locator becomes invisible or gone. */
    public boolean invisible(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /** Waits for the page title to contain the given text. */
    public boolean titleContains(String text) {
        return wait.until(ExpectedConditions.titleContains(text));
    }

    /** Utility: safely click after element is clickable. */
    public void safeClick(By locator) {
        clickable(locator).click();
    }

    /** Utility: send keys after element is visible (clears first). */
    public void type(By locator, CharSequence... keys) {
        WebElement e = visible(locator);
        e.clear();
        e.sendKeys(keys);
    }
}
