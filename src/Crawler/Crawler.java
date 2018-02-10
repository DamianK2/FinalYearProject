package crawler;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Implementation of a web crawler. Fetches links from the main page.
 * 
 * @author Damian Kluziak
 *
 */
public class Crawler {
	protected static ArrayList<String> linkList = new ArrayList<>();
	protected static ArrayList<Thread> threads = new ArrayList<>();

	// Add the website that we want to crawl
	public Crawler(String url) {
		linkList.add(url);
		System.out.println("(inside Crawler)The url is: " + url);
	}
	
	// Fetch all links in the website, including sub-links
	public ArrayList<String> getAllLinks() {
		Document doc = null;
        String firstURL = linkList.get(0);
		try {
			doc = Jsoup.connect(firstURL).get();
			System.out.println("Fetching from " + firstURL + "...");
		} catch (IOException e) {
			System.out.println("Something went wrong when getting the first element from the list of links.");
			e.printStackTrace();
		}
        Elements links = doc.select("ul a[href]");
        this.addToLinkList(links);
        
        // Create threads for each link just fetched to decrease crawling time
        Thread thread;
        for(Element link: links) {
        	if(!link.text().matches(".*[oO]ther [eE]dition.*")) {
	        	thread = new Thread(new Worker(link, linkList));
	        	threads.add(thread);
	        	thread.start();
        	} else
        		break;
        }
        
        // Join the threads to prevent the program from finishing before the threads do
        for(int i = 0; i < threads.size(); i++) {
			try {
				threads.get(i).join();
			} catch (InterruptedException e) {
				System.out.println("Something went wrong then joining the threads.");
				e.printStackTrace();
			}
        }
        
        // Return the ArrayList with all the links from the given website
        return linkList;
	}
	
	// Add the newly fetched links into an ArrayList
	protected void addToLinkList(Elements links) {
		for(Element link: links) {			
			if(!this.checkDuplicates(link.attr("abs:href")))
				linkList.add(link.attr("abs:href"));
		}
	}
	
	private boolean checkDuplicates(String link) {
		boolean check = false;
		for(String l: linkList) {
			if(link.equals(l))
				check = true;
		}
		return check;
	}
}
