package crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import database.Information;
import venue.Country;

public class Parser {
	protected static Information information;
	protected static Crawler crawler;
	// Synchronize the list to avoid concurrent modification
	protected static List<String> searchKeywords = Collections.synchronizedList(new ArrayList<>());
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
	private static final int MAX_CHARS_IN_DATE = 45;
	protected static String acronymPattern = "([A-Z]{3,}.[A-Z]{1,}|[A-Z]{3,})";
	protected static String acronymYearPattern = "([A-Z]+.[A-Z]+|[A-Z]+)('|\\s)(\\d{4}|\\d{2})";
	private static String confDaysPattern = "\\d+\\s*?(-|–)\\s*?\\d+.+\\w+.\\d{4}|(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|"
			+ "Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?).+?\\d{1,2}.+?(-|–|to\\s)\\d{1,2}.+?\\d{4}"
			+ "|(Mon(day)?|Tue(sday)?|Wed(nesday)?|Thu(rsday)?|Fri(day)?|Sat(urday)?|Sun(day)?)\\s\\d{1,2}.+?to.+?\\d{1,2}.+?"
			+ "(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|"
			+ "Dec(ember)?)\\s\\d{4}|(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?"
			+ "|Nov(ember)?|Dec(ember)?).+\\d{1,2}(-|\\s–\\s)\\d{1,2}.+?\\d{4}|(Mon(day)?|Tue(sday)?|Wed(nesday)?|Thu(rsday)?|Fri(day)?|"
			+ "Sat(urday)?|Sun(day)?)(\\s+?|,\\s+?).+?\\d{1,2}\\s+?(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|"
			+ "Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)\\s+?\\d{4}|(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|"
			+ "Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)\\s\\d{1,2}-\\w+\\s\\d{1,2},\\s\\d{4}";
	static Logger logger = LogManager.getLogger(Parser.class);
	static Logger info_logger = LogManager.getLogger("information_log");
	
	public Parser(Information info, Crawler c) {
		information = info;
		crawler = c;
	}
	
	/**
	 * Extracts the title from the home page of the website.
	 * @return title
	 */
	public String getTitle(Document doc) {
		return doc.title();
	}
	
	/**
	 * Finds the acronym of the conference from the title
	 * @param title
	 * @return acronym or empty string
	 */
	public String getAcronym(String title, String description) {
		String found = this.findAcronym(title);
		logger.debug("Found acronym \"" + found + "\" from title");
		return found;
	}
	
	/**
	 * Extracts the sponsors from the title or the description.
	 * @param title
	 * @param description
	 * @return sponsors
	 */
	public String getSponsors(String title, String description) {
		String sponsors = "";
		
		for(String sponsor: information.getSponsors()) {
			if(title.matches(this.changeToRegex(sponsor)))
				if(sponsors.isEmpty())
					sponsors += sponsor;
				else
					sponsors += "/" + sponsor;	//There can be more than 1 sponsor
		}
		logger.debug("Found sponsors" + sponsors + "\" from title");

		return sponsors;
	}
	
	/**
	 * Searches and parses different links to find the proceedings.
	 * @return proceedings
	 */
	public String getProceedings(Document doc) {
		if(doc == null) 
			return "";
		
		// Use SB to concatenate many proceedings
		StringBuilder proceedings = new StringBuilder();
			
		// Get all the elements
		for(Element e: doc.getAllElements()) {
			String elementString = e.wholeText();
			// Find the proceedings in the text
			for(String proc: information.getProceedings()) {
				// Make sure it has a space in front of it
				if(elementString.matches(changeToRegex(" " + proc)))
					if(proceedings.toString().isEmpty())
						proceedings.append(proc);
					else if(!proceedings.toString().contains(proc))
						proceedings.append("/" + proc);
			}
		}
		
		return proceedings.toString();
	}
	
	/**
	 * Find links in the passed in list that can contain proceedings
	 * @param linkList
	 * @return list of potential proceeding links
	 */
	public synchronized ArrayList<String> findProceedingLinks(ArrayList<String> linkList) {
		ArrayList<String> proceedingLinks = new ArrayList<>();
		this.addLinkKeywords();
		synchronized(searchKeywords) {
			for(String keyword: searchKeywords) {
				String url = this.searchLinks(keyword, linkList);
				if(!url.isEmpty())
					proceedingLinks.add(url);
			}
		}
		return proceedingLinks;
	}
	
