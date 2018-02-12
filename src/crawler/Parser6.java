package crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.TextNode;

public class Parser6 extends Parser {

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
			doc = this.getURLDoc(link);
			
			Elements el = doc.select("div:contains(Important Dates)");
			
			try {
				// Extract the paragraph
				String elementString = el.select("p").toString().replaceAll("\r|\n", "");
				// Replace the unneeded text
				elementString = elementString.replaceAll("<strike>(.*?|.*\\n.*\\n)<\\/strike>|<del>(.*?|.*\\n.*\\\\n)<\\/del>|line-through.+?>.+?<\\/.+?>", "");
				
				// Split the paragraphs
				separated = elementString.split("</p>");
				
				int i = 0;
				for(String string: separated) {
					String[] furtherSeparated;
					// Check if the string contains a date
					if(string.matches(this.changeToRegex(regex))) {
						// Replace the unneeded symbols and separate the string
						String noHtml = Jsoup.parse(string).text().replaceAll("-|–", "");
						furtherSeparated = noHtml.split(".*" + regex);
						for(String deadlineTitle: furtherSeparated) {
							if(!deadlineTitle.isEmpty()) {
								// Populate the maps
								deadlines.put(deadlineTitle.replaceAll("^\\s+", ""), noHtml.replaceAll(deadlineTitle, ""));
								allDeadlines.put(Integer.toString(i), new LinkedHashMap<String, String>(deadlines));
								// Reset the variables
								deadlines.clear();
								i++;
							}
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
	public String getAntiquity(String title, String description, ArrayList<String> linkList) {
		Pattern pattern = Pattern.compile("\\d{1,2}(st|nd|rd|th)|([tT]wenty-|[tT]hirty-|[fF]orty-"
				+ "|[fF]ifty-|[sS]ixty-|[sS]eventy-|[eE]ighty-|[nN]inety-)*([fF]ir|[sS]eco|[tT]hi|"
				+ "[fF]our|[fF]if|[sS]ix|[sS]even|[eE]igh|[nN]in|[tT]en|[eE]leven|[tT]welf|[tT]hirteen|"
				+ "[fF]ourteen|[fF]ifteen|[sS]ixteen|[sS]eventeen|[eE]ighteen|[nN]ineteen)(st|nd|rd|th)|"
				+ "(twentieth|thirtieth|fourtieth|fiftieth|sixtieth|seventieth|eightieth|ninetieth)", Pattern.CASE_INSENSITIVE);
		// Connect to the home page
		Document doc = this.getURLDoc(linkList.get(0));
		// Split on the new line character
		String[] separated = doc.wholeText().split("\n");
		String antiquity = "";
		
		for(String toCheck: separated) {
			antiquity = this.findDeadline(toCheck, pattern);
			
			// No need to check other if statements if the string is empty
			if(antiquity.isEmpty())
				continue;
			// If it is in the form of 1st, 2nd, 3rd etc. then strip of the ending i.e. st, nd
			else if(antiquity.toLowerCase().matches("\\d{1,2}(?:st|nd|rd|th)")) {
				String[] number = antiquity.toLowerCase().split("(?:st|nd|rd|th)");
				return this.toOrdinal(Integer.parseInt(number[0]));
			} else {
				return antiquity;
			}
		}
		
		return antiquity;
		
	}
	
	@Override
	public String getConferenceDays(String title, String description, ArrayList<String> linkList) {		
		Document doc = null;
		String regex = "";
		String link = this.searchLinks("[iI]mportant", linkList);
		if(link.isEmpty())
			return "";
		else {
			doc = this.getURLDoc(link);
			
			for(Element el: doc.getAllElements()) {
				for(TextNode textNode: el.textNodes()) {
					String found = this.findConfDays(textNode.text());
					if(!found.isEmpty())
						return found;
				}
			}
			
			
		}
		
		return "";
	}
}
