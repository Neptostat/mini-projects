package com.example.tests;

import com.example.pages.LoginPage;
import com.example.pages.SecureAreaPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(description = "Valid login should show success banner")
    public void validLoginShowsBanner() {
        LoginPage login = new LoginPage(driver, config.getExplicitSeconds())
                .open(config.getBaseUrl());
        SecureAreaPage secure = login.login("tomsmith", "SuperSecretPassword!");
        Assert.assertTrue(secure.isSuccessBannerVisible(), "Success banner not visible");
        Assert.assertTrue(secure.bannerText().contains("You logged into a secure area!"), "Unexpected banner text");
    }
}
