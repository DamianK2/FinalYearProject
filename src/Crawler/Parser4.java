package Crawler;

import java.util.ArrayList;

import org.jsoup.nodes.Document;

import venue.Country;

public class Parser4 extends Parser {

	public Parser4(ArrayList<String> links) {
		super(links);
	}
	
	@Override
	public String getVenue(String title, String description, Country country) {
		String venue = "", header;
		Document doc = null;
        doc = this.getURLDoc(linkList.get(0));
		header = doc.select("div#header").text();
		
		// Search the target div header on the home website for the country of the conference
		if(!header.equals(""))
			venue = this.searchCountries(header, country);
		
		return venue;
	}

}
