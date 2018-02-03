package crawler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import venue.Country;

public class Parser4 extends Parser {
	
	@Override
	public String getDescription(String homeLink) {
		Document doc = this.getURLDoc(homeLink);
		String description = "";
		System.out.println(homeLink);

		try {
			description = doc.select("div.page-header").parents().first().text();
		} catch(NullPointerException e) {
			System.out.println("No class with name \"site-description\"");
		}
		
		return description;
	}

	@Override
	public String getVenue(String title, String description, Country country, ArrayList<String> linkList) {
		String venue = "", header;
		Document doc = null;
		
		// Connect to the home page
        doc = this.getURLDoc(linkList.get(0));
		header = doc.select("div#header").text();
		
		// Search the target div header on the home website for the country of the conference
		if(!header.equals(""))
			venue = this.searchCountries(header, country);
		
		return venue;
	}
	
	@Override
	public LinkedHashMap<String, LinkedHashMap<String, String>> getDeadlines(ArrayList<String> linkList) {
		Document doc = null;
		Elements el;
		LinkedHashMap<String, String> deadlines = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, String>> allDeadlines = new LinkedHashMap<>();
		
		try {
			doc = getURLDoc(linkList.get(0));
			el = doc.select("div:contains(Upcoming Important Dates)").next();
		} catch(NullPointerException e) {
			System.err.println("Couldn't find \"Upcoming Important Dates\"");
			return new LinkedHashMap<String, LinkedHashMap<String, String>>();
		}
		
		if(el != null) {
			boolean finished = false;
			String regex = "(Mon(day)?|Tue(sday)?|Wed(nesday)?|Thu(rsday)?|Fri(day)?|"
					+ "Sat(urday)?|Sun(day)?)\\s\\d{1,2}.(Jan(uary)?|Feb(ruary)?|Mar(ch)?|"
					+ "Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|"
					+ "Dec(ember)?)(\\s+|,)\\d{4}|\\w+.\\d{1,2}.-.\\w+.\\d{1,2}.\\w+.\\d{4}";
			// Use the regex to find the pattern
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(el.text());
			// Split the string removing the regex
			String[] split = el.text().split(regex);
			int i = 1;
			int position = 0;
			while(!finished) {
				if(matcher.find()) {
					deadlines.put(split[i], matcher.group());
					allDeadlines.put(Integer.toString(position), new LinkedHashMap<String, String>(deadlines));
					deadlines.clear();
					i++;
					position++;
				} else
					finished = true;
			}
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
	
	public String getConferenceDays(String title, String description, String homeLink) {		
		Document doc = this.getURLDoc(homeLink);
		return this.findConfDays(doc.select("p").text());
	}

}
