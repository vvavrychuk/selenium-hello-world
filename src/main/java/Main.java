import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
  private static boolean isHeroku() {
    return System.getenv("DYNO") != null;
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    String query;
    if (args.length == 0) {
      System.out.print("Enter search query: ");
      query = (new BufferedReader(new InputStreamReader(System.in))).readLine();
    } else {
      query = args[0];
    }

    ChromeDriverService service;
    if (isHeroku())
      service = XvfbChromeDriverService.createDefaultService();
    else
      service = ChromeDriverService.createDefaultService();

    ChromeOptions options = new ChromeOptions();
    if (isHeroku()) {
      options.setBinary("/app/.apt/opt/google/chrome/chrome");
      options.addArguments("--no-sandbox");
    }

    WebDriver driver = new ChromeDriver(service, options);
    driver.get("http://www.google.com");

    // Enter query into HTML input.
    WebElement queryInput = driver.findElement(By.name("q"));
    queryInput.sendKeys(query);
    queryInput.submit();

    // Google loads search results are loaded asynchronously with JavaScript.
    // Wait for this to happen.
    WebDriverWait wait = new WebDriverWait(driver, 10);
    WebElement searchResults = wait.until(
      ExpectedConditions.presenceOfElementLocated(By.id("rso")));

    for (WebElement searchResult : searchResults.findElements(By.tagName("h3"))) {
      System.out.println(searchResult.getText());
    }

    Thread.sleep(1000);
    driver.quit();
  }
}
