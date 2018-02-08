package crawler;

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
					String found = this.findDeadline(Jsoup.parse(string).text(), pattern);
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
	public String getAntiquity(String description, ArrayList<String> linkList) {
		String antiquity = "";
	
		// Connect to the home page
		Document doc = this.getURLDoc(linkList.get(0));
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
	
	public String getConferenceDays(String title, String description, ArrayList<String> linkList) {		
		return this.findConfDays(description);
	}
	
	/**
	 * Finds the organizers on the websites.
	 * @param linkList
	 * @param country
	 * @return a map with organizer teams as key and members as value
	 */
	public LinkedHashMap<String, List<String>> getOrganisers(ArrayList<String> linkList, Country country) {
		List<String> potentialLinks = new ArrayList<>();
		this.addCommitteeSearchWords();
		
		// Find the links containing the search keywords (i.e. committee, chair etc.)
		for(String keyword: searchKeywords) {
			ArrayList<String> links = this.findAllLinks(keyword, linkList);
			if(!links.isEmpty())
				// Iterate through the links to make sure no duplicates are added
				for(String link: links)
					if(!potentialLinks.contains(link)) {
//						System.out.println(link);
						potentialLinks.add(link);
					}	
		}
		
		// Initialize variables
		String tempSubteam = "";
		LinkedHashMap<String, List<String>> committees = new LinkedHashMap<>();
		List<String> members = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		int memberCounter = 0;
		
		for(String link: potentialLinks) {
			// Get the document
			Document doc = this.getURLDoc(link);
			
			// Find all elements and text nodes
			for(Element node: doc.getAllElements()) {
				for(TextNode textNode: node.textNodes()) {
//					System.out.println(textNode.text());
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
		
		return committees;
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
}
