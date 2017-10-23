package Crawler;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import venue.Country;

public class Parser {
	private static ArrayList<String> linkList = new ArrayList<>();
	
	public Parser(ArrayList<String> links) {
		linkList = links;
	}
	
	// Get the website's title
	public String getTitle() {
		Document doc = null;
        doc = this.getURLDoc(linkList.get(0));
		return doc.title();
	}
	
	// Get the website's description
	public String getDescription() {
		Document doc = null;
        doc = this.getURLDoc(linkList.get(0));
		boolean check = true;
		String meta = "", parsedMeta = "";
		try {
			meta = doc.select("meta[name=description]").first().attr("content");
			parsedMeta = meta.replaceAll("(.{100})", "$1\n");
			System.out.println("Decription: " + parsedMeta);
		} catch(NullPointerException e) {
		   System.out.println("No meta with attribute \"name\"");
		   check = false;
		}
		if(!check) {
			check = true;
			try {
				meta = doc.select("meta[property=og:description]").first().attr("content");
				parsedMeta = meta.replaceAll("(.{100})", "$1\n");
				System.out.println("Description: " + parsedMeta);
			} catch(NullPointerException e) {
				System.out.println("No meta with attribute \"property\"");
				check = false;
			}
		}
		if(!check) {
			System.out.println("Well this is embarassing. No description found!");
			meta = "No description!";
		}
		
		return parsedMeta;
	}
	
	public String getVenue(String title, String description, Country country) {
		String venue = "", link, temp;
		boolean found = false;
		Document doc = null;
		//search the venue website
		link = this.searchLinks("venue");
		if(!link.equals("")) {
			doc = this.getURLDoc(link);
			temp = doc.select("*p").text();
			if(!temp.equals("")) {
				venue = this.searchCountries(temp, country);
				if(!venue.equals(""))
					found = true;
			}
		}
			
		// if not there search the title
		if(!found) {
			if(!title.equals("")) {
				venue = this.searchCountries(title, country);
				if(!venue.equals(""))
					found = true;
			}
		}
		
		// if not there search the description
		if(!found) {
			if(!description.equals("")) {
				venue = this.searchCountries(description, country);
				if(!venue.equals(""))
					found = true;
			}
		}

		// if not there target div header on home website
		if(!found) {
			temp = doc.select("div#header").text();
			if(!temp.equals("")) {
				venue = this.searchCountries(temp, country);
				if(!venue.equals(""))
					found = true;
			}
		}
		
		return venue;
	}
	
	private String searchCountries(String string, Country country) {
		String venue = "", countryRegex;
		for(String countryName: country.getCountries()) {
			countryRegex = this.changeToRegex(countryName);
			if(string.matches(countryRegex))
				venue = countryName;
		}
		return venue;
	}
	
	private String changeToRegex(String keyword) {
		keyword = ".*" + keyword +".*";
		return keyword;
	}

	private String searchLinks(String keyword) {
		String answer = "";
		keyword = this.changeToRegex(keyword);
		
		for(String link: linkList) {
			if(link.matches(keyword))
				answer = link;
		}
		
		return answer;
	}
	
	private Document getURLDoc(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
			System.out.println("(inside Parser)Fetching from " + url + "...");
		} catch (IOException e) {
			System.out.println("Something went wrong when getting the first element from the list of links.");
			e.printStackTrace();
		}
		return doc;
	}
}
