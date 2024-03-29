package crawler;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	private ArrayList<String> linkList = new ArrayList<>();
	static Logger logger = LogManager.getLogger(Crawler.class);
	
	/**
	 * Using the passed in document extracts the links and adds them to the list of links.
	 * @param home page Document
	 * @param list with the home page link
	 * @return list of links from the website
	 */
	public synchronized ArrayList<String> getAllLinks(Document doc, ArrayList<String> link) {
		this.linkList = link;
        StringBuilder sb = new StringBuilder();
        Elements links = null;
        try {
        	links = doc.select("a[href]");
        } catch(NullPointerException e) {
        	return linkList;
        }
        
        if(links != null) {
        	if(links.isEmpty()) {
                Elements eles = doc.getElementsByTag("frame");
                if(!eles.isEmpty()) {
                	for(Element e: eles) {
                    	sb.append(linkList.get(0));
                    	String src = e.attr("src");
                    	if(!src.isEmpty()) {
                    		// Check if it is a http link
                    		if(src.contains("http") && this.checkLinkFormat(src) && !this.checkDuplicates(src))
                    			linkList.add(src);
                    		else {
                    			// The link can look like this ./example.html
                    			if(src.charAt(0) == '.') {
                    				StringBuilder tempSb = new StringBuilder(src); 
                    				// Delete the dot and slash "./"
                    				tempSb.delete(0, 2);
                    				sb.append(tempSb.toString());
                    			} else {
                    				sb.append(src);
                    			}
                    			if(this.checkLinkFormat(sb.toString()) && !this.checkDuplicates(sb.toString()))
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
        }
        
        // Return the ArrayList with all the links from the given website
        return linkList;
	}
	
	
	/**
	 * Add the newly fetched links into an ArrayList
	 * @param Elements links
	 */
	private synchronized void addToLinkList(Elements links) {
		for(Element link: links) {
			// Eliminate the unneeded links with images or pdfs
			if(!this.checkDuplicates(link.attr("abs:href")) && this.checkLinkFormat(link.attr("abs:href")))
				linkList.add(link.attr("abs:href"));
		}
	}
	
	/**
	 * Checks if a link contains extensions such as pdf, docx, png etc.
	 * @param link
	 * @return true/false
	 */
	private synchronized boolean checkLinkFormat(String link) {
		if(link.toLowerCase().matches("[http].+(pdf|rar|zip|jpg|png|doc|docx)") 
				|| link.toLowerCase().matches(".*other edition.*")
				|| link.toLowerCase().matches("mailto:.+")) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if a links already exists in the list
	 * @param link
	 * @return true/false
	 */
	private synchronized boolean checkDuplicates(String link) {
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
//		logger.debug("Getting document from: " + url);
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			logger.error("Couldn't connect to: " + url);
		}
		return doc;
	}
}
