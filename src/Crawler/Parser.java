package crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import venue.Country;

public class Parser {
	protected static ArrayList<String> searchKeywords = new ArrayList<>();
	private static ArrayList<String> searchWords = new ArrayList<>(Arrays.asList("work-in-progress", 
																				"tools", "workshop")); 
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
	private static final String[] COMMITTEES = {"[cC][oO][mM][mM][iI][tT][tT][eE][eE]", 
			"[cC][hH][aA][iI][rR]", "[pP][aA][pP][eE][rR]"};
	
	private static final int MAX_CHARS_IN_DATE = 30;
	
	/**
	 * Parses the title from the home page of the website.
	 * @return title
	 */
	public String getTitle(String homeLink) {
		Document doc = null;
		// Connect to the home page
        doc = this.getURLDoc(homeLink);
		return doc.title();
	}
	
	public String getAcronym(String title) {
		String acronymWithYear = "";
		String acronym = "";
		Pattern pattern = Pattern.compile("[A-Z]+.\\d{4}");
		Matcher matcher;
		// Match the pattern with the title
		matcher = pattern.matcher(title);
		if(matcher.find())
			acronymWithYear = matcher.group(0);
		
		pattern = Pattern.compile("[A-Z]+");
		// Match the pattern with the title
		matcher = pattern.matcher(acronymWithYear);
		if(matcher.find())
			acronym = matcher.group(0);
		
		return acronym;
	}
	
	/**
	 * Extracts the sponsors from the title or the description.
	 * @param title
	 * @param description
	 * @return sponsors
	 */
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
	 * Searches and parses different links to find the proceedings.
	 * @return proceedings
	 */
	public String getProceedings(ArrayList<String> linkList) {
		String proceedings = "";
		this.addLinkKeywords();
		Document doc;
		int keyword = 0;
		while(proceedings == "" && keyword < searchKeywords.size()) {
			String url = this.searchLinks(searchKeywords.get(keyword), linkList);
			if(!url.isEmpty()) {
				doc = this.getURLDoc(url);
				String elementString = doc.select("*p").text();
				for(String sponsor: SPONSORS) {
					if(elementString.matches(changeToRegex(sponsor)))
						if(proceedings.isEmpty())
							proceedings += sponsor;
						else
							proceedings += "/" + sponsor;
				}
			}
			keyword++;
		}
		return proceedings;
	}
	
