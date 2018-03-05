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
	private ArrayList<String> linkList = new ArrayList<>();
//	protected static ArrayList<Thread> threads = new ArrayList<>();
	
	// Fetch all links in the website, including sub-links
	public ArrayList<String> getAllLinks(Document doc, ArrayList<String> link) {
		this.linkList = link;
        StringBuilder sb = new StringBuilder(); 
        Elements links = doc.select("a[href]");
        
        if(links.isEmpty()) {
            Elements eles = doc.getElementsByTag("frame");
            if(!eles.isEmpty()) {
            	for(Element e: eles) {
                	sb.append(linkList.get(0));
                	String src = e.attr("src");
                	if(!src.isEmpty()) {
                		// Check if it isn't a http link
                		if(src.contains("http"))
                			linkList.add(src);
                		else {
                			// The link can look like this ./example.pdf
                			if(src.charAt(0) == '.') {
                				StringBuilder tempSb = new StringBuilder(src); 
                				// Delete the dot and slash "./"
                				tempSb.delete(0, 2);
                				sb.append(tempSb.toString());
                			} else {
                				sb.append(src);
                			}
                    		linkList.add(sb.toString());
                		}
                	}
                	
                	doc = this.getURLDoc(sb.toString());
                	if(doc != null)
                		links = doc.select("a[href]");
                    
                    if(!links.isEmpty())
                    	this.addToLinkList(links);
                    
                    // Reset the variable
                    sb.setLength(0);
                }
            }
            
        } else {
        	this.addToLinkList(links);
        }
        
//        for(String link: linkList) {
//        	System.out.println(link);
//        }
        
//        // Create threads for each link just fetched to decrease crawling time
//        Thread thread;
//        for(Element link: links) {
//        	if(!link.text().matches(".*[oO]ther [eE]dition.*")) {
//	        	thread = new Thread(new Worker(link, linkList));
//	        	threads.add(thread);
//	        	thread.start();
//        	} else
//        		break;
//        }
//        
//        // Join the threads to prevent the program from finishing before the threads do
//        for(int i = 0; i < threads.size(); i++) {
//			try {
//				threads.get(i).join();
//			} catch (InterruptedException e) {
//				System.out.println("Something went wrong then joining the threads.");
//				e.printStackTrace();
//			}
//        }
        
        // Return the ArrayList with all the links from the given website
        return linkList;
	}
	
	// Add the newly fetched links into an ArrayList
	private void addToLinkList(Elements links) {
		for(Element link: links) {
			// Eliminate the unneeded links with images or pdfs
			if(!this.checkDuplicates(link.attr("abs:href")) 
					&& !link.attr("abs:href").toLowerCase().matches("[http].+(pdf|rar|zip|jpg|png|doc|docx)") 
					&& !link.text().matches(".*[oO]ther [eE]dition.*")
					&& !link.attr("abs:href").toLowerCase().matches("mailto:.+"))
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
	
	public Document getURLDoc(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
			System.out.println("Fetching from " + url + "...");
		} catch (IOException e) {
			System.out.println("Something went wrong when connecting to: " + url);
//			e.printStackTrace();
		}
		return doc;
	}
}
