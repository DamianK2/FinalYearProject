package Crawler;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import venue.Country;

public class Parser3 extends Parser {

	public Parser3(ArrayList<String> links) {
		super(links);
	}
	
	@Override
	public String getDescription() {
		Document doc = null;
        doc = this.getURLDoc(linkList.get(0));
		String meta = "", parsedMeta = "";
		try {
			parsedMeta = doc.select(".site-description").text();
			// Limit the string to 100 characters
			//parsedMeta = meta.replaceAll("(.{100})", "$1\n");
			System.out.println("Description: " + parsedMeta);
		} catch(NullPointerException e) {
			System.out.println("No class with name \"site-description\"");
		}
		return parsedMeta;
	}

	@Override
	public String getVenue(String title, String description, Country country) {
		String venue = "";
		
		// Search the description for the country of the conference
		if(!description.equals(""))
			venue = this.searchCountries(description, country);
		
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
		
		// Extract the list
		String elementString = el.select("ul li").toString();
		if(!elementString.isEmpty()) {
			// Split into multiple lines on seeing the new line
			separated = elementString.split("\n");
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
		
		// Get the content of the list (if available)
		String elementString = el.select("ul li").toString();
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
}
