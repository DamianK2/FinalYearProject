package crawler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
		Pattern pattern = Pattern.compile("((Mon(day)?|Tue(sday)?|Wed(nesday)?|Thu(rsday)?|Fri(day)?|Sat(urday)?|Sun(day)?)\\s\\d{1,2}.|\\d{1,2}.|\\d{1,2}-\\d{1,2}.)*(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)(\\s+|,)\\d{4}|\\w+.\\d{1,2}.-.\\w+.\\d{1,2}.\\w+.\\d{4}|\\w+.\\d{1,2},\\s\\d{4}", Pattern.CASE_INSENSITIVE);
		String[] separated;
		
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
					String found = this.findPattern(Jsoup.parse(string).text(), pattern);
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
	
	@Override
	public String getAntiquity(String title, String description, ArrayList<String> linkList) {
		String antiquity = "";
	
		// Connect to the home page
		Document doc = this.getURLDoc(linkList.get(0));
		Elements ele = null;
		try {
			Element el = doc.select("div:contains(Previous)").last();
			ele = el.select("ul li");
		} catch(NullPointerException e) {
			return antiquity;
		}
		
		if(ele.isEmpty())
			return antiquity;
		else {
			int currentYear = 1;
			// Count the number of previously held conferences
			currentYear += ele.size();
			antiquity = this.toOrdinal(currentYear);
			
			return antiquity;
		}
	}
	
	public String getConferenceDays(String title, String description, ArrayList<String> linkList) {		
		Document doc = this.getURLDoc(linkList.get(0));
		return this.findConfDays(doc.select("#header").text());
	}
}
