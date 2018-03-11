package crawler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import database.Information;
import venue.Country;

public class Parser3 extends Parser {
	static Logger logger = LogManager.getLogger(Parser3.class);
	
	public Parser3(Information info, Crawler c) {
		super(info, c);
	}

	@Override
	public String getDescription(Document doc) {
		String meta = "";
		try {
			meta = doc.select(".site-description").text();
			return meta;
		} catch(NullPointerException e) {
			return "";
		}
		
	}

	@Override
	public String getVenue(String title, String description, Country country, Document doc) {
		String venue = "";
		
		// Search the description for the country of the conference
		if(!description.equals(""))
			venue = this.searchCountries(description, country);
		
		logger.debug("Found venue \"" + venue + "\" from from description");
		
		return venue;
	}
	
	@Override
	public LinkedHashMap<String, LinkedHashMap<String, String>> getDeadlines(ArrayList<String> linkList) {
		Document doc = null;
		LinkedHashMap<String, String> deadlines = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, String>> allDeadlines = new LinkedHashMap<>();
		Pattern pattern = Pattern.compile("((Mon(day)?|Tue(sday)?|Wed(nesday)?|Thu(rsday)?|Fri(day)?|Sat(urday)?|Sun(day)?)\\s\\d{1,2}.|\\d{1,2}.|\\d{1,2}-\\d{1,2}.)*(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)(\\s+|,|\\.)\\d{4}|\\w+.\\d{1,2}.-.\\w+.\\d{1,2}.\\w+.\\d{4}|\\w+.\\d{1,2},\\s\\d{4}", Pattern.CASE_INSENSITIVE);
		String[] separated;
		
		logger.debug("Getting deadlines from: " + linkList.get(0));
		// Connect to the home page
		doc = crawler.getURLDoc(linkList.get(0));
		
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
		
		return allDeadlines;
	}
	
	@Override
	public String getAntiquity(String title, String description, Document doc) {
		String antiquity = "";
	
		Elements ele = null;
		try {
			Element el = doc.select("div:contains(Previous)").last();
			ele = el.select("ul li");
		} catch(NullPointerException e) {
			return antiquity;
		}
	
		int currentYear = 1;
		// Count the number of previously held conferences
		currentYear += ele.size();
		antiquity = this.toOrdinal(currentYear);
		
		// If it is the first conference then it wouldn't have "Previous" editions
		if(currentYear == 1)
			return "";
		else {
			logger.debug("Found antiquity \"" + antiquity +  "\" from passed in document");
			return antiquity;
		}
			
	}
	
	public String getConferenceDays(String title, String description, Document doc) {
		String[] possibleNames = {"div#header", "div.header","header#header", "header.header", "div#footer", "div.footer", "footer#footer", "footer.footer", "header", "footer"};
	        
		try {
	        // Iterate through all the possible name id's and classes
	        for(String name: possibleNames) {
	        	String confDays = this.findConfDays(doc.select(name).text());
	        	if(!confDays.isEmpty()) {
	        		logger.debug("Found conference days \"" + confDays + "\" from the passed in document");
	        		return confDays;
	        	}
	        }
		} catch(NullPointerException e) {
			return "";
		}
		
		return "";
	}
	
	@Override
	public LinkedHashMap<String, List<String>> getOrganisers(Document doc, Country country) {
		LinkedHashMap<String, List<String>> committees = new LinkedHashMap<>();
		
		// Initialize variables
		String tempSubteam = "";
		List<String> members = new ArrayList<>();
		
		// Remove the spaces
		String[] split = doc.wholeText().split("\n");
		for(String toCheck: split) {
			toCheck = toCheck.trim();
			if(!toCheck.isEmpty()) {
				// Check for a committee keyword in the string
				if(this.searchForCommittees(toCheck)) {
					// Add the found values to the output map
					if(!members.isEmpty() && !tempSubteam.equals("")) {
						committees.put(tempSubteam.replaceAll("\"|'", ""), new ArrayList<String>(members));
					}
					// Overwrite the variables
					tempSubteam = toCheck;
					members.clear();
				} else {
					// Add the members
					members.add(toCheck.replaceAll("\"|'", ""));
				}
			}
		}
		if(!members.isEmpty() && !tempSubteam.equals("")) {
			committees.put(tempSubteam, new ArrayList<String>(members));
		}
		
		// If only 1 committee is returned then it must be an error
		return committees.size() < 2 ? new LinkedHashMap<String, List<String>>() : committees;
	}
}
