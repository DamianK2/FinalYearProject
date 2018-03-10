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

public class Parser8 extends Parser {
	static Logger logger = LogManager.getLogger(Parser8.class);

	public Parser8(Information info, Crawler c) {
		super(info, c);
	}
	
	@Override
	public LinkedHashMap<String, LinkedHashMap<String, String>> getDeadlines(ArrayList<String> linkList) {
		Document doc = null;
		LinkedHashMap<String, String> deadlines = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, String>> allDeadlines = new LinkedHashMap<>();
		Pattern pattern = Pattern.compile("(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)(.\\s+|\\s+)\\d{1,2}(\\s+|,|(st|nd|rd|th),?)\\s+\\d{4}", Pattern.CASE_INSENSITIVE);
		String[] separated;
		
		// Connect to the home page
		
		ArrayList<String> links = this.findAllLinks("important-dates", linkList);
		links.addAll(this.findAllLinks("importantdates", linkList));
		links.addAll(this.findAllLinks("important_dates", linkList));
		
		for(String link: links) {
			logger.debug("Getting deadlines from: " + link);
			doc = crawler.getURLDoc(link);
			
			try {
				// Select the div with "Important Dates"
				Element el = doc.select("div:contains(Important Dates)").last();
				// Remove the outdated information
				String toParse = el.toString().replaceAll("<strike>(.*?|.*\\n.*\\n)<\\/strike>|<del>(.*?|.*\\n.*\\n)<\\/del>|line-through.+?>.+?<\\/.+?>|<s>(.*?|.*\\n.*\\n)<\\/s>", "");
				// Convert to document and get the whole text
				toParse = Jsoup.parse(toParse).wholeText();
				separated = toParse.split("\n");
				
				if(this.checkDeadlineFormat(separated, pattern)) {
					String deadlineTitle = "";
					String keyHeading = "";
					for(String s: separated) {
						// Ignore the empty strings
						if(!s.matches("^\\s+$")) {
							// Remove spaces at the front and the end of a string if they are present
							s = s.replaceAll("^\\s+|\\s+$", "");
							// Find the deadline in the string
							String found = this.findPattern(s, pattern);
							
							// Check if the string contains a submission keyword
							if(found.isEmpty() && this.isSubmission(s)) {
								// Use this for the title of the deadline
								deadlineTitle = s;
							} else if(!found.isEmpty()) {
								// Update the list to use the deadline title and deadline that were found
								if(!deadlineTitle.isEmpty()) {
									deadlines.put(deadlineTitle, found);
									deadlineTitle = "";
								}
							// Update the list containing all the deadlines
							} else if(!keyHeading.isEmpty()){
								if(!deadlines.isEmpty()) {
									allDeadlines.put(keyHeading, new LinkedHashMap<String, String>(deadlines));
									deadlines.clear();
								}		
								// Change the main heading
								keyHeading = s;
							} else
								keyHeading = s;
						}
					}
				}
			} catch(NullPointerException e) {
				logger.info("Null Pointer exception but was expected.");
				allDeadlines.clear();
				deadlines.clear();
			}
			
			if(!allDeadlines.isEmpty())
				return allDeadlines;
		}
		
		
//		for(String key: allDeadlines.keySet()) {
//			System.out.println();
//			System.out.println("Heading: " + key);
//			LinkedHashMap<String, String> deadlines1 = allDeadlines.get(key);
//			for(String d: deadlines1.keySet()) {
//				System.out.println(d + ": " + deadlines1.get(d));
//			}
//		}
		
		return allDeadlines;
	}

	/**
	 * Checks the format of the webpage to decide if the method should be used
	 * @param separated
	 * @return true/false
	 */
	private boolean checkDeadlineFormat(String[] separated, Pattern pattern) {
		
		for(String s: separated) {
			// Ignore all the empty strings
			if(!s.matches("^\\s+$")) {
				// If the string contains both submission and the deadline in the same line then don't use this parser
				if(this.isSubmission(s)) {
					if(!this.findPattern(s, pattern).equals(""))
						return false;
				}
			}
		}
		return true;
	}
}