	/**
	 * Parses the description from the home page 
	 * or the head of the website.
	 * @return description
	 */
	public String getDescription(Document doc) {
		String meta = "";
		try {
			meta = doc.select("meta[name=description]").first().attr("content");
			return meta;
		} catch(NullPointerException e) {
			return "";
		}
	}
	
	/**
	 * Parses the venue from the /venue link (if available), 
	 * the title(if available), description(if available) 
	 * or header(if available).
	 * @param title
	 * @param description
	 * @param country
	 * @return venue or empty string
	 */
	public String getVenue(String title, String description, Country country, Document doc) {
		if(doc == null)
			return "";
		
		String venue = "";
		// Go through all the elements in the document
		for(Element e: doc.getAllElements()) {
			// Extract text nodes
			for(TextNode textNode: e.textNodes()) {
				// Omit empty strings
				if(!textNode.text().matches("^\\s+$")) {
					// Search for countries in the text node
					venue = searchCountries(textNode.text(), country);
					if(!venue.isEmpty()) {
						logger.debug("Found venue \"" + venue + "\" from from passed in document");
						return venue;
					}
				}
			}
		}
		
		return venue;
	}
	
	public synchronized ArrayList<String> findVenueLinks(ArrayList<String> linkList) {
		ArrayList<String> venueLinks = new ArrayList<>();
		venueLinks.add(linkList.get(0));
		// Search the links for the title of the webpage (aimed at pages with frames)
		ArrayList<String> possibleLinks = this.findAllLinks(this.changeToRegex("[tT]itle"), linkList);
		if(!possibleLinks.isEmpty())
			venueLinks.addAll(possibleLinks);
		
		// Search for the different links in the conference
		String link = this.searchLinks("[vV]enue", linkList);
		if(!link.isEmpty())
			venueLinks.add(link);
		link = this.searchLinks("[rR]egistra", linkList);
		if(!link.isEmpty())
			venueLinks.add(link);

		return venueLinks;
	}
	
	/**
	 * Parses the important deadlines from the /Important_Dates
	 * link (if available) or the home page
	 * @return list of deadlines
	 */
	public LinkedHashMap<String, LinkedHashMap<String, String>> getDeadlines(ArrayList<String> linkList) {
		Document doc = null;
		LinkedHashMap<String, String> deadlines = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, String>> allDeadlines = new LinkedHashMap<>();
		
		String link = this.searchLinks("[iI]mportant", linkList);
		if(link.isEmpty())
			return allDeadlines;
		else {
			logger.debug("Getting deadlines from: " + link);
			doc = crawler.getURLDoc(link);
			
			Elements tds;
			try {
				// Selects the last table on the website
				Element el = doc.select("table").last();
				// Gets the rows from the table
				Elements rows = el.select("tr");
				// Gets the <td> elements from the rows
				tds = rows.select("td");
			} catch(NullPointerException e) {
				return allDeadlines;
			}
			
			boolean wasSplit = false;
			String[] split = null;
			String keyHeading = "";
			for(Element td: tds) {
				// Replace all uneeded tags with empty strings
				String filteredTd = td.toString().replaceAll("\r|\n", "");
				filteredTd = filteredTd.replaceAll("<strike>(.*?|.*\\n.*\\n)<\\/strike>|<del>(.*?|.*\\n.*\\n)<\\/del>|line-through.+?>.+?<\\/.+?>", "");
				// Use Jsoup to remove the rest of the tags
				filteredTd = Jsoup.parse(filteredTd).text();
				
				String dateRegex = "(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)\\s+\\d{1,2}(\\s+|,)\\s+\\d{4}";
				
				boolean finished = false;
				if(filteredTd.matches(".+?:")) {
					split = filteredTd.split(":");
					wasSplit = true;
				} else if(wasSplit == true) {
					Pattern pattern = Pattern.compile(dateRegex);
					Matcher matcher = pattern.matcher(filteredTd);
					int i = 0;
					while(!finished) {
						if(matcher.find()) {
							deadlines.put(split[i], matcher.group());
							i++;
						} else
							finished = true;
					}
					allDeadlines.put(new String(keyHeading), new LinkedHashMap<String, String>(deadlines));
					
					// Reset the values for the next iteration of deadlines
					deadlines.clear();
					wasSplit = false;
					keyHeading = "";
				} else if(filteredTd.matches("^\\s+$") || filteredTd.matches(""));
				else {
					keyHeading = filteredTd;
				}
			}
				
			return allDeadlines.size() < 3 ? new LinkedHashMap<String, LinkedHashMap<String, String>>() : allDeadlines;
		}
	}
	
