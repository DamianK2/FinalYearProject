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
	
	@Override
	public ArrayList<String> getDeadlines() {
		Document doc = null;
		ArrayList<String> deadlines = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\w+.\\s\\d{1,2},\\s\\d{4}");
		String[] separated;
		doc = this.getURLDoc(linkList.get(0));
		// Select the div with "Important Dates"
		Element el = doc.select("div:contains(Important Dates)").last();
		
		// Extract the paragraph
		String elementString = el.select("p").toString();
		if(!elementString.isEmpty()) {
			// Split into multiple lines on seeing the <br> tag
			separated = elementString.split("<br> ");
			// Search for the deadlines
			for(String toFind: searchDeadlines) {
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
		
		doc = this.getURLDoc(linkList.get(0));
		Element el = doc.select("div:contains(Important Dates)").last();
		
		String elementString = el.select("p").toString();
		if(elementString.isEmpty()) {
			return additionalInfo;
		} else {
			elementString = elementString.replaceAll("\n", "");
			for(String keyword: searchDeadlines) {
				if(elementString.matches(keyword))
					additionalInfo.add("Yes");
				else
					additionalInfo.add("No");
			}
			return additionalInfo;
		}	
	}
	
	@Override
	public String getAntiquity(String description) {
		String antiquity = "";
	
		Document doc = this.getURLDoc(linkList.get(0));
		Element el = doc.select("div:contains(Previous)").last();
		Elements ele = el.select("ul li");
		if(ele.isEmpty())
			return antiquity;
		else {
			int currentYear = 1;
			currentYear += ele.size();
			antiquity = this.toOrdinal(currentYear);
			
			return antiquity;
		}
	}
}
