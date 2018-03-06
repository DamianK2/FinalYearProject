package crawler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import venue.Country;

public class Parser2 extends Parser {
	
	public Parser2(Information info) {
		super(info);
	}
	
	@Override
	public String getAcronym(String title, String description) {
		return this.findAcronym(description);
	}

	@Override
	public String getSponsors(String title, String description) {
		String sponsors = "";
		
		// Iterate through the list of sponsors
		for(String sponsor: information.getSponsors()) {
			// Check if the description has the sponsor
			if(description.matches(this.changeToRegex(sponsor)))
				if(sponsors.isEmpty())
					sponsors += sponsor;
				else
					sponsors += "/" + sponsor;
		}
		
		return sponsors;
	}
	
	@Override
	public String getDescription(Document doc) {
		String meta = "";
		try {
			meta = doc.select("meta[property=og:description]").first().attr("content");
		} catch(NullPointerException e) {
			System.out.println("No meta with attribute \"property\"");
		}
		return meta;
	}
	
	@Override
	public String getVenue(String title, String description, Country country, Document doc) {
		String venue = "";
		// Search the title for the country of the conference
		if(!title.equals(""))
			venue = this.searchCountries(title, country);
		
		return venue;
	}
	
	@Override
	public LinkedHashMap<String, LinkedHashMap<String, String>> getDeadlines(ArrayList<String> linkList) {
		Document doc = null;
		LinkedHashMap<String, String> deadlines = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, String>> allDeadlines = new LinkedHashMap<>();
		Pattern pattern = Pattern.compile("(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)\\s+\\d{1,2}(\\s+|,)\\s+\\d{4}", Pattern.CASE_INSENSITIVE);
		String[] separated, split;
		
		// Connect to the home page
		doc = this.getURLDoc(linkList.get(0));
		// Select the div with "Important Dates"
		Element el = doc.select("div:contains(Important Dates)").last();
		
		try {
			// Extract the paragraph
			String elementString = el.select("p").toString().replaceAll("\r|\n", "");
			elementString = elementString.replaceAll("<strike>(.*?|.*\\n.*\\n)<\\/strike>|<del>(.*?|.*\\n.*\\\\n)<\\/del>|line-through.+?>.+?<\\/.+?>", "");
			
			if(!elementString.isEmpty()) {
				// Split into multiple lines on seeing the <br> tag
				separated = elementString.split("<br> ");
				int i = 0;
				for(String string: separated) {
					String found = this.findPattern(Jsoup.parse(string).text(), pattern);
					if(!found.equals("")) {
						split = Jsoup.parse(string).text().split(":");
						deadlines.put(split[0], found);
						allDeadlines.put(Integer.toString(i), new LinkedHashMap<String, String>(deadlines));
						deadlines.clear();
						i++;
					}
				}
			}
		} catch(NullPointerException e) {
			return allDeadlines;
		}
		
		
//		for(String key: allDeadlines.keySet()) {
//			System.out.println("Heading: " + key);
//			LinkedHashMap<String, String> deadlines1 = allDeadlines.get(key);
//			for(String d: deadlines1.keySet()) {
//				System.out.println(d + ": " + deadlines1.get(d));
//			}
//		}
		
		return allDeadlines;
	}
	
	@Override
	public String getConferenceYear(String date, String title) {
		String year = "";
		Pattern pattern = Pattern.compile("\\d{4}");
		Matcher matcher;
		// Match the pattern with the title
		matcher = pattern.matcher(title);
		if(matcher.find())
			year = matcher.group(0);
		return year;
	}
	
	@Override
	public String getAntiquity(String title, String description, Document doc) {
		String antiquity = "";
		Pattern pattern = Pattern.compile("\\d{1,2}(st|nd|rd|th)|([tT]wenty-|[tT]hirty-|[fF]orty-"
				+ "|[fF]ifty-|[sS]ixty-|[sS]eventy-|[eE]ighty-|[nN]inety-)*([fF]ir|[sS]eco|[tT]hi|"
				+ "[fF]our|[fF]if|[sS]ix|[sS]even|[eE]igh|[nN]in|[tT]en|[eE]leven|[tT]welf|[tT]hirteen|"
				+ "[fF]ourteen|[fF]ifteen|[sS]ixteen|[sS]eventeen|[eE]ighteen|[nN]ineteen)(st|nd|rd|th)|"
				+ "(twentieth|thirtieth|fourtieth|fiftieth|sixtieth|seventieth|eightieth|ninetieth)", Pattern.CASE_INSENSITIVE);
		Matcher matcher;
		// Match the pattern with the description
		matcher = pattern.matcher(title);
		if(matcher.find()) {
			antiquity = matcher.group(0);
			// If the string is in the format of "1st, 2nd, 3rd" etc.
			// Change it to its ordinal form
			if(antiquity.toLowerCase().matches("\\d{1,2}(?:st|nd|rd|th)")) {
				String[] number = antiquity.toLowerCase().split("(?:st|nd|rd|th)");
				antiquity = this.toOrdinal(Integer.parseInt(number[0]));
			}
		}
		
		return antiquity;
	}
	