	/**
	 * Checks the title for the conference year (if available).
	 * @param title
	 * @return conference year
	 */
	public String getConferenceYear(String date, String title) {
		String year = "";
		Pattern pattern = Pattern.compile("\\d{4}");
		Matcher matcher;
		// Match the pattern with the title
		matcher = pattern.matcher(date);
		if(matcher.find())
			year = matcher.group(0);
		
		logger.debug("Found conference year \"" + year + "\" from the date");
		return year;
	}
	
	/**
	 * Checks the description or hompage for the antiquity of the conference.
	 * @param description
	 * @return antiquity
	 */
	public String getAntiquity(String title, String description, Document doc) {
		String antiquity = "";
		Pattern pattern = Pattern.compile("\\d{1,2}(st|nd|rd|th)|([tT]wenty-|[tT]hirty-|[fF]orty-"
				+ "|[fF]ifty-|[sS]ixty-|[sS]eventy-|[eE]ighty-|[nN]inety-)*([fF]ir|[sS]eco|[tT]hi|"
				+ "[fF]our|[fF]if|[sS]ix|[sS]even|[eE]igh|[nN]in|[tT]en|[eE]leven|[tT]welf|[tT]hirteen|"
				+ "[fF]ourteen|[fF]ifteen|[sS]ixteen|[sS]eventeen|[eE]ighteen|[nN]ineteen)(st|nd|rd|th)|"
				+ "(twentieth|thirtieth|fourtieth|fiftieth|sixtieth|seventieth|eightieth|ninetieth)", Pattern.CASE_INSENSITIVE);
		Matcher matcher;
		// Match the pattern with the description
		matcher = pattern.matcher(description);
		if(matcher.find()) {
			antiquity = matcher.group(0);
			String confDays = this.findConfDays(description);

			if(!confDays.isEmpty()) {
				if(confDays.contains(antiquity))
					return "";
			}
				
			// If the string is in the format of "1st, 2nd, 3rd" etc.
			// Change it to its ordinal form
			if(antiquity.toLowerCase().matches("\\d{1,2}(?:st|nd|rd|th)")) {
				String[] number = antiquity.toLowerCase().split("(?:st|nd|rd|th)");
				antiquity = this.toOrdinal(Integer.parseInt(number[0]));
			}
		}
		
		logger.debug("Found antiquity \"" + antiquity +  "\" from the description");
		return antiquity;
	}
	
	/**
	 * Finds the days on which the conference is ongoing.
	 * @param title, description
	 * @return date
	 */
	public String getConferenceDays(String title, String description, Document doc) {
		String confDays = this.findConfDays(title);
		logger.debug("Found conference days \"" + confDays + "\" from the title");
		return confDays;
	}
	
