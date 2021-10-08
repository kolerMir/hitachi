package ai.makeitright.utilities.scraper;

import ai.makeitright.utilities.DriverConfig;
import ai.makeitright.utilities.db.AuctionData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ai.makeitright.utilities.db.AuctionData.crateAuctionDataObjectFromJSoupDocument;

public class Scraper extends DriverConfig {

    public static ArrayList<String> scrapeUrlsOfAuctions(ArrayList<String> arrayListOfPageSources) {
        List<String> hrefsOfAllAuctions = scrapePartialLinksToAuctionsDetials(arrayListOfPageSources, "div[data-id]");
        ArrayList<String> urlsOfSpecificAuctions = new ArrayList<>();
        for (String hrefOfAuction : hrefsOfAllAuctions) {
            urlsOfSpecificAuctions.add("https://aukcje.hitachicapital.pl/ItemDetails.aspx?id=" + hrefOfAuction);
        }
        return urlsOfSpecificAuctions;
    }

    public static ArrayList<AuctionData> scrapeAuctions(final ArrayList<String> urlsOfAllAuctions) throws ParseException {
        System.out.println("------------------------------------------------------");
        System.out.println("Quantity of urls of all auctions: " + urlsOfAllAuctions.size());
        System.out.println("");
        ArrayList<AuctionData> auctionDatas = new ArrayList<>();
        for (String urlOfAuction : urlsOfAllAuctions) {
            System.out.println("Downloading url: " + urlOfAuction);
            driver.navigate().to(urlOfAuction);
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#MainContent_lblEnginePowerTextKM")));
            Document document = Jsoup.parse(driver.getPageSource());
            AuctionData ad = crateAuctionDataObjectFromJSoupDocument(document, urlOfAuction);
            auctionDatas.add(ad);
        }
        return auctionDatas;
    }

    private static List<String> scrapePartialLinksToAuctionsDetials(final ArrayList<String> htmlPagesAsString,
                                                                    final String selectForAElement) {
        Set<String> finalSetOfPartialLinks = new HashSet<>();
        for (String htmlPageAsString : htmlPagesAsString) {
            Document parsedHtmlPage = Jsoup.parse(htmlPageAsString);
            Elements rows = parsedHtmlPage.select("div.Auction2 ").parents();
            for (Element row : rows) {
                Elements aTags = row.select(selectForAElement);
                List<String> temporarylistOfPartialLinks = aTags.select("div.Auction2").eachAttr("data-id");
                finalSetOfPartialLinks.addAll(temporarylistOfPartialLinks);
            }
        }
        return new ArrayList<>(finalSetOfPartialLinks);
    }
}