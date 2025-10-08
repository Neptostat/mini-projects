package com.example.core;
// Maven deps (pom.xml)
// ------------------------------------------------------------
// <dependencies>
//   <dependency>
//     <groupId>org.seleniumhq.selenium</groupId>
//     <artifactId>selenium-java</artifactId>
//     <version>4.23.0</version>
//   </dependency>
//   <dependency>
//     <groupId>io.github.bonigarcia</groupId>
//     <artifactId>webdrivermanager</artifactId>
//     <version>5.9.2</version>
//   </dependency>
//   <dependency>
//     <groupId>org.testng</groupId>
//     <artifactId>testng</artifactId>
//     <version>7.10.2</version>
//     <scope>test</scope>
//   </dependency>
//   <dependency>
//     <groupId>org.assertj</groupId>
//     <artifactId>assertj-core</artifactId>
//     <version>3.25.3</version>
//     <scope>test</scope>
//   </dependency>
//   <!-- Optional: JDBC to compare against warehouse (e.g., Postgres, SQL Server, Snowflake) -->
//   <!-- Add suitable driver and create a small DAO to run SQL and compare counts/metrics. -->
// </dependencies>

package com.example.tableau;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

/**
 * Tableau Dashboard Checks – Java + Selenium + TestNG
 *
 * WHAT THIS DOES
 * 1) Loads a Tableau view (embedded or direct view URL).
 * 2) Validates filters/drill‑downs/parameters using Tableau JS API via executeAsyncScript.
 * 3) (Optional) Asserts KPI text on the page. You can also wire JDBC to compare numbers to SQL.
 *
 * PREREQS
 * - The page must include an embedded Tableau viz OR a direct view URL with JS API available.
 * - For direct view, prefer adding query params: :embed=y&:showVizHome=no to simplify DOM.
 */
public class TableauDashboardTests {

    private WebDriver driver;
    private WebDriverWait wait;

    // <<<<< CHANGE THESE FOR YOUR ENV >>>>>
    private static final String VIEW_URL = System.getProperty("tableau.view",
            "https://your-tableau-server/views/YourWorkbook/YourView?:embed=y&:showVizHome=no");

    // Example worksheet names inside a dashboard (used to fetch data via JS API)
    private static final String KPI_SHEET = System.getProperty("tableau.kpiSheet", "KPI_Summary");
    private static final String MAIN_SHEET = System.getProperty("tableau.mainSheet", "Bars_By_Region");

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--start-maximized");
        driver = new ChromeDriver(opts);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @AfterClass(alwaysRun = true)
    public void teardown() {
        if (driver != null) driver.quit();
    }

    // --------------------
    // TEST 1: Filter logic
    // --------------------
    @Test
    public void filterUpdatesKpi() {
        driver.get(VIEW_URL);
        Tableau.waitForVizReady(driver, wait);

        // Apply Region filter = EMEA
        Tableau.applyFilter(driver, MAIN_SHEET, "Region", "EMEA");

        // Grab KPI sheet total (or a single cell) after filter
        String kpiValue = Tableau.getFirstCellAsString(driver, KPI_SHEET);
        System.out.println("KPI after Region=EMEA => " + kpiValue);

        // Basic assertion example (replace with your expected value or non-empty check)
        Assertions.assertThat(kpiValue).isNotBlank();
    }

    // -----------------------
    // TEST 2: Parameter logic
    // -----------------------
    @Test
    public void parameterChangesContent() {
        // Set a parameter via URL (Tableau supports :parameter.<Name>=Value)
        String url = VIEW_URL + "&:parameter.Portfolio=A";
        driver.get(url);
        Tableau.waitForVizReady(driver, wait);

        // Read a cell that should change when parameter changes
        String afterA = Tableau.getFirstCellAsString(driver, KPI_SHEET);

        // Switch parameter to B using JS API
        Tableau.setParameter(driver, "Portfolio", "B");
        String afterB = Tableau.getFirstCellAsString(driver, KPI_SHEET);

        System.out.printf("KPI A=%s, B=%s%n", afterA, afterB);
        Assertions.assertThat(afterA).isNotEqualTo(afterB);
    }

    // -----------------------
    // TEST 3: Drill‑down flow
    // -----------------------
    @Test
    public void drilldownChangesWorksheet() {
        driver.get(VIEW_URL);
        Tableau.waitForVizReady(driver, wait);

        // Select first mark on the main sheet (e.g., a bar in bar chart)
        Tableau.selectFirstMark(driver, MAIN_SHEET);

        // After drill, assert that the active sheet or breadcrumb changed (indicative of drill‑down)
        String activeSheetName = Tableau.getActiveSheetName(driver);
        System.out.println("Active sheet after drill => " + activeSheetName);

        // Replace with your expected drilled sheet name or assert it simply changed
        Assertions.assertThat(activeSheetName).isNotBlank();
    }
}

// =====================================================================
// Helper: Minimal Tableau JS API bridge using Selenium's executeAsyncScript
// =====================================================================
class Tableau {
    private Tableau() {}