	/**
	 * Finds the organizers on the websites.
	 * @param linkList
	 * @param country
	 * @return a map with organizer teams as key and a list of members as value
	 */
	public LinkedHashMap<String, List<String>> getOrganisers(Document doc, Country country) {
		LinkedHashMap<String, List<String>> committees = new LinkedHashMap<>();
		
		// Check if the format of organisers is suitable for this code
		if(!this.checkOrganiserFormat(doc, country))
			return committees;
		else {
			String tempSubteam = "";
			
			List<String> members = new ArrayList<>();
			
			// Replace the strong tags as they can cause wrong committees to be returned from certain websites
			doc = Jsoup.parse(doc.toString().replaceAll("<strong>|</strong>", ""));
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
					}
					// Check the string for a country to find if it is a member or not
					else if(this.checkStringForCountry(textNode.text(), country)) {
						// Add the member to the list if a valid subteam is present
						if(textNode.text().matches(".*?(,|\\().*?,.*$")) {
							if(!tempSubteam.equals(""))
								members.add(textNode.text().replaceAll("\"|'", ""));
						}
					}
					// Do nothing with empty strings
					else if(textNode.text().matches("^\\s+$")); 
					else {
						// If it's neither a member or a subteam then everything gathered so far to the map and clear the variables
						if(!tempSubteam.equals("") && !members.isEmpty()) {
							committees.put(tempSubteam.replaceAll("\"|'", ""), new ArrayList<String>(members));
						}
						members.clear();
						tempSubteam = "";
						info_logger.info("A possible committee member that wasn't found by the system: " + textNode.text());
					}
				}
			}
		}
		
		// If only 1 committee is returned then it must be an error
		return committees.size() < 2 ? new LinkedHashMap<String, List<String>>() : committees;
	}
	
	/**
	 * Looks through the links to find ones that may contain committees
	 * @param linkList
	 * @return list of links that could contain committee members
	 */
	public synchronized ArrayList<String> findCommitteeLinks(ArrayList<String> linkList) {
		ArrayList<String> potentialLinks = new ArrayList<>();
		// Find the links on the websites that contain the organizers
		this.addCommitteeSearchWords();
		
		// Needs to be synchronized so that two threads don't modify the same list at the same time
		synchronized(searchKeywords) {
			for(String keyword: searchKeywords) {
				ArrayList<String> links = this.findAllLinks(keyword, linkList);
				if(!links.isEmpty())
					for(String link: links)
						if(!potentialLinks.contains(link))
							potentialLinks.add(link);
			}
		}
		
		return potentialLinks;
	}
	
	// ------------- HELPER METHODS START HERE -------------
	
	protected void addLinkKeywords() {
		searchKeywords.clear();
		searchKeywords.add("[sS]ubmission");
		searchKeywords.add("[pP]roceedings");
		searchKeywords.add("[pP]aper");
		searchKeywords.add("[iI]nstructions");
		searchKeywords.add("[iI]nformation");
		searchKeywords.add("[gG]uideline");
		searchKeywords.add("[pP]ublication");
		searchKeywords.add("[dD]ocument");
	}
	
	/**
	 * Adds search keywords to the ArrayList that will be
	 * used for searching the important dates
	 * i.e. submission, notification, camera
	 */
	protected void addCommitteeSearchWords() {
		searchKeywords.clear();
		searchKeywords.add(this.changeToRegex("[oO]rganiz"));
		searchKeywords.add(this.changeToRegex("[oO]rganis"));
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
	protected synchronized String searchCountries(String string, Country country) {
		String venue = "", countryRegex;
		// Go through the list of countries
		for(String countryName: country.getCountries()) {
			countryRegex = this.changeToRegex(" " + countryName);
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
	protected boolean checkStringForCountry(String string, Country country) {
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
	protected synchronized String searchLinks(String keyword, ArrayList<String> linkList) {
		String answer = "";
		keyword = this.changeToRegex(keyword);
		
		// Find the link in the list of links that contains the keyword if possible
		for(String link: linkList) {
			if(link.matches(keyword))
				return link;
		}
		
		return answer;
	}
	
	/**
	 * Receives a keyword that we want to find in the link
	 * and returns the list of links if it has been found.
	 * e.g. passing in "venue" can return a link like
	 * www.example.com/venue/
	 * @param keyword
	 * @param linkList
	 * @return list of links
	 */
	protected ArrayList<String> findAllLinks(String keyword, ArrayList<String> linkList) {
		ArrayList<String> answer = new ArrayList<>();
		keyword = this.changeToRegex(keyword);
		
		// Find the link in the list of links that contains the keyword if possible
		for(String link: linkList) {
			if(link.toLowerCase().matches(keyword) && !answer.contains(link))
				answer.add(link);
		}
		
		return answer;
	}
	
	/**
	 * Finds the deadline from the received parameters.
	 * Returns an empty string if no match is found.
	 * @param separated
	 * @param toFind
	 * @param pattern
	 * @return deadline
	 */
	protected String findPattern(String string, Pattern pattern) {
		// Match the string with the pattern
		 Matcher matcher = pattern.matcher(string);
		
		if(matcher.find())
			return matcher.group(0);
		else
			return "";
	}
	
	/**
	 * Looks through the string to find a date.
	 * @param toCheck
	 * @return date or empty string
	 */
	protected String findConfDays(String toCheck) {		
		Pattern pattern = Pattern.compile(confDaysPattern);
		Matcher matcher;
		// Match the pattern with the string
		matcher = pattern.matcher(toCheck);
		if(matcher.find()) {
			// Make sure the found string doesn't exceed the maximum length
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
	protected boolean searchForCommittees(String string) {
		String subteamRegex;
		for(String subteam: information.getCommitteeNames()) {
			subteamRegex = this.changeToRegex(subteam);
			if(string.toLowerCase().matches(subteamRegex))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if the organisers are in the format e.g. General Chair\nJohn Doe, UCD, Ireland
	 * @param link
	 * @param country
	 * @return true/false
	 */
	protected boolean checkOrganiserFormat(Document doc, Country country) {
		logger.debug("Checking format of the passed in document");
		int counter = 0;
		// Find all elements and text nodes
		for(Element node: doc.getAllElements()) {
			for(TextNode textNode: node.textNodes()) {
				// Check if this text node contains the committee keywords
				boolean isCommittee = this.searchForCommittees(textNode.text());
				// Skip the whitespaces
				if(textNode.text().matches("^\\s+$"));
				// If it does then add 1 to the counter
				else if(isCommittee && counter == 0) {
					counter++;
				// If the previous text node was a committee then make sure the counter is at 1 for this text node
				} else if(isCommittee && counter > 0) {
					counter = 1;
				// Add 1 to the counter if the text node contains a country i.e. it's a committee member
				} else {
					if(this.checkStringForCountry(textNode.text(), country)) {
						counter++;
						// Even if the counter reached 2, check if the text doesn't ONLY contain a country (has other words)
						if(counter == 2 && textNode.text().matches(".*?(,|\\().*?,.*$"))
							return true;
					} else {
						counter = 0;
					}
				}
					
			}
		}
		return false;
	}
	
	/**
	 * Checks if the string contains a submission, camera, proposal etc. keywords
	 * @param toCheck
	 * @return true/false
	 */
	protected boolean isSubmission(String toCheck) {
		// Add keywords to this array if new are found
		String[] keywords = {"submis", "submit", "notification", "camera", "proposal", "registration"};
		
		// Iterate through the keywords to find it in the string
		for(String key: keywords) {
			if(toCheck.toLowerCase().matches(this.changeToRegex(key)))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Searches for the acronym of the conference in the passed in string parameter
	 * @param toCheck
	 * @return acronym
	 */
	protected String findAcronym(String toCheck) {
		Pattern pattern = Pattern.compile(acronymYearPattern);
		
		// Match the title with the pattern
		Matcher matcher = pattern.matcher(toCheck);
		pattern = Pattern.compile(acronymPattern);
		
		String found;
		// Check if the string contains the acronym followed by the year i.e. ICPE 2018
		if(matcher.find())
			found = this.findPattern(matcher.group(0), pattern);
		// Otherwise check for a sequence of capital letters
		else
			found = this.findPattern(toCheck, pattern);
		
		// Filter out the possibility of the acronym being a sponsor
		for(String sponsor: information.getSponsors()) {
			if(found.toLowerCase().contains(sponsor.toLowerCase()))
				return "";
		}
		
		return found;
	}
	
	/**
	 * Finds a link with the history keyword
	 * @param linkList
	 * @return link containing the history keyword
	 */
	public String findLinkContainingHistory(ArrayList<String> linkList) {
		return this.searchLinks("[hH]istory", linkList);
	}
	
	/**
	 * Finds a link with the history keyword
	 * @param linkList
	 * @return link containing the history keyword
	 */
	public ArrayList<String> findConferenceDaysLinks(ArrayList<String> linkList) {
		ArrayList<String> possibleLinks = new ArrayList<>();
		possibleLinks.add(linkList.get(0));
		// Search the links for the title of the webpage (aimed at pages with frames)
		ArrayList<String> links = this.findAllLinks(this.changeToRegex("[tT]itle"), linkList);
		if(!links.isEmpty())
			possibleLinks.addAll(links);
		String link = this.searchLinks("[iI]mportant", linkList);
		if(!link.isEmpty())
			possibleLinks.add(link);
		
		return possibleLinks;
	}
}
