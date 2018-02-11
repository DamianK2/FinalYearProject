package crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.TextNode;

public class Parser6 extends Parser {

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
			
			Elements el = doc.select("div:contains(Important Dates)");
			
			try {
				// Extract the paragraph
				String elementString = el.select("p").toString().replaceAll("\r|\n", "");
				// Replace the unneeded text
				elementString = elementString.replaceAll("<strike>(.*?|.*\\n.*\\n)<\\/strike>|<del>(.*?|.*\\n.*\\\\n)<\\/del>|line-through.+?>.+?<\\/.+?>", "");
				
				// Split the paragraphs
				separated = elementString.split("</p>");
				
				int i = 0;
				for(String string: separated) {
					String[] furtherSeparated;
					// Check if the string contains a date
					if(string.matches(this.changeToRegex(regex))) {
						// Replace the unneeded symbols and separate the string
						String noHtml = Jsoup.parse(string).text().replaceAll("-|–", "");
						furtherSeparated = noHtml.split(".*" + regex);
						for(String deadlineTitle: furtherSeparated) {
							if(!deadlineTitle.isEmpty()) {
								// Populate the maps
								deadlines.put(deadlineTitle.replaceAll("^\\s+", ""), noHtml.replaceAll(deadlineTitle, ""));
								allDeadlines.put(Integer.toString(i), new LinkedHashMap<String, String>(deadlines));
								// Reset the variables
								deadlines.clear();
								i++;
							}
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
	public String getConferenceDays(String title, String description, ArrayList<String> linkList) {		
		Document doc = null;
		String regex = "";
		String link = this.searchLinks("[iI]mportant", linkList);
		if(link.isEmpty())
			return "";
		else {
			doc = this.getURLDoc(link);
			
			for(Element el: doc.getAllElements()) {
				for(TextNode textNode: el.textNodes()) {
					String found = this.findConfDays(textNode.text());
					if(!found.isEmpty())
						return found;
				}
			}
			
			
		}
		
		return "";
	}
}
