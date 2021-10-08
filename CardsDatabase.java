package cardsdatabase;

import java.io.FileReader;

import java.io.Reader;

import java.util.ArrayList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class CardsDatabase {
    //change to your database name
    static final String DB_URL
            = "jdbc:mysql://localhost:3306/cardsdatabase";
    static final String DB_DRV
            = "com.mysql.jdbc.Driver";
    static final String DB_USER = "root";
    static final String DB_PASSWD = "root";
    static ArrayList<ArrayList<String>> allGames = new ArrayList();
    static ArrayList<String> appid = new ArrayList();
    static ArrayList<String> singleGame = new ArrayList();
    static String url;
    static ArrayList<String> gamename = new ArrayList();


    public static void main(String[] args) throws Exception {
        getCSV();
        getCardLists();

    }

    public static void getCardLists() {

        try {
            for (String i : appid) {

                Document doc = Jsoup.connect("https://steamcommunity.com/market/search?category_753_Game%5B%5D=tag_app_" + i + "&category_753_cardborder%5B%5D=tag_cardborder_0&category_753_item_class%5B%5D=tag_item_class_2&appid=753").get();

                Elements els = doc.getElementsByClass("market_listing_item_name");
                els.forEach(element -> singleGame.add(element.text()));
                allGames.add(singleGame);
                writeToDatabase(i);
                singleGame.clear();

                Thread.sleep(15000);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public ArrayList<String> getCSV() {
        try {
            Reader in = new FileReader("path to csv file");
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
            for (CSVRecord record : records) {

                appid.add(record.get(15));
                gamename.add(record.get(0));
            }
            appid.remove(0);
            System.out.println(appid);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return appid;
    }

    static public void writeToDatabase(String i) throws Exception {
        try {
            Class.forName(DB_DRV);

            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            //Database contains one table(cards) with two columns - appid(INT) card_name(VARCHAR) change according to your needs
            PreparedStatement st = conn.prepareStatement("INSERT INTO cards(appid,card_name) VALUE(?,?)");

            for (ArrayList<String> k : allGames) {
                System.out.println(k);
            }

            for (String j : singleGame) {

                st.setInt(1, Integer.parseInt(i));
                st.setString(2, j);
                st.executeUpdate();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
