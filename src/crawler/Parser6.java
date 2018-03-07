package crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

public class Parser6 extends Parser {

	public Parser6(Information info, Crawler c) {
		super(info, c);
	}
	
	@Override
	public LinkedHashMap<String, LinkedHashMap<String, String>> getDeadlines(ArrayList<String> linkList) {
		Document doc = null;
		LinkedHashMap<String, String> deadlines = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, String>> allDeadlines = new LinkedHashMap<>();
		String regex = "(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)(.\\s+|\\s+)\\d{1,2}(\\s+|,|(st|nd|rd|th),?)\\s+\\d{4}";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		String[] separated, tempSeparation;
		
		// Connect to the home page
		
		ArrayList<String> links = this.findAllLinks("important-dates", linkList);
		links.addAll(this.findAllLinks("importantdates", linkList));
		links.addAll(this.findAllLinks("important_dates", linkList));
		
		for(String link: links) {
			doc = crawler.getURLDoc(link);
			
			try {
				// Select the div with "Important Dates"
				Element el = doc.select("div:contains(Important Dates)").first();
				// Remove the outdated information
				String toParse = el.toString().replaceAll("<strike>(.*?|.*\\n.*\\n)<\\/strike>|<del>(.*?|.*\\n.*\\n)<\\/del>|line-through.+?>.+?<\\/.+?>|<s>(.*?|.*\\n.*\\n)<\\/s>", "");
				// Convert to document and get the whole text
				toParse = Jsoup.parse(toParse).wholeText();
				separated = toParse.split("\n");
				
				if(this.checkDeadlineFormat(separated, pattern)) {
					String keyHeading = "";
					for(String s: separated) {
						// Ignore the empty strings
						if(!s.matches("^\\s+$")) {
							// Remove spaces at the front and the end of a string if they are present
							s = s.replaceAll("^\\s+|\\s+$", "");
							// Find the deadline in the string
							String found = this.findPattern(s, pattern);
							
							if(!found.isEmpty()) {
								do {
									// Split the string based on the found deadline
									tempSeparation = s.split(found);
									// The first element in the array is the deadline title
									deadlines.put(tempSeparation[0], found);
									// Try to find the next deadline in the string
									if(tempSeparation.length > 1)
										s = tempSeparation[1];
									else
										s = tempSeparation[0];
										
									found = this.findPattern(s, pattern);
								} while(!found.isEmpty());
							// Update the list containing all the deadlines
							} else if(!keyHeading.isEmpty()) {
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
				allDeadlines.clear();
				deadlines.clear();
			}
			
//			System.out.println("-----------------------");
//			for(String key: allDeadlines.keySet()) {
//				System.out.println();
//				System.out.println("Heading: " + key);
//				LinkedHashMap<String, String> deadlines1 = allDeadlines.get(key);
//				for(String d: deadlines1.keySet()) {
//					System.out.println(d + ": " + deadlines1.get(d));
//				}
//			}
			
			if(!allDeadlines.isEmpty())
				return allDeadlines;
		}
		

		
		return allDeadlines;
	}
	
//	public static void main(String[] args) {
//		Parser p = new Parser6(new Information());
//		p.getDeadlines(new ArrayList<String>(Arrays.asList("http://www.ieee-iccse.org/important_dates.html")));
//	}
	
	@Override
	public String getAntiquity(String title, String description, Document doc) {
		if(doc != null) {
			int antiquity = 1;
			for(Element el: doc.getAllElements()) {
				for(TextNode textNode: el.textNodes()) {
					if(textNode.text().toLowerCase().matches(this.changeToRegex("\\d{1,2}(?:st|nd|rd|th)")))
						antiquity++;
				}
			}
			
			return this.toOrdinal(antiquity);
		} else
			return "";
	}
	
	@Override
	public String getConferenceDays(String title, String description, Document doc) {		
		if(doc != null) {
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
	
	/**
	 * Checks the format of the webpage to decide if the method should be used
	 * @param separated
	 * @return true/false
	 */
	private boolean checkDeadlineFormat(String[] separated, Pattern pattern) {
		
		for(String s: separated) {
			// Ignore all the empty strings
			if(!s.matches("^\\s+$")) {
				// If the string contains both submission and the deadline in the same line then use this parser
				if(this.isSubmission(s)) {
					if(!this.findPattern(s, pattern).equals(""))
						return true;
				}
			}
		}
		return false;
	}
}
