package Crawler;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Crawler {
	
	private static ArrayList<String> linkList = new ArrayList<String>();
	
	public Crawler(String url) {
		linkList.add(url);
	}
	
	public ArrayList<String> getAllLinks() {
        Document doc = null;
        String firstURL = linkList.get(0);
		try {
			doc = Jsoup.connect(firstURL).get();
			System.out.println("Fetching from" + firstURL + "...");
		} catch (IOException e) {
			System.out.println("Something went wrong when getting the first element from the list of links.");
			e.printStackTrace();
		}
        Elements links = doc.select("ul a[href]");
        this.addToLinkList(links);
        this.getSublinks(links);
        return linkList;
	}

	private void getSublinks(Elements links) {
		Document doc = null;
		Elements sublinks = null;
		for(Element link: links) {
			try {
				doc = Jsoup.connect(link.attr("abs:href")).ignoreContentType(true).get();
				
			} catch (IOException e) {
				System.out.println("Something went wrong while fetching the sublinks.");
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
}
