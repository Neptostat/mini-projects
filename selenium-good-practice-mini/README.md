# Selenium POM Starter (Good vs Bad Practices)

A small, production-style Selenium + TestNG project that showcases:
- ✅ Good patterns: DriverFactory, Page Object Model, explicit waits, config, logging
- ❌ Bad patterns: hard-coded sleeps, brittle locators, inline driver usage

## Prereqs
- Java 17+
- Maven 3.9+
- Chrome installed

## Quick Start
```bash
mvn -q -e -Dbase.url=https://the-internet.herokuapp.com -Dbrowser=chrome clean test
```
If you omit system properties, defaults from `src/test/resources/config.properties` are used.

## Project Layout
```
src/
  main/java/com/example/core
    BasePage.java         # common utils and waits
    DriverFactory.java    # singleton driver management + WebDriverManager
    Config.java           # config POJO
    ConfigLoader.java     # loads from system props or properties file
  main/java/com/example/pages
    LoginPage.java
    SecureAreaPage.java
  test/java/com/example/tests
    BaseTest.java
    LoginTest.java        # good practice example
    bad/BadPracticeTest.java   # intentionally bad anti-patterns (disabled by default)
testng.xml
```

## Toggle Bad Tests
Bad tests are excluded by default via TestNG groups. To run them:
```bash
mvn -Dgroups=bad -Dbase.url=https://the-internet.herokuapp.com clean test
```

## Notes
- Driver options (headless, window size) can be customized via `DriverFactory` and system properties.
- This project uses [WebDriverManager] to resolve the correct ChromeDriver.
