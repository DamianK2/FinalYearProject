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
	protected static ArrayList<String> linkList = new ArrayList<>();
	protected static ArrayList<String> searchDeadlines = new ArrayList<>();
	private static final String[] SPECIAL_CASES = {"Zeroth","First", "Second", "Third", 
												"Fourth", "Fifth", "Sixth", "Seventh", 
												"Eighth", "Ninth", "Tenth", "Eleventh", 
												"Twelfth", "Thirteenth", "Fourteenth", 
												"Fifteenth", "Sixteenth", "Seventeenth", 
												"Eighteenth", "Nineteenth"};
	private static final String[] TENS = {"Twent", "Thirt", "Fort", "Fift", 
										"Sixt", "Sevent", "Eight", "Ninet"};
	
	public Parser(ArrayList<String> links) {
		linkList = links;		
		this.addSearchWords();
	}
	
	private void addSearchWords() {
		searchDeadlines.clear();
		searchDeadlines.add(this.changeToRegex("[sS]ubmission"));
		searchDeadlines.add(this.changeToRegex("[nN]otification"));
		searchDeadlines.add(this.changeToRegex("[cC]amera"));
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
		String meta = "", parsedMeta = "";
		try {
			meta = doc.select("meta[name=description]").first().attr("content");
			// Limit the string to 100 characters
			parsedMeta = meta.replaceAll("(.{100})", "$1\n");
			System.out.println("Decription: " + parsedMeta);
		} catch(NullPointerException e) {
		   System.out.println("No meta with attribute \"name\"");
		}
//		if(!check) {
//			System.out.println("Well this is embarassing. No description found!");
//			meta = "No description!";
//		}
		
		return parsedMeta;
	}
	
	public String getVenue(String title, String description, Country country) {
		String venue = "", link, temp;
		Document doc = null;
		// Search the venue website
		link = this.searchLinks("[vV]enue");
		if(!link.equals("")) {
			doc = this.getURLDoc(link);
			temp = doc.select("*p").text();
			if(!temp.equals(""))
				venue = this.searchCountries(temp, country);
		}
		
		return venue;
	}
	
	public ArrayList<String> getDeadlines() {
		Document doc = null;
		ArrayList<String> deadlines = new ArrayList<>();
		ArrayList<String> otherSeparated = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\w+.\\s\\d{1,2},\\s\\d{4}");
		String[] separated;
		
		String link = this.searchLinks("[iI]mportant");
		if(link.isEmpty())
			return deadlines;
		else {
			doc = this.getURLDoc(link);
			
			// Selects the last table on the website
			Element el = doc.select("table").last();
			// Gets the first row from the table
			Element row = el.select("tr").get(1);
			// Gets the <td> elements from the row
			Elements tds = row.select("td");
			
			for(Element td: tds) {
				// Extract the paragraph from the <td> element
				String selectedHtml = td.select("p").html();
				// Split and add the first paragraph to the ArrayList
				if(!selectedHtml.isEmpty() && otherSeparated.isEmpty()) {
					separated = selectedHtml.split("<br>");
					for(String s: separated) {
						otherSeparated.add(s);
					}	
				} else if(!selectedHtml.isEmpty()) {
					// Split and add the second paragraph to the ArrayList
					separated = selectedHtml.split("<br>");
					for(int i = 0; i < separated.length; i++) {
						String newString = otherSeparated.get(i) + separated[i];
						otherSeparated.remove(i);
						otherSeparated.add(i, newString);
					}
				}
				
			}

			// Search for the deadlines in the above extracted information
			for(String toFind: searchDeadlines) {
				String found = this.findDeadline(otherSeparated, toFind, pattern);
				deadlines.add(found);
			}
			return deadlines;
		}
	}
	
	public ArrayList<String> getAdditionalDeadlineInfo() {
		ArrayList<String> additionalInfo = new ArrayList<>();
		Document doc = null;
		this.addNewSearchWords();
		
		String link = this.searchLinks("[iI]mportant");
		if(link.isEmpty()) {
			return additionalInfo;
		} else {
			doc = this.getURLDoc(link);
			Element el = doc.select("div:contains(Important Dates)").last();
			String html = el.html();
			html = html.replaceAll("\n", "");
			for(String keyword: searchDeadlines) {
				if(html.matches(keyword))
					additionalInfo.add("Yes");
				else
					additionalInfo.add("No");
			}
			return additionalInfo;
		}
		
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
		if(matcher.find()) {
			antiquity = matcher.group(0);
			if(antiquity.matches("\\d{1,2}(?:st|nd|rd|th)")) {
				String[] number = antiquity.split("(?:st|nd|rd|th)");
				antiquity = this.toOrdinal(Integer.parseInt(number[0]));
			}
		}
		
		return antiquity;
	}
	
	// Helper methods start here
	protected void addNewSearchWords() {
		searchDeadlines.clear();
		searchDeadlines.add(this.changeToRegex("[wW]ork-[iI]n-[pP]rogress"));
		searchDeadlines.add(this.changeToRegex("[tT]ools"));
		searchDeadlines.add(this.changeToRegex("[wW]orkshop"));
	}
	
	/**
	 * Changes an integer to its ordinal.
	 * @param number
	 * @return ordinal
	 */
	protected String toOrdinal(int number) {
		if(number < 20)
			return SPECIAL_CASES[number];
		else if(number % 10 == 0)
			return TENS[(number/10 - 2)] + "ieth";
		else
			return TENS[(number/10) - 2] + "y-" + SPECIAL_CASES[number % 10];
	}
	
	protected String searchCountries(String string, Country country) {
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
	
	protected Document getURLDoc(String url) {
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
	
	protected String findDeadline(String[] separated, String toFind, Pattern pattern) {
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
}
