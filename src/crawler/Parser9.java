package crawler;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import database.Information;

public class Parser9 extends Parser {
	static Logger logger = LogManager.getLogger(Parser9.class);

	public Parser9(Information info, Crawler c) {
		super(info, c);
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
			logger.debug("Getting deadlines from: " + link);
			doc = crawler.getURLDoc(link);
			
			try {
				Elements el = doc.select("div:contains(Important Dates)");
				// Extract the paragraph
				String elementString = el.select("p").toString().replaceAll("\r|\n", "");
				// Replace the unneeded text
				elementString = elementString.replaceAll("<strike>(.*?|.*\\n.*\\n)<\\/strike>|<del>(.*?|.*\\n.*\\n)<\\/del>|line-through.+?>.+?<\\/.+?>|<s>(.*?|.*\\n.*\\n)<\\/s>", "");
				
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
}
