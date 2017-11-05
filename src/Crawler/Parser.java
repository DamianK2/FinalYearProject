package Crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import venue.Country;

public class Parser {
	private static ArrayList<String> linkList = new ArrayList<>();
	private static ArrayList<String> searchDeadlines = new ArrayList<>();
	
	public Parser(ArrayList<String> links) {
		linkList = links;		
		searchDeadlines.clear();
		searchDeadlines.add(".*[sS]ubmission.*");
		searchDeadlines.add(".*[nN]otification.*");
		searchDeadlines.add(".*[cC]amera.*");
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
			check = true;
			try {
				meta = doc.select(".site-description").text();
				parsedMeta = meta.replaceAll("(.{100})", "$1\n");
				System.out.println("Description: " + parsedMeta);
			} catch(NullPointerException e) {
				System.out.println("No class with name \"site-description\"");
				check = false;
			}
			if(parsedMeta.equals("")) {
				System.out.println("No class with name \"site-description\"");
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
		link = this.searchLinks("[vV]enue");
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
	
	public ArrayList<String> getDeadlines() {
		Document doc = null;
		ArrayList<String> deadlines = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\w+.\\s\\d{1,2},\\s\\d{4}");
		String[] separated;
		String link = this.searchLinks("[iI]mportant");
		if(!link.isEmpty()) {
			ArrayList<String> otherSeparated = new ArrayList<>();
			doc = this.getURLDoc(link);
			Element el = doc.select("table").last();
			Element row = el.select("tr").get(1);
//			for(Element row: el.select("tr")) {
//			otherSeparated.clear();
			Elements tds = row.select("td");
			
			for(Element td: tds) {
				String selectedHtml = td.select("p").html();
				if(!selectedHtml.isEmpty() && otherSeparated.isEmpty()) {
					separated = selectedHtml.split("<br>");
					for(String s: separated) {
						otherSeparated.add(s);
					}	
				} else if(!selectedHtml.isEmpty()) {
					separated = selectedHtml.split("<br>");
					for(int i = 0; i < separated.length; i++) {
						String newString = otherSeparated.get(i) + separated[i];
						otherSeparated.remove(i);
						otherSeparated.add(i, newString);
					}
				}
				
			}

			for(String toFind: searchDeadlines) {
				String found = this.findDeadline(otherSeparated, toFind, pattern);
				deadlines.add(found);
			}
		} else {
	        doc = this.getURLDoc(linkList.get(0));
			Element el = doc.select("div:contains(Important Dates)").last();
			
			String elementString = el.select("p").toString();
			if(!elementString.isEmpty()) {
				this.removeFromString(elementString, "<p>");
				this.removeFromString(elementString, "</p>");
				separated = elementString.split("<br> ");
				for(String toFind: searchDeadlines) {
					String found = this.findDeadline(separated, toFind, pattern);
					deadlines.add(found);
				}
			}
			
			elementString = el.select("ul li").toString();
			if(!elementString.isEmpty()) {
				this.removeFromString(elementString, "<li>");
				this.removeFromString(elementString, "</li>");
				separated = elementString.split("\n");
				for(String toFind: searchDeadlines) {
					String found = this.findDeadline(separated, toFind, pattern);
					deadlines.add(found);
				}
			}
		}
		
		return deadlines;
	}
	
	public String getConferenceYear(String title) {
		String year = "";
		Pattern pattern = Pattern.compile("\\d{4}");
		Matcher matcher;
		matcher = pattern.matcher(title);
		if(matcher.find())
			year = matcher.group(0);
		return year;
	}
	
	public String getAntiquity(String description) {
		String antiquity = "";
		Pattern pattern = Pattern.compile("\\d{1,2}(?:st|nd|rd|th)|\\w+(?:st|nd|rd|th)|\\w+-\\w+(?:st|nd|rd|th)");
		Matcher matcher;
		matcher = pattern.matcher(description);
		if(matcher.find())
			antiquity = matcher.group(0);
		
		if(antiquity.equals("")) {
			Document doc = this.getURLDoc(linkList.get(0));
			Element el = doc.select("div:contains(Previous)").last();
			Elements ele = el.select("ul li");
			int currentYear = 1;
			currentYear += ele.size();
			antiquity = Integer.toString(currentYear);
		}
		
		return antiquity;
	}
	
	// Helper methods start here
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
	
	private String findDeadline(String[] separated, String toFind, Pattern pattern) {
		String found = "N/A";
		Matcher matcher;
		for(String string: separated) {
			if(string.matches(toFind)) {
				//System.out.println("Found: " + string);
				matcher = pattern.matcher(string);
				
				if(matcher.find())
					found = matcher.group(0);
				break;
			}
		}
		return found;
	}
	
	private String findDeadline(ArrayList<String> separated, String toFind, Pattern pattern) {
		String found = "N/A";
		Matcher matcher;
		for(String string: separated) {
			if(string.matches(toFind)) {
				System.out.println("Found: " + string);
				matcher = pattern.matcher(string);
				
				if(matcher.find())
					found = matcher.group(0);
				if(matcher.find())
					found = matcher.group(0);		
	
				break;
			}
		}
		return found;
	}
	
	//TODO not working
	private void removeFromString(String fromString, String remove) {
		fromString.replaceAll(remove, "");
	}
}
