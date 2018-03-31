package crawler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import database.Information;

public class Parser5 extends Parser {
	static Logger logger = LogManager.getLogger(Parser5.class);
	
	public Parser5(Information info, Crawler c) {
		super(info, c);
	}
	
	@Override
	public String getDescription(Document doc) {
		String description = "";

		try {
			description = doc.select("p").first().text();
			return description;
		} catch(NullPointerException e) {
			return "";
		}
		
		
	}
	
	@Override
	public LinkedHashMap<String, LinkedHashMap<String, String>> getDeadlines(ArrayList<String> linkList) {
		Document doc = null;
		LinkedHashMap<String, String> deadlines = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, String>> allDeadlines = new LinkedHashMap<>();
		String regex = "(\\d{1,2}\\s+)*(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)(\\s+\\d{1,2}(\\s+|,)\\s+\\d{4}|\\s+\\d{4})";
		String[] separated;
		
		String link = this.searchLinks("[iI]mportant", linkList);
		if(link.isEmpty())
			return allDeadlines;
		else {
			try {
				logger.debug("Getting deadlines from: " + link);
				doc = crawler.getURLDoc(link);
				
				Element el = doc.select("div:contains(Important Dates)").last();
				// Extract the paragraph
				String elementString = el.select("p").toString().replaceAll("\r|\n", "");
				// Replace the unneeded text
				elementString = elementString.replaceAll("<strike>(.*?|.*\\n.*\\n)<\\/strike>|<del>(.*?|.*\\n.*\\\\n)<\\/del>|line-through.+?>.+?<\\/.+?>", "");
				// Split the paragraphs
				separated = elementString.split("</p>");
				
				int i = 0;
				boolean foundTitle = false;
				String deadlineTitle = "";
				for(String string: separated) {
					// Split each paragraph on a break tag
					String[] furtherSeparated = string.split("<br>");
					for(String s: furtherSeparated) {
						// Separate the deadline titles from the submission dates
						if(!s.matches(this.changeToRegex(regex)) && !foundTitle) {
							deadlineTitle = Jsoup.parse(s).text();
							foundTitle = true;
						}
						else if(s.matches(this.changeToRegex(regex)) && foundTitle) {
							deadlines.put(deadlineTitle, Jsoup.parse(s).text());
							// Store the information in the linked hash map
							allDeadlines.put(Integer.toString(i), new LinkedHashMap<String, String>(deadlines));
							// Reset the variables
							deadlines.clear();
							foundTitle = false;
							i++;
						} else {
							deadlineTitle = Jsoup.parse(s).text();
						}
					}
				}
			} catch(NullPointerException e) {
				return new LinkedHashMap<String, LinkedHashMap<String, String>>();
			}
		}
		
		return allDeadlines;
	}
	
	@Override
	public String getAntiquity(String title, String description, Document doc) {
		if(doc == null) 
			return "";
		
		Pattern pattern = Pattern.compile("\\d{1,2}(st|nd|rd|th)|([tT]wenty-|[tT]hirty-|[fF]orty-"
				+ "|[fF]ifty-|[sS]ixty-|[sS]eventy-|[eE]ighty-|[nN]inety-)*([fF]ir|[sS]eco|[tT]hi|"
				+ "[fF]our|[fF]if|[sS]ix|[sS]even|[eE]igh|[nN]in|[tT]en|[eE]leven|[tT]welf|[tT]hirteen|"
				+ "[fF]ourteen|[fF]ifteen|[sS]ixteen|[sS]eventeen|[eE]ighteen|[nN]ineteen)(st|nd|rd|th)|"
				+ "(twentieth|thirtieth|fourtieth|fiftieth|sixtieth|seventieth|eightieth|ninetieth)", Pattern.CASE_INSENSITIVE);
		
		// Split on the new line character
		String[] separated = doc.wholeText().split("\n");
		String antiquity = "";
		
		for(String toCheck: separated) {
			antiquity = this.findPattern(toCheck, pattern);
			
			// No need to check other if statements if the string is empty
			if(antiquity.isEmpty())
				continue;
			// If it is in the form of 1st, 2nd, 3rd etc. then strip of the ending i.e. st, nd
			else if(antiquity.toLowerCase().matches("\\d{1,2}(?:st|nd|rd|th)")) {
				String[] number = antiquity.toLowerCase().split("(?:st|nd|rd|th)");
				antiquity = this.toOrdinal(Integer.parseInt(number[0]));
				logger.debug("Found antiquity \"" + antiquity +  "\" from passed in document");
				return antiquity;
			}
		}
		
		return "";
	}
	
	@Override
	public String getConferenceDays(String title, String description, Document doc) {
		if(doc == null) 
			return "";
		
		for(Element el: doc.getAllElements()) {
			for(TextNode textNode: el.textNodes()) {
				String found = this.findConfDays(textNode.text());
				if(!found.equals("")) {
					logger.debug("Found conference days \"" + found + "\" from the passed in document");
					return found;
				}
			}
		}
		return "";
	}
}
