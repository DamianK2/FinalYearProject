package Crawler;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Implementation of a thread class fetching sub-links from the already gotten links on the website.
 * 
 * @author Damian Kluziak
 *
 */
public class Worker implements Runnable {
	
	private Element link;
	private ArrayList<String> linkList;
	
	public Worker(Element link, ArrayList<String> linkList) {
		this.link = link;
		this.linkList = linkList;
	}
	
	// Runs the thread when created
	public void run() {
		this.getSublinks(this.link);
	}
	
	// Fetches more links from already fetched links from Crawler
	private void getSublinks(Element link) {
		Document doc = null;
		Elements sublinks = null;
			try {
				doc = Jsoup.connect(link.attr("abs:href")).ignoreContentType(true).get();
				
			} catch (IOException e) {
				System.out.println("Something went wrong while fetching the sublinks.");
				e.printStackTrace();
			}	
			sublinks = doc.select("ul li ul a[href]");
			if(!sublinks.isEmpty())
				this.addToLinkList(sublinks);
	}
	
	// Adds the newly fetched links into an ArrayList
	private void addToLinkList(Elements links) {
		for(Element link: links) {
			// Change element to string
			String sublink = link.attr("abs:href");
			// If the link is not the same as one of existing links, then add it
			if(!this.checkDuplicates(sublink))
				this.linkList.add(sublink);
		}
	}
	
	private boolean checkDuplicates(String sublink) {
		boolean check = false;
		for(String link: linkList) {
			if(sublink.equals(link))
				check = true;
		}
		return check;
	}
}
