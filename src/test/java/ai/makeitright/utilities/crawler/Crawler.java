package ai.makeitright.utilities.crawler;

import ai.makeitright.utilities.DriverConfig;
import ai.makeitright.utilities.pageobjects.SearchResultsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Crawler extends DriverConfig {

    static SearchResultsPage searchResultsPage = PageFactory.initElements(driver, SearchResultsPage.class);


    public static ArrayList<String> crawl() throws InterruptedException {
        String uri = System.getProperty("inputParameters.startPage");
        driver.navigate().to(uri);
        ArrayList<String> arrayListOfPageSources = new ArrayList<>();
        Thread.sleep(3000);
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tr.pgr td a")));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'...')]"))).click();
        Thread.sleep(3000);
        searchResultsPage.clickOneHundred();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@value ='100' and @class='btn btn-primary button-selected-auctions']")));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("tr.pgr td a")));
        List<WebElement> as = driver.findElements(By.cssSelector("tr.pgr td a"));
        Set<Integer> subpages = new HashSet<>();
        for (WebElement pageInPgr : as) {
            if (!pageInPgr.getText().equals("...")) {
                Integer subpage = Integer.parseInt(pageInPgr.getText());
                subpages.add(subpage);
            }
        }
        arrayListOfPageSources.add(driver.getPageSource());
        for (Integer subpage : subpages) {
            driver.findElement(By.xpath("//tr[@class='pgr']//td//a[contains(text(),'" + subpage + "')]")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td/span[text()='" + subpage + "']")));
            arrayListOfPageSources.add(driver.getPageSource());
        }
        return arrayListOfPageSources;
    }

}