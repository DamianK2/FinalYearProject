package Crawler;

import java.util.ArrayList;

import org.jsoup.nodes.Document;

import venue.Country;

public class Parser2 extends Parser {

	public Parser2(ArrayList<String> links) {
		super(links);
	}
	
	@Override
	public String getDescription() {
		Document doc = null;
        doc = this.getURLDoc(linkList.get(0));
		String meta = "", parsedMeta = "";
		try {
			meta = doc.select("meta[property=og:description]").first().attr("content");
			// Limit the string to 100 characters
			parsedMeta = meta.replaceAll("(.{100})", "$1\n");
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
}
