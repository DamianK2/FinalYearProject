package Crawler;

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
	private static ArrayList<String> linkList = new ArrayList<>();
	private static ArrayList<Thread> threads = new ArrayList<>();

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
        	thread = new Thread(new Worker(link, linkList));
        	threads.add(thread);
        	thread.start();
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
	private void addToLinkList(Elements links) {
		for(Element link: links) {
			linkList.add(link.attr("abs:href"));
		}
	}
}
