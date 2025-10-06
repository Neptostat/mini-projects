package com.example.tests.bad;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;

// NOTE: This class intentionally demonstrates bad practices.
// It is included in 'bad' group and excluded by default in testng.xml.

public class BadPracticeTest {

    @Test(groups = {"bad"})
    public void brittleTestWithSleepsAndInlineDriver() throws InterruptedException {
        // ❌ Hard-coded driver and no WebDriverManager
        WebDriver driver = new ChromeDriver();
        driver.get("https://the-internet.herokuapp.com/login");

        // ❌ Thread.sleep instead of explicit waits
        Thread.sleep(3000);

        // ❌ Brittle locator (overly specific XPath)
        driver.findElement(By.xpath("//form[@id='login']/div[1]/div/input")).sendKeys("tomsmith");
        driver.findElement(By.xpath("//form[@id='login']/div[2]/div/input")).sendKeys("SuperSecretPassword!");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Thread.sleep(2000);
        boolean displayed = driver.findElement(By.id("flash")).isDisplayed();
        Assert.assertTrue(displayed, "Expected banner");

        // ❌ No teardown
        driver.quit();
    }
}