	@Override
	public String getConferenceDays(String title, String description, Document doc) {		
		return this.findConfDays(description);
	}
	
	@Override
	public LinkedHashMap<String, List<String>> getOrganisers(Document doc, Country country) {
		LinkedHashMap<String, List<String>> committees = new LinkedHashMap<>();
		
		// Check if the format of organisers is suitable for this code
		if(!this.checkOrganiserFormat(doc, country))
			return committees;
		else {
			// Initialize variables
			String tempSubteam = "";
			List<String> members = new ArrayList<>();
			StringBuilder sb = new StringBuilder();
			int memberCounter = 0;
			
			// Find all elements and text nodes
			for(Element node: doc.getAllElements()) {
				for(TextNode textNode: node.textNodes()) {
//						System.out.println(textNode.text());
					// Search for committee names in the text node
					if(this.searchForCommittees(textNode.text())) {
						// If the subteam isn't empty and there are members in the list add them to the map to be returned later
						if(!tempSubteam.equals("") && !members.isEmpty()) {
							// Avoid overwriting the same key in the map, and add members to the existing list instead
							this.addToCommittees(committees, tempSubteam, members);
							members.clear();
						}
						// Add the subteam found for later use as a key
						tempSubteam = textNode.text();
					}
					// Do nothing with empty text nodes
					else if(textNode.text().matches("^\\s+$"));
					else if(!tempSubteam.equals("") && memberCounter < 4) {
						// Check if this text node is a country
						boolean isCountry = this.checkStringForCountry(textNode.text(), country);
						// If it is not the first text node to be appended (counter > 0), then add a comma and increase the counter
						if(memberCounter > 0 && !isCountry) {
							sb.append(", " + textNode.text());
							memberCounter++;
						}
						// If the text node contains a country then we concatenated a full string and we can add it to the list of members
						else if(memberCounter > 0 && isCountry) {
							// Add the member to the list
							sb.append(", " + textNode.text());
							members.add(new String(sb.toString()));
							// Reset the string builder and counter
							sb.setLength(0);
							memberCounter = 0;
						}
						// The counter is 0, add the raw string
						else {
							sb.append(textNode.text());
							memberCounter++;
						}
					}
					else {
						// If it's neither a member or a subteam then add everything gathered so far to the map and clear the variables
						if(!tempSubteam.equals("") && !members.isEmpty()) {
							// Avoid overwriting the same key in the map, and add members to the existing list instead
							this.addToCommittees(committees, tempSubteam, members);
						}
						
						// Reset the variables
						members.clear();
						tempSubteam = "";
						memberCounter = 0;
						sb.setLength(0);
					}
				}
			}
		}
		
//		// Test print
//		String allMembers = "";
//		for(String subteam: committees.keySet()) {
//    		allMembers += subteam + ": ";
//			List<String> subteamMembers = committees.get(subteam);
//			for(String subteamMember: subteamMembers) {
//				allMembers += subteamMember + " //// ";
//			}
//			System.out.println(allMembers);
//        	allMembers = "";
//		}
		
		// If only 1 committee is returned then it must be an error
		return committees.size() < 2 ? new LinkedHashMap<String, List<String>>() : committees;
	}
	
	/**
	 * Add the given parameters to the map
	 * @param committees
	 * @param tempSubteam
	 * @param members
	 */
	private void addToCommittees(LinkedHashMap<String, List<String>> committees, String tempSubteam, List<String> members) {
		List<String> membs = null;
		if(committees.containsKey(tempSubteam)) {
			membs = committees.get(tempSubteam);
			for(String member: members) {
				if(!membs.contains(member))
					membs.add(member);
			}
			if(membs != null)
				committees.put(tempSubteam, new ArrayList<String>(membs));
		} else {
			committees.put(tempSubteam, new ArrayList<String>(members));
		}
	}
	
	@Override
	protected boolean checkOrganiserFormat(Document doc, Country country) {
		int counter = 0;
		// Find all elements and text nodes
		for(Element node: doc.getAllElements()) {
			for(TextNode textNode: node.textNodes()) {
				// Check if this text node contains the committee keywords
				boolean isCommittee = this.searchForCommittees(textNode.text());
				// Skip the whitespaces
				if(textNode.text().matches("^\\s+$"));
				// If it does then add 1 to the counter
				else if(isCommittee && counter == 1) {
					counter++;
				} else if(isCommittee && counter != 1) {
					counter = 0;
				// Add 1 to the counter if the text node contains a country
				} else if(this.checkStringForCountry(textNode.text(), country)) {
					counter++;
					if(counter == 3)
						return true;
					else
						counter = 0;
				} else {
					counter++;
				}
					
			}
		}
		return false;
	}
}
