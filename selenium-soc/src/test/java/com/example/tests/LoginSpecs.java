package com.example.tests;

import com.example.assertions.FluentAssert;
import com.example.data.Credentials;
import com.example.flows.LoginFlow;
import org.testng.annotations.Test;

public class LoginSpecs extends BaseSpec {

    @Test
    public void successful_login_shows_banner_and_header(){
        var creds = Credentials.builder().validTheInternetUser().build();
        var result = new LoginFlow(driver, cfg).loginWith(creds.username(), creds.password());

        FluentAssert.that(result.bannerVisible, "Banner should be visible after login");
        FluentAssert.contains(result.bannerText, "You logged into a secure area!", "Banner text should confirm success");
        FluentAssert.contains(result.headerText, "Secure Area", "Header should indicate Secure Area");
    }
}