	/**
	 * Parses the description from the home page 
	 * or the head of the website.
	 * @return description
	 */
	public String getDescription(String homeLink) {
		Document doc = null;
		// Connect to the home page
        doc = this.getURLDoc(homeLink);
		String meta = "";
		try {
			meta = doc.select("meta[name=description]").first().attr("content");
			System.out.println("Decription: " + meta);
		} catch(NullPointerException e) {
		   System.out.println("No meta with attribute \"name\"");
		}
	
		return meta;
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
	public String getVenue(String title, String description, Country country, ArrayList<String> linkList) {
		String venue = "", link, temp;
		Document doc = null;
		// Search the venue website
		link = this.searchLinks("[vV]enue", linkList);
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
	public ArrayList<String> getDeadlines(ArrayList<String> linkList) {
		Document doc = null;
		ArrayList<String> deadlines = new ArrayList<>();
		ArrayList<String> otherSeparated = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\w+.\\s\\d{1,2},\\s\\d{4}");
		String[] separated;
		this.addSearchWords();
		
		String link = this.searchLinks("[iI]mportant", linkList);
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
			for(String toFind: searchKeywords) {
				String found = this.findDeadline(otherSeparated, toFind, pattern);
				deadlines.add(found);
			}
			return deadlines;
		}
	}
	
	/**
	 * Parses information about additional papers, from the /Important_Dates
	 * link (if available) or the home page to see if they can be submitted or not.
	 * @return list containing "Yes/No" + deadlines strings
	 */
	public ArrayList<String> getAdditionalDeadlineInfo(ArrayList<String> linkList) {
		ArrayList<String> additionalInfo = new ArrayList<>();
		ArrayList<String> otherSeparated = new ArrayList<>();
		String[] separated;
		Pattern pattern = Pattern.compile("\\w+.\\s\\d{1,2},\\s\\d{4}");
		Document doc = null;
		// State the keywords you want to search for
		this.addNewSearchWords();
		
		String link = this.searchLinks("[iI]mportant", linkList);
		if(link.isEmpty()) {
			return additionalInfo;
		} else {
			// Connect to the target link
			doc = this.getURLDoc(link);
			// Parse the element to receive a string
			Element el = doc.select("div:contains(Important Dates)").last();
			String html = el.html().replaceAll("\n", "");
			// Search for the keywords in the string
			for(int i = 0; i < searchKeywords.size(); i++) {
				// Overwrite the ArrayList with the submission keywords
				this.addNewSearchWords();
				otherSeparated.clear();
				// Add the appropriate strings based on the evaluation
				if(html.matches(searchKeywords.get(i)))
					additionalInfo.add("Yes");
				else
					additionalInfo.add("No");
				
				// Get the row that contains the search word
				Element row = el.select("tr:contains(" + searchWords.get(i) + ")").first();
				Elements tds = null;
				try {
					// Gets the <td> elements for the next row
					tds = row.nextElementSibling().select("td");
					
					for(Element td: tds) {
						// Split and add the first paragraph to the ArrayList
						if(otherSeparated.isEmpty()) {
							separated = td.toString().split("<br>");
							for(String s: separated) {
								otherSeparated.add(s);
							}	
						} else {
							// Split and add the second paragraph to the ArrayList
							separated = td.toString().split("<br>");
							for(int j = 0; j < separated.length; j++) {
								String newString = otherSeparated.get(j) + separated[j];
								otherSeparated.remove(j);
								otherSeparated.add(j, newString);
							}
						}
					}
				} catch(NullPointerException e) {
					   System.out.println("No <td> for" + searchWords.get(i));
				}

				// Overwrite the ArrayList with the deadline keywords that we are looking for
				this.addSearchWords();
				// Add the appropriate information
				for(String toFind: searchKeywords) {
					String found = findDeadline(otherSeparated, toFind, pattern);
					additionalInfo.add(found);
				}
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
	public String getAntiquity(String description, String homeLink) {
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
	
	/**
	 * Finds the days on which the conference is ongoing.
	 * @param title, description
	 * @return date
	 */
	public String getConferenceDays(String title, String description, String homeLink) {		
		return this.findConfDays(title);
	}
	
	/**
	 * Finds the organizers on the websites.
	 * @param linkList
	 * @param country
	 * @return a map with organizer teams as key and members as value
	 */
	public LinkedHashMap<String, List<String>> getOrganisers(ArrayList<String> linkList, Country country) {
		List<String> potentialLinks = new ArrayList<>();
		// Find the links on the websites that contain the organizers
		this.addCommitteeSearchWords();
		for(String keyword: searchKeywords) {
			String link = this.searchLinks(keyword, linkList);
			if(!link.isEmpty())
				potentialLinks.add(link);
		}
		
		String tempSubteam = "";
		LinkedHashMap<String, List<String>> committees = new LinkedHashMap<>();
		List<String> members = new ArrayList<>();
		
		for(String link: potentialLinks) {
			// Get the document
			Document doc = this.getURLDoc(link);
			// Find all elements and text nodes
			for(Element node: doc.getAllElements()) {
				for(TextNode textNode: node.textNodes()) {
					// Search for committee names in the text node
					if(this.searchForCommittees(textNode.text())) {
						// If the subteam isn't empty and there are members in the list add them to the map to be returned later
						if(!tempSubteam.equals("") && !members.isEmpty()) {
							committees.put(tempSubteam, new ArrayList<String>(members));
							members.clear();
						}
						// Add the subteam found for later use as a key
						tempSubteam = textNode.text();
//						System.out.println("FOUND: " + textNode.text());
//						System.out.println(tempSubteam);
					}
					// Check the string for a country to find if it is a member or not
					else if(this.checkStringForCountry(textNode.text(), country)) {
						// Add the member to the list if a valid subteam is present
						if(!tempSubteam.equals("")) {
							members.add(textNode.text());
						}
//						System.out.println(textNode.text());
//						System.out.println(members);
					}
					// Do nothing with empty strings
					else if(textNode.text().matches("^\\s+$")); 
					else {
						// If it's neither a member or a subteam then everything gathered so far to the map and clear the variables
						if(!tempSubteam.equals("") && !members.isEmpty()) {
							committees.put(tempSubteam, new ArrayList<String>(members));
						}
						members.clear();
						tempSubteam = "";
					}
				}
			}
		}
		
		return committees;
	}
	
	// ------------- HELPER METHODS START HERE -------------
	
	// No workshops, tools or work in progress available on several sites.
	// Therefore, I need to store an empty string 3 times for the
	// output to look nice in excel
	protected void tempMethod(ArrayList<String> additionalInfo) {
		for(int i = 0; i < 3; i++)
			additionalInfo.add("");
	}
	
	private void addLinkKeywords() {
		searchKeywords.clear();
		searchKeywords.add("[sS]ubmissions");
		searchKeywords.add("[pP]roceedings");
		searchKeywords.add("[pP]aper");
	}
	
	/**
	 * Adds search keywords to the ArrayList that will be
	 * used for searching the important dates
	 * i.e. submission, notification, camera
	 */
	protected void addSearchWords() {
		searchKeywords.clear();
		searchKeywords.add(this.changeToRegex("[sS]ubmission"));
		searchKeywords.add(this.changeToRegex("[nN]otification"));
		searchKeywords.add(this.changeToRegex("[cC]amera"));
	}
	
	/**
	 * Overwrites the ArrayList with the "submission, notification and
	 * camera" keywords with new keywords. Used for searching the
	 * website for papers like "Work in progress", "Tools", "Workshops".
	 */
	protected void addNewSearchWords() {
		searchKeywords.clear();
		searchKeywords.add(this.changeToRegex("[wW]ork-[iI]n-[pP]rogress"));
		searchKeywords.add(this.changeToRegex("[tT]ools"));
		searchKeywords.add(this.changeToRegex("[wW]orkshop"));
	}
	
	/**
	 * Adds search keywords to the ArrayList that will be
	 * used for searching the important dates
	 * i.e. submission, notification, camera
	 */
	protected void addCommitteeSearchWords() {
		searchKeywords.clear();
		searchKeywords.add(this.changeToRegex("[oO]rganiz"));
		searchKeywords.add(this.changeToRegex("[pP]rogram"));
		searchKeywords.add(this.changeToRegex("[pP]eople"));
		searchKeywords.add(this.changeToRegex("[cC]ommittee"));
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
	 * Checks whether a string contains a country.
	 * @param string
	 * @param country
	 * @return true/false
	 */
	private boolean checkStringForCountry(String string, Country country) {
		String countryRegex;
		// Go through the list of countries
		for(String countryName: country.getCountries()) {
			countryRegex = changeToRegex(countryName);
			// Check if the string contains the country name
			if(string.matches(countryRegex))
				return true;
		}
		
		return false;
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
	private String searchLinks(String keyword, ArrayList<String> linkList) {
		String answer = "";
		keyword = this.changeToRegex(keyword);
		
		// Find the link in the list of links that contains the keyword if possible
		for(String link: linkList) {
			System.out.println("LINK: " + link + "     keyword: " + keyword);
			if(link.matches(keyword))
				return link;
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
		String found = "";
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
		String found = "";
		Matcher matcher;
		for(String string: separated) {
			if(string.matches(toFind)) {
//				System.out.println("Found: " + string);
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
	
	
	/**
	 * Looks through the string to find a date.
	 * @param toCheck
	 * @return date or empty string
	 */
	protected String findConfDays(String toCheck) {		
		Pattern pattern = Pattern.compile("\\d+-\\d+.+\\w+.+\\d{4}|(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?).+\\d+-\\d+.+\\d{4}");
		Matcher matcher;
		// Match the pattern with the description
		matcher = pattern.matcher(toCheck);
		if(matcher.find()) {
			if(matcher.group(0).length() < MAX_CHARS_IN_DATE)
				return matcher.group(0);
			else
				return "";
		} else {
			return "";
		}
	}
	
	/**
	 * Receives a string and checks whether it contains a committee or chair keyword.
	 * @param string
	 * @return true/false
	 */
	private boolean searchForCommittees(String string) {
		String subteamRegex;
		for(String subteam: COMMITTEES) {
			subteamRegex = this.changeToRegex(subteam);
			if(string.matches(subteamRegex))
				return true;
		}
		return false;
	}
}
