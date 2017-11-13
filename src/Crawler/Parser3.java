package Crawler;

import java.util.ArrayList;

import org.jsoup.nodes.Document;

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
			meta = doc.select(".site-description").text();
			// Limit the string to 100 characters
			parsedMeta = meta.replaceAll("(.{100})", "$1\n");
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
}
