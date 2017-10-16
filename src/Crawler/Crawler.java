package Crawler;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Crawler {
	
	private static ArrayList<String> linkList = new ArrayList<String>();

	public static void main(String[] args) throws IOException {

		Crawler test = new Crawler();
		test.getAllLinks();
        print("\nLinks: (%d)", linkList.size());
        int i = 1;
        for (String link : linkList) {
            print(i + " * a: <%s>", link);
            i++;
        }
    }
	
	private void getAllLinks() {
        String url = "https://icpe2017.spec.org/icpe2017.html";
        print("Fetching %s...", url);
        Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Elements links = doc.select("ul a[href]");
        this.addToLinkList(links);
        this.getSublinks(links);
        
	}

	private void getSublinks(Elements links) {
		Document doc = null;
		Elements sublinks = null;
		for(Element link: links) {
			try {
				doc = Jsoup.connect(link.attr("abs:href")).ignoreContentType(true).get();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			sublinks = doc.select("ul li ul a[href]");
			this.addToLinkList(sublinks);
		}	
	}
	
	private void addToLinkList(Elements links) {
		for(Element link: links) {
			linkList.add(link.attr("abs:href"));
		}
	}
	
    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
}
