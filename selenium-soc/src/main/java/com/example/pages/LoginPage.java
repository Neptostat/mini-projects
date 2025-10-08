package com.example.pages;

import com.example.core.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class LoginPage extends Page {
    private final By username = By.id("username");
    private final By password = By.id("password");
    private final By submit = By.cssSelector("button[type='submit']");

    public LoginPage(WebDriver driver, Waits waits) {
        super(driver, waits);
    }

    public LoginPage open(String baseUrl) {
        driver.get(baseUrl + "/login");
        return this;
    }

    public void typeUsername(String user) { el(username).clear(); el(username).sendKeys(user); }
    public void typePassword(String pass) { el(password).clear(); el(password).sendKeys(pass); }
    public void submit() { waits.clickable(submit); driver.findElement(submit).click(); }
}
