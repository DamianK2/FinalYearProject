package Crawler;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Crawler {

	public static void main(String[] args) throws IOException {
        String url = "https://icpe2017.spec.org/icpe2017.html";
        print("Fetching %s...", url);

        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("ul a[href]");

        print("\nLinks: (%d)", links.size());
        for (Element link : links) {
            print(" * a: <%s>", link.attr("abs:href"));
        }
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
}
