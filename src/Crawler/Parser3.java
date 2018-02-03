package crawler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import venue.Country;

public class Parser3 extends Parser {

	@Override
	public String getDescription(String homeLink) {
		Document doc = null;
        doc = this.getURLDoc(homeLink);
		String meta = "";
		try {
			meta = doc.select(".site-description").text();
		} catch(NullPointerException e) {
			System.out.println("No class with name \"site-description\"");
		}
		return meta;
	}

	@Override
	public String getVenue(String title, String description, Country country, ArrayList<String> linkList) {
		String venue = "";
		
		// Search the description for the country of the conference
		if(!description.equals(""))
			venue = this.searchCountries(description, country);
		
		return venue;
	}
	
	@Override
	public LinkedHashMap<String, LinkedHashMap<String, String>> getDeadlines(ArrayList<String> linkList) {
		Document doc = null;
		LinkedHashMap<String, String> deadlines = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, String>> allDeadlines = new LinkedHashMap<>();
		Pattern pattern = Pattern.compile("(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)\\s+\\d{1,2}(\\s+|,)\\s+\\d{4}", Pattern.CASE_INSENSITIVE);
		String[] separated;
		this.addSearchWords();
		
		// Connect to the home page
		doc = this.getURLDoc(linkList.get(0));
		
		try {
			// Select the div with "Important Dates"
			Element el = doc.select("div:contains(Important Dates)").last();
			
			String elementString = el.select("ul li").toString().replaceAll("\r|\n", "");
			
			if(!elementString.isEmpty()) {
				elementString = elementString.replaceAll("<strike>(.*?|.*\\n.*\\n)<\\/strike>|<del>(.*?|.*\\n.*\\\\n)<\\/del>|line-through.+?>.+?<\\/.+?>", "");
				
				separated = elementString.split("</li>");
				
				int i = 0;
				for(String string: separated) {
					String found = this.findDeadline(Jsoup.parse(string).text(), pattern);
					if(!found.equals("")) {
						deadlines.put(Jsoup.parse(string.replaceAll(found, "")).text(), found);
						allDeadlines.put(Integer.toString(i), new LinkedHashMap<String, String>(deadlines));
						deadlines.clear();
						i++;
					}
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
	
	public String getAntiquity(String description, String homeLink) {
		String antiquity = "";
		Document doc = this.getURLDoc(homeLink);
		Elements el = doc.select(":contains(Other Editions)").next();
		
		for(Element e: el) {
			if(e.text().matches(".*\\d{4}.*")) {
				String[] split = e.text().split("\\d{4}");
				
				// Add all the found editions and the current one to find the conference antiquity
				return this.toOrdinal(split.length + 1);
			}
		}
		
		return antiquity;
	}
	
	public String getConferenceDays(String title, String description, String homeLink) {		
		Document doc = this.getURLDoc(homeLink);
		return this.findConfDays(doc.select("#header").text());
	}
}
