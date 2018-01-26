package Crawler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import venue.Country;

public class Parser3 extends Parser {

	@Override
	public String getDescription(String homeLink) {
		Document doc = null;
        doc = this.getURLDoc(homeLink);
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
	public String getVenue(String title, String description, Country country, ArrayList<String> linkList) {
		String venue = "";
		
		// Search the description for the country of the conference
		if(!description.equals(""))
			venue = this.searchCountries(description, country);
		
		return venue;
	}
	
	@Override
	public ArrayList<String> getDeadlines(ArrayList<String> linkList) {
		Document doc = null;
		ArrayList<String> deadlines = new ArrayList<>();
		ArrayList<String> deadlinesEmpty = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\w+.\\s\\d{1,2},\\s\\d{4}");
		String[] separated;
		this.addSearchWords();
		
		// Connect to the home page
		doc = this.getURLDoc(linkList.get(0));
		// Select the div with "Important Dates"
		Element el = doc.select("div:contains(Important Dates)").last();
		
		// Counter to see how many empty spaces are returned
		int count = 1;
		// Extract the list
		String elementString = el.select("ul li").toString();
		if(!elementString.isEmpty()) {
			// Split into multiple lines on seeing the new line
			separated = elementString.split("\n");
			// Search for the deadlines
			for(String toFind: searchKeywords) {
				String found = this.findDeadline(separated, toFind, pattern);
				deadlines.add(found);
				if(found.equals(""))
					count++;
			}
		}
		
		return count < 3 ? deadlines : deadlinesEmpty;
	}
	
	@Override
	public ArrayList<String> getAdditionalDeadlineInfo(ArrayList<String> linkList) {
		ArrayList<String> additionalInfo = new ArrayList<>();
		Document doc = null;
		this.addNewSearchWords();
		
		// Connect to the home page
		doc = this.getURLDoc(linkList.get(0));
		Element el = doc.select("div:contains(Important Dates)").last();

		
		// Get the content of the list (if available)
		String elementString = el.select("ul li").toString();
		elementString = elementString.replaceAll("\n|\r", "");
		
		if(elementString.isEmpty()) {
			return additionalInfo;
		} else if(!elementString.matches(".*\\d+.+(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|[mM][aA][yY]"
				+ "|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?).+\\d{4}.*"
				+ "|.*(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|[mM][aA][yY]|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?"
				+ "|Oct(ober)?|Nov(ember)?|Dec(ember)?)(.+\\d+\\d{4}|.+\\d+,.+\\d{4}).*")) {
			System.out.println("here!!!");
			return additionalInfo;
		} else {
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
	
	public String getConferenceDays(String title, String description, String homeLink) {		
		Document doc = this.getURLDoc(homeLink);
		return this.findConfDays(doc.select("#header").text());
	}
}
