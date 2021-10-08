package ai.makeitright.utilities.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "auctions")
public class AuctionData {
    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private String marka;
    @DatabaseField
    private String model;
    @DatabaseField
    private String rokProdukcji;
    @DatabaseField
    private String numerRejestracyjny;
    @DatabaseField
    private String vin;
    @DatabaseField
    private String rodzajPaliwa;
    @DatabaseField
    private String klasaEuro;
    @DatabaseField
    private String kluczyki;
    @DatabaseField
    private String dowodRejestracyjny;
    @DatabaseField
    private String kartaPojazdu;
    @DatabaseField
    private Long przebieg;
    @DatabaseField
    private Long cena;
    @DatabaseField
    private String pdfUrl;
    @DatabaseField
    private String wyposazenie;
    @DatabaseField
    private Timestamp dataWyszukania;
    @DatabaseField
    private Timestamp doKoncaAukcji;
    @DatabaseField
    private String zrodlo;
    @DatabaseField
    private String typAukcji;

    public static AuctionData crateAuctionDataObjectFromJSoupDocument(Document document, String urlOfAuction) throws ParseException {
        Element divAC = document.selectFirst("#content.exchange-container");
        AuctionData ad = new AuctionData();
        ad.setId(urlOfAuction);

        String markAndModel = divAC.selectFirst("#MainContent_lblMakeModel").ownText();
        ad.setMarka(markAndModel.substring(0, markAndModel.indexOf(" ")));
        ad.setModel(markAndModel.substring(markAndModel.indexOf(" ")+1));

        ad.setRokProdukcji(divAC.selectFirst("#MainContent_lblProductionYear").selectFirst("span").ownText());
        ad.setNumerRejestracyjny(divAC.selectFirst("#MainContent_lblRegNumber").ownText());
        ad.setVin(divAC.selectFirst("#MainContent_lblVINheader").ownText());
        ad.setRodzajPaliwa(divAC.selectFirst("#MainContent_lblFuelType").text());
        ad.setKlasaEuro("");
        ad.setKluczyki("");
        ad.setDowodRejestracyjny("");
        ad.setKartaPojazdu("");

        String przebieg = divAC.selectFirst("#MainContent_lblMileage").ownText()
                .replaceAll(" ","")
                .replaceAll("km","");
        ad.setPrzebieg(Long.valueOf(przebieg));

        ad.setCena(Long.valueOf(divAC.selectFirst("#MainContent_lblPriceNetValue").ownText()
                .replaceAll(",00", "")
                .replaceAll("z.", "")
                .replaceAll("\\s+", "")));

        String numer = urlOfAuction.substring(urlOfAuction.lastIndexOf("=") + 1);
        ad.setPdfUrl("https://aukcje.hitachicapital.pl/SiteHelpers/FilesRender.aspx?id=" + numer + "&type=EWV");

        StringBuilder sumOfWyposazenie = new StringBuilder();
        Elements listingFeatures = divAC.select("h2[contains(Wyposa)]  col-md-4");
        if (listingFeatures != null) {
            for (Element feature : listingFeatures) {
                sumOfWyposazenie.append(feature.ownText()).append(" ");
            }
            ad.setWyposazenie(sumOfWyposazenie.toString());
        }

        Timestamp dataWyszukaniaTimestamp = new Timestamp(System.currentTimeMillis());
        ad.setDataWyszukania(dataWyszukaniaTimestamp);

        String koniecAukcji = "2070-01-01 01:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parsedDate = dateFormat.parse(koniecAukcji);
        Timestamp koniecAukcjiTimestamp = new java.sql.Timestamp(parsedDate.getTime());
        ad.setDoKoncaAukcji(koniecAukcjiTimestamp);

        ad.setZrodlo("hitachi");
        ad.setTypAukcji("kup teraz");
        return ad;
    }

}