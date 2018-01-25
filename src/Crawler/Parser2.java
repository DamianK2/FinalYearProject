package Crawler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import venue.Country;

public class Parser2 extends Parser {

	public Parser2(ArrayList<String> links) {
		super(links);
	}
	
	public String getSponsors(String title, String description) {
		String sponsors = "";
		
		for(String sponsor: SPONSORS) {
			if(description.matches(this.changeToRegex(sponsor)))
				if(sponsors.isEmpty())
					sponsors += sponsor;
				else
					sponsors += "/" + sponsor;
		}
		
		return sponsors;
	}
	
	@Override
	public String getDescription() {
		Document doc = null;
		// Connect to the home page
        doc = this.getURLDoc(linkList.get(0));
		String meta = "", parsedMeta = "";
		try {
			parsedMeta = doc.select("meta[property=og:description]").first().attr("content");
			// Limit the string to 100 characters
			//parsedMeta = meta.replaceAll("(.{100})", "$1\n");
			System.out.println("Description: " + parsedMeta);
		} catch(NullPointerException e) {
			System.out.println("No meta with attribute \"property\"");
		}
		return parsedMeta;
	}
	
	@Override
	public String getVenue(String title, String description, Country country) {
		String venue = "";
		// Search the title for the country of the conference
		if(!title.equals(""))
			venue = this.searchCountries(title, country);
		
		return venue;
	}
	
	@Override
	public ArrayList<String> getDeadlines() {
		Document doc = null;
		ArrayList<String> deadlines = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\w+.\\s\\d{1,2},\\s\\d{4}");
		String[] separated;
		this.addSearchWords();
		
		// Connect to the home page
		doc = this.getURLDoc(linkList.get(0));
		// Select the div with "Important Dates"
		Element el = doc.select("div:contains(Important Dates)").last();
		
		// Extract the paragraph
		String elementString = el.select("p").toString();
		if(!elementString.isEmpty()) {
			// Split into multiple lines on seeing the <br> tag
			separated = elementString.split("<br> ");
			// Search for the deadlines
			for(String toFind: searchKeywords) {
				String found = this.findDeadline(separated, toFind, pattern);
				deadlines.add(found);
			}
		}
		
		return deadlines;
	}
	
	@Override
	public ArrayList<String> getAdditionalDeadlineInfo() {
		ArrayList<String> additionalInfo = new ArrayList<>();
		Document doc = null;
		this.addNewSearchWords();
		
		// Connect to the home page
		doc = this.getURLDoc(linkList.get(0));
		Element el = doc.select("div:contains(Important Dates)").last();
		
		// Get the content of the paragraph (if available)
		String elementString = el.select("p").toString();
		if(elementString.isEmpty()) {
			return additionalInfo;
		} else {
			elementString = elementString.replaceAll("\n", "");
			// Find matching information in the string by searching the keywords
			for(String keyword: searchKeywords) {
				if(elementString.matches(keyword)) {
					additionalInfo.add("Yes");
					this.tempMethod(additionalInfo);
				} else {
					additionalInfo.add("No");
					this.tempMethod(additionalInfo);
				}
			}
			return additionalInfo;
		}	
	}
	
	@Override
	public String getAntiquity(String description) {
		String antiquity = "";
	
		// Connect to the home page
		Document doc = this.getURLDoc(linkList.get(0));
		Element el = doc.select("div:contains(Previous)").last();
		Elements ele = el.select("ul li");
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
	
	public String getConferenceDays(String title, String description) {		
		return this.findConfDays(description);
	}
}