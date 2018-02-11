package crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Parser7 extends Parser {
	
	@Override
	public LinkedHashMap<String, LinkedHashMap<String, String>> getDeadlines(ArrayList<String> linkList) {
		Document doc = null;
		LinkedHashMap<String, String> deadlines = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, String>> allDeadlines = new LinkedHashMap<>();
		Pattern pattern = Pattern.compile("(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)(.\\s+|\\s+)\\d{1,2}(\\s+|,|(st|nd|rd|th),?)\\s+\\d{4}", Pattern.CASE_INSENSITIVE);
		String[] separated, split;
		
		// Connect to the home page
		doc = this.getURLDoc(linkList.get(0));
		// Select the div with "Important Dates"
		Element el = doc.select("div:contains(Important Dates)").last();
		
		try {
			// Extract the paragraph
			String elementString = el.select("p").toString().replaceAll("\r|\n", "");
			elementString = elementString.replaceAll("<strike>(.*?|.*\\n.*\\n)<\\/strike>|<del>(.*?|.*\\n.*\\\\n)<\\/del>|line-through.+?>.+?<\\/.+?>", "");
			
//			System.out.println(elementString);
			String deadline = "";
			String keyHeading = "";
			if(!elementString.isEmpty()) {
				// Split into multiple lines on seeing the <p> tag
				separated = elementString.split("</p>");
				int i = 0;
				for(String string: separated) {
					// Split into multiple lines on seeing the <br> tag
					String[] furtherSeparated = string.split("<br> ");
					for(String s: furtherSeparated) {
						// If the deadline was not found yet then search for it
						if(deadline.isEmpty()) {
							deadline = this.findDeadline(Jsoup.parse(s).text(), pattern);
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
		
		
//		for(String key: allDeadlines.keySet()) {
//			System.out.println("Heading: " + key);
//			LinkedHashMap<String, String> deadlines1 = allDeadlines.get(key);
//			for(String d: deadlines1.keySet()) {
//				System.out.println(d + ": " + deadlines1.get(d));
//			}
//		}
		
		return allDeadlines;
	}
	
	private boolean isSubmission(String toCheck) {
		String[] keywords = {"submis", "submit", "notification"};
		
		for(String key: keywords) {
			if(toCheck.toLowerCase().matches(this.changeToRegex(key)))
				return true;
		}
		
		return false;
	}
}
