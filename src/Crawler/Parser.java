package Crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
	private static final ArrayList<String> SPECIAL_CASES = new ArrayList<>(
															Arrays.asList(
															"Zeroth","First", "Second", "Third", 
															"Fourth", "Fifth", "Sixth", "Seventh", 
															"Eighth", "Ninth", "Tenth", "Eleventh", 
															"Twelfth", "Thirteenth", "Fourteenth", 
															"Fifteenth", "Sixteenth", "Seventeenth", 
															"Eighteenth", "Nineteenth"));
	private static final ArrayList<String> TENS = new ArrayList<>(
										Arrays.asList("Twent", "Thirt", "Fort", "Fift", 
										"Sixt", "Sevent", "Eight", "Ninet"));
	protected static final ArrayList<String> SPONSORS = new ArrayList<>(Arrays.asList("ACM", "SPEC", 
																		"UNESCO", "Springer", "IEEE"));
	
	public Parser(ArrayList<String> links) {
		linkList = links;		
		this.addSearchWords();
	}
	
	/**
	 * Adds search keywords to the ArrayList that will be
	 * used for searching the important dates
	 * i.e. submission, notification, camera
	 */
	private void addSearchWords() {
		searchDeadlines.clear();
		searchDeadlines.add(this.changeToRegex("[sS]ubmission"));
		searchDeadlines.add(this.changeToRegex("[nN]otification"));
		searchDeadlines.add(this.changeToRegex("[cC]amera"));
	}
	
	/**
	 * Parses the title from the home page of the website.
	 * @return title
	 */
	public String getTitle() {
		Document doc = null;
		// Connect to the home page
        doc = this.getURLDoc(linkList.get(0));
		return doc.title();
	}
	
	public String getSponsors(String title, String description) {
		String sponsors = "";
		
		for(String sponsor: SPONSORS) {
			if(title.matches(this.changeToRegex(sponsor)))
				if(sponsors.isEmpty())
					sponsors += sponsor;
				else
					sponsors += "/" + sponsor;
		}
		
		return sponsors;
	}
	
	/**
	 * Parses the description from the home page 
	 * or the head of the website
	 * @return description
	 */
	public String getDescription() {
		Document doc = null;
		// Connect to the home page
        doc = this.getURLDoc(linkList.get(0));
		String meta = "", parsedMeta = "";
		try {
			parsedMeta = doc.select("meta[name=description]").first().attr("content");
			// Limit the string to 100 characters
			//parsedMeta = meta.replaceAll("(.{100})", "$1\n");
			System.out.println("Decription: " + parsedMeta);
		} catch(NullPointerException e) {
		   System.out.println("No meta with attribute \"name\"");
		}
	
		return parsedMeta;
	}
	
	/**
	 * Parses the venue from the /venue link (if available), 
	 * the title(if available), description(if available) 
	 * or header(if available).
	 * @param title
	 * @param description
	 * @param country
	 * @return venue
	 */
	public String getVenue(String title, String description, Country country) {
		String venue = "", link, temp;
		Document doc = null;
		// Search the venue website
		link = this.searchLinks("[vV]enue");
		if(!link.equals("")) {
			// Connect to the target link
			doc = this.getURLDoc(link);
			// Select the paragraphs from the website
			temp = doc.select("*p").text();
			if(!temp.equals(""))
				venue = this.searchCountries(temp, country);
		}
		
		return venue;
	}
	
	/**
	 * Parses the important deadlines from the /Important_Dates
	 * link (if available) or the home page and 
	 * returns a list in the format (month dd, yyyy).
	 * @return list of deadlines
	 */
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
	
	/**
	 * Parses information about additional papers, from the /Important_Dates
	 * link (if available) or the home page to see if they can be submitted or not.
	 * @return list containing "Yes/No" strings
	 */
	public ArrayList<String> getAdditionalDeadlineInfo() {
		ArrayList<String> additionalInfo = new ArrayList<>();
		Document doc = null;
		this.addNewSearchWords();
		
		String link = this.searchLinks("[iI]mportant");
		if(link.isEmpty()) {
			return additionalInfo;
		} else {
			// Connect to the target link
			doc = this.getURLDoc(link);
			// Parse the elements to receive a string
			Element el = doc.select("div:contains(Important Dates)").last();
			String html = el.html();
			html = html.replaceAll("\n", "");
			// Search for the keywords in the string
			for(String keyword: searchDeadlines) {
				if(html.matches(keyword))
					additionalInfo.add("Yes");
				else
					additionalInfo.add("No");
			}
			return additionalInfo;
		}
		
	}
	
	/**
	 * Checks the title for the conference year (if available).
	 * @param title
	 * @return conference year
	 */
	public String getConferenceYear(String title) {
		String year = "";
		Pattern pattern = Pattern.compile("\\d{4}");
		Matcher matcher;
		// Match the pattern with the title
		matcher = pattern.matcher(title);
		if(matcher.find())
			year = matcher.group(0);
		return year;
	}
	
	/**
	 * Checks the description or hompage for the antiquity of the conference.
	 * @param description
	 * @return antiquity
	 */
	public String getAntiquity(String description) {
		String antiquity = "";
		Pattern pattern = Pattern.compile("\\d{1,2}(?:st|nd|rd|th)|\\w+(?:st|nd|rd|th)|\\w+-\\w+(?:st|nd|rd|th)");
		Matcher matcher;
		// Match the pattern with the description
		matcher = pattern.matcher(description);
		if(matcher.find()) {
			antiquity = matcher.group(0);
			// If the string is in the format of "1st, 2nd, 3rd" etc.
			// Change it to its ordinal form
			if(antiquity.matches("\\d{1,2}(?:st|nd|rd|th)")) {
				String[] number = antiquity.split("(?:st|nd|rd|th)");
				antiquity = this.toOrdinal(Integer.parseInt(number[0]));
			}
		}
		
		return antiquity;
	}
	
	// ------------- HELPER METHODS START HERE -------------
	/**
	 * Overwrites the ArrayList with the "submission, notification and
	 * camera" keywords with new keywords. Used for searching the
	 * website for papers like "Work in progress", "Tools", "Workshops".
	 */
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
			return SPECIAL_CASES.get(number);
		else if(number % 10 == 0)
			return TENS.get(number/10 - 2) + "ieth";
		else
			return TENS.get(number/10 - 2) + "y-" + SPECIAL_CASES.get(number % 10);
	}
	
	/**
	 * Finds a country name using the given string.
	 * @param string
	 * @param country
	 * @return venue
	 */
	protected String searchCountries(String string, Country country) {
		String venue = "", countryRegex;
		// Go through the list of countries
		for(String countryName: country.getCountries()) {
			countryRegex = this.changeToRegex(countryName);
			// Check if the string contains the country name
			if(string.matches(countryRegex))
				venue = countryName;
		}
		return venue;
	}
	
	/**
	 * Receives a keyword that we want to search for
	 * and adds ".*" around it.
	 * @param keyword
	 * @return regex
	 */
	protected String changeToRegex(String keyword) {
		keyword = ".*" + keyword +".*";
		return keyword;
	}

	/**
	 * Receives a keyword that we want to find in the link
	 * and returns the link if it has been found in the list
	 * of links.
	 * e.g. passing in "venue" can return a link like
	 * www.example.com/venue/
	 * @param keyword
	 * @return link
	 */
	private String searchLinks(String keyword) {
		String answer = "";
		keyword = this.changeToRegex(keyword);
		
		// Find the link in the list of links that contains the keyword if possible
		for(String link: linkList) {
			if(link.matches(keyword))
				answer = link;
		}
		
		return answer;
	}
	
	/**
	 * Receives the URL to connect to and after successfully 
	 * connecting, returns the document object.
	 * @param url
	 * @return document
	 */
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
	
	/**
	 * Finds the deadline from the received parameters.
	 * Returns "N/A" if no match is found.
	 * @param separated
	 * @param toFind
	 * @param pattern
	 * @return deadline
	 */
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
	
	/**
	 * Finds the deadline from the received parameters.
	 * Returns "N/A" if no match is found.
	 * @param separated
	 * @param toFind
	 * @param pattern
	 * @return deadline
	 */
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
