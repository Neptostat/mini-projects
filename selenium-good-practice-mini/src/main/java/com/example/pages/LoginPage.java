package com.example.pages;

import com.example.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private final By username = By.id("username");
    private final By password = By.id("password");
    private final By loginBtn = By.cssSelector("button[type='submit']");

    public LoginPage(WebDriver driver, int explicitSeconds) {
        super(driver, explicitSeconds);
    }

    public LoginPage open(String baseUrl) {
        driver.get(baseUrl + "/login");
        return this;
    }

    public SecureAreaPage login(String user, String pass) {
        type(username, user);
        type(password, pass);
        click(loginBtn);
        return new SecureAreaPage(driver, (int) wait.getTimeout().getSeconds());
    }
}
