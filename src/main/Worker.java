package main;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jsoup.nodes.Document;

import crawler.Crawler;
import crawler.Parser;
import database.Conference;
import venue.Country;

public class Worker implements Runnable {
	
	private String url;
	private ArrayList<String> linkList = new ArrayList<>();
	private Crawler crawler;
	private Country country;
	private Row row;
	private Sheet sheet;
	private CreationHelper createHelper;
	private ArrayList<Parser> parsers;
	private int rowNumber;
	private Conference sqlConnection;
	
	public Worker(String url, Crawler crawler, Country country, Row row, Sheet sheet, CreationHelper createHelper, ArrayList<Parser> parsers, int rowNumber, Conference sqlConnection) {
		this.url = url;
		this.crawler = crawler;
		this.country = country;
		this.row = row;
		this.sheet = sheet;
		this.createHelper = createHelper;
		this.parsers = parsers;
		this.rowNumber = rowNumber;
		this.sqlConnection = sqlConnection;
	}
	
	// Runs the thread when created
	public void run() {
		
		try {
			// This will hang until there is a vacancy
    	    Main.semaphore.acquire(); 
    	    this.extractInformation(this.url);
    	} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
    	    Main.semaphore.release();
    	}
	}
	
	// Fetches more links from already fetched links from Crawler
	private void extractInformation(String url) {
		LinkedHashMap<String, LinkedHashMap<String, String>> deadlines = new LinkedHashMap<>();
		linkList.add(url);
		linkList = crawler.getAllLinks(crawler.getURLDoc(url), linkList);
        
        row = sheet.createRow(rowNumber);
        // TODO Use this in the database to store the link to be used with the acronym as a[href] on the webpage
        String mainLink = linkList.get(0);
        row.createCell(1).setCellValue(createHelper.createRichTextString(mainLink));
        Document mainLinkDoc = crawler.getURLDoc(mainLink);
		String title = parsers.get(0).getTitle(mainLinkDoc);
		row.createCell(2).setCellValue(createHelper.createRichTextString(title));
		
		ArrayList<String> proceedingLinks = parsers.get(0).findProceedingLinks(linkList);
		String proceedings = "";
		int k = 0;
		int l = 0;
		do {
			l = 0;
			do {
				if(proceedingLinks.isEmpty())
					proceedings = parsers.get(k).getProceedings(null);
				else
					proceedings = parsers.get(k).getProceedings(crawler.getURLDoc(proceedingLinks.get(l)));
				l++;
			} while(proceedings.equals("") && l < proceedingLinks.size());
        	k++;
        } while(proceedings.equals("") && k < parsers.size());  
		row.createCell(4).setCellValue(createHelper.createRichTextString(proceedings));
		
		String description;
		k = 0;
		do {
        	description = parsers.get(k).getDescription(mainLinkDoc);
        	k++;
        } while(description.equals("") && k < parsers.size());   
        
        row.createCell(5).setCellValue(createHelper.createRichTextString(description));
        
        String acronym;
        k = 0;
		do {
			acronym = parsers.get(k).getAcronym(title, description);
        	k++;
        } while(acronym.equals("") && k < parsers.size());   
		
        row.createCell(0).setCellValue(createHelper.createRichTextString(acronym));
        
        String sponsor;
		k = 0;
		do {
			sponsor = parsers.get(k).getSponsors(title, description);
			k++;
		} while(sponsor.equals("") && k < parsers.size());
		
		row.createCell(3).setCellValue(createHelper.createRichTextString(sponsor));
        
		ArrayList<String> venueLinks = parsers.get(0).findVenueLinks(linkList);
        String venue = "";
        k = 0;
		do {
			l = 0;
			do {
				if(venueLinks.isEmpty())
					venue = parsers.get(k).getVenue(title, description, country, null);
				else
					venue = parsers.get(k).getVenue(title, description, country, crawler.getURLDoc(venueLinks.get(l)));
				l++;
			} while(venue.equals("") && l < venueLinks.size());
        	k++;
        } while(venue.equals("") && k < parsers.size());

		System.out.println("VENUE: " + venue);
        row.createCell(6).setCellValue(createHelper.createRichTextString(venue));
        
		ArrayList<String> potentialLinks = new ArrayList<String>();
		potentialLinks.add(linkList.get(0));
		String historyLink = parsers.get(0).findLinkContainingHistory(linkList);
		if(!historyLink.isEmpty())
			potentialLinks.add(historyLink);
		
        String antiquity; 
        k = 0;
        do {
        	l = 0;
        	do {
        		if(potentialLinks.isEmpty())
        			antiquity = parsers.get(k).getAntiquity(title, description, null);
        		else
        			antiquity = parsers.get(k).getAntiquity(title, description, crawler.getURLDoc(potentialLinks.get(l)));
				l++;
			} while(antiquity.equals("") && l < potentialLinks.size());
        	k++;
        } while(antiquity.equals("") && k < parsers.size());
        k--;
        row.createCell(7).setCellValue(createHelper.createRichTextString("Parser " + k + ": " + antiquity));
		

        potentialLinks.clear();
        potentialLinks = parsers.get(0).findConferenceDaysLinks(linkList);
		String conferenceDays;
		k = 0;
		
        do {
        	l = 0;
        	do {
        		if(potentialLinks.isEmpty())
        			conferenceDays = parsers.get(k).getConferenceDays(title, description, null);
        		else
        			conferenceDays = parsers.get(k).getConferenceDays(title, description, crawler.getURLDoc(potentialLinks.get(l)));
				l++;
			} while(conferenceDays.equals("") && l < potentialLinks.size());
        	k++;
        } while(conferenceDays.equals("") && k < parsers.size());
        row.createCell(8).setCellValue(createHelper.createRichTextString(conferenceDays));
        
        String year = "";
        k = 0;
        do {
        	 year = parsers.get(k).getConferenceYear(conferenceDays, title);
        	 k++;
        } while(year.equals("") && k < parsers.size());
       
        row.createCell(9).setCellValue(createHelper.createRichTextString(year));
        
        k = 0;
        do {
        	deadlines = parsers.get(k).getDeadlines(linkList);	
        	k++;
        } while(deadlines.isEmpty() && k < parsers.size());
        	
        k--;
        int j = 10;

        for(String key: deadlines.keySet()) {
//			System.out.println("Heading: " + key);
			LinkedHashMap<String, String> deadlines1 = deadlines.get(key);
			for(String d: deadlines1.keySet()) {
//				System.out.println(d + ": " + deadlines1.get(d));
				row.createCell(j).setCellValue(createHelper.createRichTextString("Parser used: " + k + " (Heading) " + key + " ///// " + d + " ///// " + deadlines1.get(d)));
				j++;
			}
		}
        
        LinkedHashMap<String, List<String>> committees = new  LinkedHashMap<>();
        k = 0;
        do {
        	ArrayList<String> committeeLinks = parsers.get(k).findCommitteeLinks(linkList);
			for(String link: committeeLinks) {
				committees.putAll(parsers.get(k).getOrganisers(crawler.getURLDoc(link), country));
			}
        	k++;
        } while(committees.isEmpty() && k < parsers.size());
        
        String allMembers = "";
        if(!committees.isEmpty()) {
        	for(String subteam: committees.keySet()) {
        		allMembers += subteam + ": ";
				List<String> subteamMembers = committees.get(subteam);
				for(String subteamMember: subteamMembers) {
					allMembers += subteamMember + " //// ";
				}
//				System.out.println("(wtf) " + allMembers);
	        	row.createCell(++j).setCellValue(createHelper.createRichTextString(allMembers));
	        	allMembers = "";
			}
        }	
        try {
			this.sqlConnection.addConference(acronym, title, sponsor, proceedings, description, venue, year, antiquity, conferenceDays, committees, deadlines);
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
	}
}