    /** Waits until Tableau JS API reports at least one viz present and active. */
    static void waitForVizReady(WebDriver driver, WebDriverWait wait) {
        // Wait for Tableau global object and a viz instance
        wait.until((ExpectedCondition<Boolean>) d -> {
            try {
                Object ok = ((JavascriptExecutor) Objects.requireNonNull(d)).executeScript(
                        "return !!(window.tableau && window.tableau.VizManager && " +
                                "tableau.VizManager.getVizs && tableau.VizManager.getVizs().length > 0);"
                );
                return Boolean.TRUE.equals(ok);
            } catch (JavascriptException e) {
                return false;
            }
        });

        // Also wait for workbook to be ready
        executeTableauPromise(driver, "var viz=tableau.VizManager.getVizs()[0];\n" +
                "return viz.getWorkbook().getActiveSheet().getName();");
    }

    /** Applies a filter value to a worksheet inside the active dashboard. */
    static void applyFilter(WebDriver driver, String worksheetName, String fieldName, String value) {
        String script = "var viz=tableau.VizManager.getVizs()[0];\n" +
                "var wb=viz.getWorkbook();\n" +
                "var s=wb.getActiveSheet();\n" +
                "var ws=(s.getSheetType()==='dashboard')? s.getWorksheets().get('" + worksheetName + "') : s;\n" +
                "return ws.applyFilterAsync('" + fieldName + "', ['" + value + "'], tableau.FilterUpdateType.REPLACE).then(()=> 'OK');";
        executeTableauPromise(driver, script);
    }

    /** Sets a parameter value. */
    static void setParameter(WebDriver driver, String paramName, String value) {
        String script = "var viz=tableau.VizManager.getVizs()[0];\n" +
                "var wb=viz.getWorkbook();\n" +
                "return wb.changeParameterValueAsync('" + paramName + "', '" + value + "').then(()=> 'OK');";
        executeTableauPromise(driver, script);
    }

    /** Selects the first available mark on a worksheet (e.g., click first bar) to simulate a drill. */
    static void selectFirstMark(WebDriver driver, String worksheetName) {
        String script = "var viz=tableau.VizManager.getVizs()[0];\n" +
                "var wb=viz.getWorkbook();\n" +
                "var s=wb.getActiveSheet();\n" +
                "var ws=(s.getSheetType()==='dashboard')? s.getWorksheets().get('" + worksheetName + "') : s;\n" +
                "return ws.getSummaryDataAsync({maxRows: 1}).then(function(t){\n" +
                "  var data = t.getData();\n" +
                "  if(data.length===0){return 'NO_DATA';}\n" +
                "  var firstRow = data[0];\n" +
                "  // Use the first column as the mark label/category\n" +
                "  var markVal = firstRow[0].formattedValue;\n" +
                "  return ws.selectMarksByValueAsync({\n" +
                "    fieldName: t.getColumns()[0].fieldName,\n" +
                "    value: markVal\n" +
                "  }, tableau.SelectionUpdateType.REPLACE).then(()=> 'OK');\n" +
                "});";
        executeTableauPromise(driver, script);
    }

    /** Returns the first cell as a string from a given worksheet's summary data. */
    static String getFirstCellAsString(WebDriver driver, String worksheetName) {
        String script = "var viz=tableau.VizManager.getVizs()[0];\n" +
                "var wb=viz.getWorkbook();\n" +
                "var s=wb.getActiveSheet();\n" +
                "var ws=(s.getSheetType()==='dashboard')? s.getWorksheets().get('" + worksheetName + "') : s;\n" +
                "return ws.getSummaryDataAsync({maxRows: 1}).then(function(t){\n" +
                "  var d=t.getData();\n" +
                "  if(d.length===0) return '';\n" +
                "  return d[0][0].formattedValue+'';\n" +
                "});";
        Object result = executeTableauPromise(driver, script);
        return String.valueOf(result);
    }

    /** Returns active sheet name (useful to assert drill‑down). */
    static String getActiveSheetName(WebDriver driver) {
        String script = "var viz=tableau.VizManager.getVizs()[0];\n" +
                "return viz.getWorkbook().getActiveSheet().getName();";
        Object result = executeTableauPromise(driver, script);
        return String.valueOf(result);
    }

    /** Bridge for Promises: wraps provided JS in an async executor pattern and returns value. */
    private static Object executeTableauPromise(WebDriver driver, String body) {
        String asyncWrapper = "var callback = arguments[arguments.length - 1];\n" +
                "try{\n" +
                "  Promise.resolve((function(){\n" +
                body + "\n" +
                "  })()).then(function(res){ callback(res); }).catch(function(e){ callback('JS_ERROR:'+e); });\n" +
                "}catch(e){ callback('JS_ERROR:'+e); }";
        Object out = ((JavascriptExecutor) driver).executeAsyncScript(asyncWrapper);
        if (out != null && String.valueOf(out).startsWith("JS_ERROR:")) {
            throw new RuntimeException("Tableau JS error: " + out);
        }
        return out;
    }
}
