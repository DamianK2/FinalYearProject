package crawler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import venue.Country;

public class Parser2 extends Parser {
	
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
	public String getDescription(String homeLink) {
		Document doc = null;
		// Connect to the home page
        doc = this.getURLDoc(homeLink);
		String meta = "";
		try {
			meta = doc.select("meta[property=og:description]").first().attr("content");
			System.out.println("Description: " + meta);
		} catch(NullPointerException e) {
			System.out.println("No meta with attribute \"property\"");
		}
		return meta;
	}
	
	@Override
	public String getVenue(String title, String description, Country country, ArrayList<String> linkList) {
		String venue = "";
		// Search the title for the country of the conference
		if(!title.equals(""))
			venue = this.searchCountries(title, country);
		
		return venue;
	}
	
	@Override
	public ArrayList<String> getDeadlines(ArrayList<String> linkList) {
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
		String elementString = el.select("p").toString().replaceAll("\r|\n", "");
		elementString = elementString.replaceAll("<strike>(.*?|.*\\n.*\\n)<\\/strike>|<del>(.*?|.*\\n.*\\n)<\\/del>", "");
		
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
	public ArrayList<String> getAdditionalDeadlineInfo(ArrayList<String> linkList) {
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
	public String getAntiquity(String description, String homeLink) {
		String antiquity = "";
	
		// Connect to the home page
		Document doc = this.getURLDoc(homeLink);
		Elements ele = null;
		try {
			Element el = doc.select("div:contains(Previous)").last();
			ele = el.select("ul li");
		} catch(NullPointerException e) {
			return antiquity;
		}
		
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
	
	public String getConferenceDays(String title, String description, String homeLink) {		
		return this.findConfDays(description);
	}
}
