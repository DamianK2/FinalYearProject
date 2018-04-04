package crawler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import database.Information;

public class Parser7 extends Parser {
	static Logger logger = LogManager.getLogger(Parser7.class);
	
	public Parser7(Information info, Crawler c) {
		super(info, c);
	}
	
	@Override
	public LinkedHashMap<String, LinkedHashMap<String, String>> getDeadlines(ArrayList<String> linkList) {
		Document doc = null;
		LinkedHashMap<String, String> deadlines = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, String>> allDeadlines = new LinkedHashMap<>();
		Pattern pattern = Pattern.compile("(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)(.\\s+|\\s+)\\d{1,2}(\\s+|,|(st|nd|rd|th),?)\\s+\\d{4}", Pattern.CASE_INSENSITIVE);
		String[] separated;
		
		logger.debug("Getting deadlines from: " + linkList.get(0));
		// Connect to the home page
		doc = crawler.getURLDoc(linkList.get(0));

		try {
			// Select the div with "Important Dates"
			Element el = doc.select("div:contains(Important Dates)").last();
			
			// Extract the paragraph
			String elementString = el.select("p").toString().replaceAll("\r|\n", "");
			elementString = elementString.replaceAll("<strike>(.*?|.*\\n.*\\n)<\\/strike>|<del>(.*?|.*\\n.*\\n)<\\/del>|line-through.+?>.+?<\\/.+?>|<s>(.*?|.*\\n.*\\n)<\\/s>", "");
			
			String deadline = "";
			String keyHeading = "";
			if(!elementString.isEmpty()) {
				// Split into multiple lines on seeing the <p> tag
				separated = elementString.split("</p>");
				for(String string: separated) {
					// Split into multiple lines on seeing the <br> tag
					String[] furtherSeparated = string.split("<br> ");
					for(String s: furtherSeparated) {
						// If the deadline was not found yet then search for it
						if(deadline.isEmpty()) {
							deadline = this.findPattern(Jsoup.parse(s).text(), pattern);
							// If it was found in this iteration of the loop then we can skip to the next iteration
							if(!deadline.isEmpty())
								continue;
						}
						
						// Check if the string contains any of the submission keywords
						if(isSubmission(Jsoup.parse(s).text())) {
							// Add the values to the map and reset variables
							if(!deadline.isEmpty()) {
								deadlines.put(Jsoup.parse(s).text(), deadline);
								deadline = "";
							}
						} else {
							// If we have some values in the map then add them to the main map together with the key
							if(!deadlines.isEmpty()) {
								allDeadlines.put(keyHeading, new LinkedHashMap<String, String>(deadlines));
								deadlines.clear();
							}
							// If there is no deadline then change the key (the input is in the format of "heading \n date \n deadline title...")
							if(deadline.isEmpty())
								keyHeading = Jsoup.parse(s).text();
						}
					}
				}
				
				// Put the values in the map after the loop finishes to avoid losing data
				if(!deadlines.isEmpty()) {
					allDeadlines.put(keyHeading, new LinkedHashMap<String, String>(deadlines));
				}
			}
		} catch(NullPointerException e) {
			return allDeadlines;
		}
		
		return allDeadlines;
	}
}
