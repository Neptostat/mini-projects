package com.example.flows;

import com.example.core.Config;
import com.example.core.Waits;
import com.example.pages.LoginPage;
import com.example.pages.SecureAreaPage;
import com.example.pages.components.FlashBanner;
import org.openqa.selenium.WebDriver;

public class LoginFlow {

    private final WebDriver driver;
    private final Config cfg;
    private final Waits waits;

    public LoginFlow(WebDriver driver, Config cfg) {
        this.driver = driver;
        this.cfg = cfg;
        this.waits = new Waits(driver, cfg.getExplicitSeconds());
    }

    public Result loginWith(String user, String pass) {
        LoginPage login = new LoginPage(driver, waits).open(cfg.getBaseUrl());
        login.typeUsername(user);
        login.typePassword(pass);
        login.submit();

        FlashBanner banner = new FlashBanner(driver, waits);
        boolean successBanner = banner.visible();

        SecureAreaPage secure = new SecureAreaPage(driver, waits);
        String header = secure.headerText();

        return new Result(successBanner, banner.text(), header);
    }

    public static class Result {
        public final boolean bannerVisible;
        public final String bannerText;
        public final String headerText;

        public Result(boolean bannerVisible, String bannerText, String headerText) {
            this.bannerVisible = bannerVisible;
            this.bannerText = bannerText;
            this.headerText = headerText;
        }
    }
}
