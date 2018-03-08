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
	
	
	/**
	 * Using the passed in document extracts the links and adds them to the list of links.
	 * @param home page Document
	 * @param list with the home page link
	 * @return list of links from the website
	 */
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
        
        // Return the ArrayList with all the links from the given website
        return linkList;
	}
	
	
	/**
	 * Add the newly fetched links into an ArrayList
	 * @param Elements links
	 */
	private void addToLinkList(Elements links) {
		for(Element link: links) {
			// Eliminate the unneeded links with images or pdfs
			if(!this.checkDuplicates(link.attr("abs:href")) 
					&& !link.attr("abs:href").toLowerCase().matches("[http].+(pdf|rar|zip|jpg|png|doc|docx)") 
					&& !link.attr("abs:href").toLowerCase().matches(".*other edition.*")
					&& !link.attr("abs:href").toLowerCase().matches("mailto:.+"))
				linkList.add(link.attr("abs:href"));
		}
	}
	
	/**
	 * Checks if a links already exists in the list
	 * @param link
	 * @return true/false
	 */
	private boolean checkDuplicates(String link) {
		boolean check = false;
		for(String l: linkList) {
			if(link.equals(l))
				check = true;
		}
		return check;
	}
	
	/**
	 * Gets the Document from the passed in link
	 * @param url
	 * @return document of the html
	 */
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
