package Crawler;

import java.util.ArrayList;

import org.jsoup.nodes.Document;

import venue.Country;

public class Parser4 extends Parser {

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
	
	public String getConferenceDays(String title, String description, String homeLink) {		
		Document doc = this.getURLDoc(homeLink);
		return this.findConfDays(doc.select("p").text());
	}

}
