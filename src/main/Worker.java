package main;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	private static final int NUM_DESCRIPTION_METHODS = 5;
	private static final int NUM_ACRONYM_METHODS = 2;
	private static final int NUM_SPONSOR_METHODS = 2;
	private static final int NUM_VENUE_METHODS = 4;
	private static final int NUM_ANTIQUITY_METHODS = 6;
	private static final int NUM_CONF_DAYS_METHODS = 6;
	private static final int NUM_CONF_YEAR_METHODS = 2;
	private static final int NUM_DEADLINE_METHODS = 9;
	private static final int NUM_COMMITTEE_METHODS = 3;
	static Logger logger = LogManager.getLogger(Worker.class);
	
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
    		logger.error("Cannot extract information from website.");
		} finally {
			Main.updatePercentage();
    	    Main.semaphore.release();
    	}
	}
	
	// Fetches more links from already fetched links from Crawler
	private void extractInformation(String url) {
		LinkedHashMap<String, LinkedHashMap<String, String>> deadlines = new LinkedHashMap<>();
		linkList.add(url);
		logger.debug("Fetching links from: " + url);
		linkList = crawler.getAllLinks(crawler.getURLDoc(url), linkList);
        row = sheet.createRow(rowNumber);
        
        String mainLink = linkList.get(0);
        row.createCell(1).setCellValue(createHelper.createRichTextString(mainLink));
        Document mainLinkDoc = crawler.getURLDoc(mainLink);
        logger.debug("Getting title from: " + mainLink);
		String title = parsers.get(0).getTitle(mainLinkDoc);
		row.createCell(2).setCellValue(createHelper.createRichTextString(title));
		
		ArrayList<String> proceedingLinks = parsers.get(0).findProceedingLinks(linkList);
		String proceedings = "";
		int l = 0;
		if(!proceedingLinks.isEmpty()) {
			do {
				logger.debug("Getting proceedings from: " + proceedingLinks.get(l));
				if(proceedingLinks.isEmpty())
					proceedings = parsers.get(0).getProceedings(null);
				else
					proceedings = parsers.get(0).getProceedings(crawler.getURLDoc(proceedingLinks.get(l)));
				l++;
			} while(proceedings.equals("") && l < proceedingLinks.size());
		}
		
		
		row.createCell(4).setCellValue(createHelper.createRichTextString(proceedings));
		
		String description;
		int k = 0;
		do {
			logger.debug("Getting description from: " + mainLink);
        	description = parsers.get(k).getDescription(mainLinkDoc);
        	k++;
        } while(description.equals("") && k < NUM_DESCRIPTION_METHODS);
        
        row.createCell(5).setCellValue(createHelper.createRichTextString(description));
        
        String acronym;
        k = 0;
		do {
			acronym = parsers.get(k).getAcronym(title, description);
        	k++;
        } while(acronym.equals("") && k < NUM_ACRONYM_METHODS);   
		
        row.createCell(0).setCellValue(createHelper.createRichTextString(acronym));
        
        String sponsor;
		k = 0;
		do {
			sponsor = parsers.get(k).getSponsors(title, description);
			k++;
		} while(sponsor.equals("") && k < NUM_SPONSOR_METHODS);
		
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
        } while(venue.equals("") && k < NUM_VENUE_METHODS);

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
        } while(antiquity.equals("") && k < NUM_ANTIQUITY_METHODS);
        
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
        } while(conferenceDays.equals("") && k < NUM_CONF_DAYS_METHODS);
        
        row.createCell(8).setCellValue(createHelper.createRichTextString(conferenceDays));
        
        String year = "";
        k = 0;
        do {
        	year = parsers.get(k).getConferenceYear(conferenceDays, title);
        	k++;
        } while(year.equals("") && k < NUM_CONF_YEAR_METHODS);
       
        row.createCell(9).setCellValue(createHelper.createRichTextString(year));
        
        k = 0;
        do {
        	deadlines = parsers.get(k).getDeadlines(linkList);	
        	k++;
        } while(deadlines.isEmpty() && k < NUM_DEADLINE_METHODS);
        	
        k--;
        int j = 10;

        for(String key: deadlines.keySet()) {
			LinkedHashMap<String, String> deadlines1 = deadlines.get(key);
			for(String d: deadlines1.keySet()) {
				row.createCell(j).setCellValue(createHelper.createRichTextString("Parser used: " + k + " (Heading) " + key + " ///// " + d + " ///// " + deadlines1.get(d)));
				j++;
			}
		}
        
        LinkedHashMap<String, List<String>> committees = new  LinkedHashMap<>();
        k = 0;
        do {
        	ArrayList<String> committeeLinks = parsers.get(k).findCommitteeLinks(linkList);
			for(String link: committeeLinks) {
				logger.debug("Getting committees from: " + link);
				committees.putAll(parsers.get(k).getOrganisers(crawler.getURLDoc(link), country));
			}
        	k++;
        } while(committees.isEmpty() && k < NUM_COMMITTEE_METHODS);
        
        String allMembers = "";
        if(!committees.isEmpty()) {
        	for(String subteam: committees.keySet()) {
        		allMembers += subteam + ": ";
				List<String> subteamMembers = committees.get(subteam);
				for(String subteamMember: subteamMembers) {
					allMembers += subteamMember + " //// ";
				}
	        	row.createCell(++j).setCellValue(createHelper.createRichTextString(allMembers));
	        	allMembers = "";
			}
        }
        
        try {
        	logger.debug("Performing addition to venues");
    		int venueID = this.sqlConnection.addToVenues(venue);
    		
    		logger.debug("Checking if conferences exists in the database");
    		// Check if the conference already exists in the database
    		int id = this.sqlConnection.checkIfExists(acronym, venueID, year);
    		
    		// If the conference exists then overwrite every value in the database for it (to be changed in the future to check which information needs changing)
    		if(id != -1) {
    			logger.debug("Overwriting the websites table in database");
    			this.sqlConnection.updateWebsites(id, acronym, title, sponsor, proceedings, description, venueID, year, antiquity, conferenceDays, mainLink);
    			logger.debug("Deleting from the deadlines table in database");
    			this.sqlConnection.deleteFromTable(id, "delete from deadlines where id = ?");
    			logger.debug("Adding to the deadlines table in database");
    			this.sqlConnection.addToDeadlines(id, deadlines);
    			logger.debug("Deleting from the committees table in database");
    			this.sqlConnection.deleteFromTable(id, "delete from committees where id = ?");
    			logger.debug("Adding to the committees table in database");
    			this.sqlConnection.addToCommittees(id, committees);
    		} else {
    			logger.debug("Adding new conferences to the websites table in database");
    			id = this.sqlConnection.addToWebsites(acronym, title, sponsor, proceedings, description, venueID, year, antiquity, conferenceDays, mainLink);
    			logger.debug("Adding new conference deadlines to the deadlines table in database");
    			this.sqlConnection.addToDeadlines(id, deadlines);
    			logger.debug("Adding new conference committees to the committees table in database");
    			this.sqlConnection.addToCommittees(id, committees);
    		}
		} catch (SQLException e) {
			logger.error("Cannot add extracted conference information to database.");
		}
			
	}
}
