package crawler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Parser8 extends Parser {

	@Override
	public LinkedHashMap<String, LinkedHashMap<String, String>> getDeadlines(ArrayList<String> linkList) {
		Document doc = null;
		LinkedHashMap<String, String> deadlines = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, String>> allDeadlines = new LinkedHashMap<>();
		Pattern pattern = Pattern.compile("(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)(.\\s+|\\s+)\\d{1,2}(\\s+|,|(st|nd|rd|th),?)\\s+\\d{4}", Pattern.CASE_INSENSITIVE);
		String[] separated, split;
		
		// Connect to the home page
		
		ArrayList<String> links = this.findAllLinks("important", linkList);
		
		for(String link: links) {
			doc = this.getURLDoc(link);
			
			try {
				// Select the div with "Important Dates"
				Element el = doc.select("div:contains(Important Dates)").last();
				String toParse = el.wholeText();
				
				separated = toParse.split("\n");
				
				String deadlineTitle = "";
				String keyHeading = "";
				for(String s: separated) {
					if(!s.matches("^\\s+$")) {
						// Remove spaces at the front and the end of a string if they are present
						s = s.replaceAll("^\\s+|\\s+$", "");
						String found = this.findPattern(s, pattern);
						if(found.isEmpty() && this.isSubmission(s)) {
							deadlineTitle = s;
						} else if(!found.isEmpty()) {
							if(!deadlineTitle.isEmpty()) {
								deadlines.put(deadlineTitle, found);
								deadlineTitle = "";
							}
						} else if(!keyHeading.isEmpty()){
							if(!deadlines.isEmpty()) {
								allDeadlines.put(keyHeading, new LinkedHashMap<String, String>(deadlines));
								deadlines.clear();
							}		
							keyHeading = s;
						} else
							keyHeading = s;
					}
				}
			} catch(NullPointerException e) {
				allDeadlines.clear();
				deadlines.clear();
			}
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
}
