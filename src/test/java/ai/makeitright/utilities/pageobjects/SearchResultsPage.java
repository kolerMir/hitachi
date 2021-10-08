package ai.makeitright.utilities.pageobjects;

import ai.makeitright.utilities.DriverConfig;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class SearchResultsPage extends DriverConfig {

    @FindBy(css = "#MainContent_btn_100")
    public WebElement oneHundred;

    @FindBy(xpath = "//tr[@class='pgr']/td/table/tbody/tr/td/a")
    public List<WebElement> pagesInPgr;

    public SearchResultsPage clickOneHundred() {
        oneHundred.click();
        return this;
    }

}