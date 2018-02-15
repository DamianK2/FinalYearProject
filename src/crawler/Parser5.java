package crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import venue.Country;

public class Parser5 extends Parser {
	
	@Override
	public String getDescription(String homeLink) {
		Document doc = this.getURLDoc(homeLink);
		String description = "";

		try {
			description = doc.select("p").first().text();
			System.out.println(description);
		} catch(NullPointerException e) {
			System.err.println("No paragraph found to extract the description.");
		}
		
		return description;
	}
	
	@Override
	public LinkedHashMap<String, LinkedHashMap<String, String>> getDeadlines(ArrayList<String> linkList) {
		Document doc = null;
		LinkedHashMap<String, String> deadlines = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, String>> allDeadlines = new LinkedHashMap<>();
		String regex = "(\\d{1,2}\\s+)*(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)(\\s+\\d{1,2}(\\s+|,)\\s+\\d{4}|\\s+\\d{4})";
		String[] separated;
		
		String link = this.searchLinks("[iI]mportant", linkList);
		if(link.isEmpty())
			return allDeadlines;
		else {
			doc = this.getURLDoc(link);
			
			Element el = doc.select("div:contains(Important Dates)").last();
			
			try {
				// Extract the paragraph
				String elementString = el.select("p").toString().replaceAll("\r|\n", "");
				// Replace the unneeded text
				elementString = elementString.replaceAll("<strike>(.*?|.*\\n.*\\n)<\\/strike>|<del>(.*?|.*\\n.*\\\\n)<\\/del>|line-through.+?>.+?<\\/.+?>", "");
				// Split the paragraphs
				separated = elementString.split("</p>");
				
				int i = 0;
				boolean foundTitle = false;
				String deadlineTitle = "";
				for(String string: separated) {
					// Split each paragraph on a break tag
					String[] furtherSeparated = string.split("<br>");
					for(String s: furtherSeparated) {
						// Separate the deadline titles from the submission dates
						if(!s.matches(this.changeToRegex(regex)) && !foundTitle) {
							deadlineTitle = Jsoup.parse(s).text();
							foundTitle = true;
						}
						else if(s.matches(this.changeToRegex(regex)) && foundTitle) {
							deadlines.put(deadlineTitle, Jsoup.parse(s).text());
							// Store the information in the linked hash map
							allDeadlines.put(Integer.toString(i), new LinkedHashMap<String, String>(deadlines));
							// Reset the variables
							deadlines.clear();
							foundTitle = false;
							i++;
						} else {
							deadlineTitle = Jsoup.parse(s).text();
						}
					}
				}
			} catch(NullPointerException e) {
				return new LinkedHashMap<String, LinkedHashMap<String, String>>();
			}
		}
		
		return allDeadlines;
	}
	
	@Override
	public String getAntiquity(String title, String description, ArrayList<String> linkList) {
		String link = this.searchLinks("[hH]istory", linkList);
		Document doc;
		if(!link.equals(""))
			doc = this.getURLDoc(link);
		else
			return "";
	
		int antiquity = 1;
		for(Element el: doc.getAllElements()) {
			for(TextNode textNode: el.textNodes()) {
				if(textNode.text().toLowerCase().matches(this.changeToRegex("\\d{1,2}(?:st|nd|rd|th)")))
					antiquity++;
			}
		}
		
		return this.toOrdinal(antiquity);
		
	}

	@Override
	public String getConferenceDays(String title, String description, ArrayList<String> linkList) {
		
		Document doc = this.getURLDoc(linkList.get(0));
		
		for(Element el: doc.getAllElements()) {
			for(TextNode textNode: el.textNodes()) {
				String found = this.findConfDays(textNode.text());
				if(!found.equals("")) {
					return found;
				}
			}
		}
		
		return "";
	}
}
