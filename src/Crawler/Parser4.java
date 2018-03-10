package crawler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import database.Information;
import venue.Country;

public class Parser4 extends Parser {
	static Logger logger = LogManager.getLogger(Parser4.class);
	
	public Parser4(Information info, Crawler c) {
		super(info, c);
	}
	
	@Override
	public String getDescription(Document doc) {
		logger.debug("Getting description");
		String description = "";

		try {
			description = doc.select("div.page-header").parents().first().text();
		} catch(NullPointerException e) {
			logger.info("Null Pointer exception but was expected because not all websites have a div with class \"page-header\".");
		}
		
		return description;
	}

	//TODO change to suit tests
	@Override
	public String getVenue(String title, String description, Country country, Document doc) {
		logger.debug("Getting venue links from document header and footer");
		String venue = "", found;
		String[] possibleNames = {"div#header", "div.header","header#header", "header.header", "div#footer", "div.footer", "footer#footer", "footer.footer", "header", "footer"};
	        
		try {
			// Iterate through all the possible name id's and classes
	        for(String name: possibleNames) {
	        	found = doc.select(name).text();
	        	if(!found.isEmpty()) {
	        		// Check if the found string contains the country
	        		venue = this.searchCountries(found, country);
	        		if(!venue.isEmpty())
	        			return venue;
	        	}
	        }
		} catch(NullPointerException e) {
			logger.info("Null Pointer exception but was expected.");
		}
		
		return venue;
	}
	
	@Override
	public LinkedHashMap<String, LinkedHashMap<String, String>> getDeadlines(ArrayList<String> linkList) {
		Document doc = null;
		Elements el;
		LinkedHashMap<String, String> deadlines = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, String>> allDeadlines = new LinkedHashMap<>();
		
		try {
			logger.debug("Getting deadlines from: " + linkList.get(0));
			doc = crawler.getURLDoc(linkList.get(0));
			el = doc.select("div:contains(Upcoming Important Dates)").next();
		} catch(NullPointerException e) {
			logger.info("Null Pointer exception but was expected.");
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
		
		return allDeadlines;
	}
	
	@Override
	public String getAntiquity(String title, String description, Document doc) {
		logger.debug("Getting antiquity from passed in document");
		String antiquity = "";
		try {
			Elements el = doc.select(":contains(Other Editions)").next();
			
			for(Element e: el) {
				if(e.text().matches(".*\\d{4}.*")) {
					String[] split = e.text().split("\\d{4}");
					
					// Add all the found editions and the current one to find the conference antiquity
					return this.toOrdinal(split.length + 1);
				}
			}
		} catch(NullPointerException e) {
			logger.info("Null Pointer exception but was expected.");
		}
		
		return antiquity;
	}
	
	public String getConferenceDays(String title, String description, Document doc) {
		if(doc == null)
			return "";
		
		logger.debug("Getting conference days from the passed in document");
		return this.findConfDays(doc.select("p").text());
	}

}
