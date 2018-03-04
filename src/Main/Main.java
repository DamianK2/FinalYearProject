package main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import crawler.Crawler;
import crawler.Information;
import crawler.Parser;
import crawler.Parser2;
import crawler.Parser3;
import crawler.Parser4;
import crawler.Parser5;
import crawler.Parser6;
import crawler.Parser7;
import crawler.Parser8;
import crawler.Parser9;
import database.sql;
import venue.Country;

public class Main {

	public static final ArrayList<String> URLS = new ArrayList<>(
							Arrays.asList(
										"https://icpe2018.spec.org/home.html",
										"http://lsds.hesge.ch/ISPDC2018/", 
										"https://unescoprivacychair.urv.cat/psd2018/index.php",
										"https://2018.splashcon.org/home",
										"https://conf.researchr.org/home/icgse-2018",
										"https://itrust.sutd.edu.sg/hase2017/", //TODO committees
										"http://www.ispass.org/ispass2018/",
										"https://www.computer.org/web/compsac2018",
										"https://www.isf.cs.tu-bs.de/cms/events/sefm2018/",
										"http://www.es.mdh.se/icst2018/",
										"https://icssea.org/",
										"http://www.icsoft.org/",
										"http://issre.net/",
										"https://sites.uoit.ca/ifiptm2018/index.php",
										"http://cseet2017.com/",
										"http://www.ieee-iccse.org/"
										));
			
	public static void main(String[] args) {
		//long tStart = System.currentTimeMillis();
		ArrayList<String> links = new ArrayList<String>();
		LinkedHashMap<String, LinkedHashMap<String, String>> deadlines = new LinkedHashMap<>();
		
		Country country = new Country();
		Information information = new Information();
		
		Workbook wb = new HSSFWorkbook();
        //Workbook wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet("new sheet");
        // Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue(createHelper.createRichTextString("Acronym"));
        row.createCell(1).setCellValue(createHelper.createRichTextString("Link"));
        row.createCell(2).setCellValue(createHelper.createRichTextString("Title"));
        row.createCell(3).setCellValue(createHelper.createRichTextString("Sponsors"));
        row.createCell(4).setCellValue(createHelper.createRichTextString("Proceedings"));
        row.createCell(5).setCellValue(createHelper.createRichTextString("Description"));
        row.createCell(6).setCellValue(createHelper.createRichTextString("Venue"));
        row.createCell(7).setCellValue(createHelper.createRichTextString("Antiquity"));
        row.createCell(8).setCellValue(createHelper.createRichTextString("Conference Days"));
        row.createCell(9).setCellValue(createHelper.createRichTextString("Current Year"));
        
        ArrayList<Parser> parsers = new ArrayList<>();
        int i = 0;
        Crawler crawl;
        parsers.add(new Parser(information));
        parsers.add(new Parser2(information));
        parsers.add(new Parser3(information));
        parsers.add(new Parser4(information));
        parsers.add(new Parser5(information));
        parsers.add(new Parser6(information));
        parsers.add(new Parser7(information));
        parsers.add(new Parser8(information));
        parsers.add(new Parser9(information));
        
//		sql sql = new sql();
//        sql.createConnection();
        
		for(String url: URLS) {
			links.clear();
			deadlines.clear();
			crawl = new Crawler(url);
			links = crawl.getAllLinks();
	        
	        row = sheet.createRow(i+1);
	        // TODO Use this in the database to store the link to be used with the acronym as a[href] on the webpage
	        String mainLink = links.get(0);
	        row.createCell(1).setCellValue(createHelper.createRichTextString(mainLink));
			String title = parsers.get(0).getTitle(mainLink);
			row.createCell(2).setCellValue(createHelper.createRichTextString(title));
			
			String proceedings;
			int k = 0;
			do {
				proceedings = parsers.get(k).getProceedings(links);
	        	k++;
	        } while(proceedings == "" && k < parsers.size());  
			row.createCell(4).setCellValue(createHelper.createRichTextString(proceedings));
			
			String description;
			k = 0;
			do {
	        	description = parsers.get(k).getDescription(mainLink);
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
			} while(sponsor == "" && k < parsers.size());
			
			row.createCell(3).setCellValue(createHelper.createRichTextString(sponsor));
	        
	        String venue;
	        k = 0;
			do {
				venue = parsers.get(k).getVenue(title, description, country, links);
	        	k++;
	        } while(venue == "" && k < parsers.size());	

			System.out.println("VENUE: " + venue);
	        row.createCell(6).setCellValue(createHelper.createRichTextString(venue));
	        
	        String antiquity; 
	        k = 0;
	        do {
	        	antiquity = parsers.get(k).getAntiquity(title, description, links);
	        	k++;
	        } while(antiquity == "" && k < parsers.size());
	        
	        row.createCell(7).setCellValue(createHelper.createRichTextString(antiquity));
			
			String conferenceDays;
			k = 0;
	        do {
	        	conferenceDays = parsers.get(k).getConferenceDays(title, description, links);
	        	k++;
	        } while(conferenceDays == "" && k < parsers.size());
	        row.createCell(8).setCellValue(createHelper.createRichTextString(conferenceDays));
	        
	        String year = "";
	        k = 0;
	        do {
	        	 year = parsers.get(k).getConferenceYear(conferenceDays, title);
	        	 k++;
	        } while(year == "" && k < parsers.size());
	       
	        row.createCell(9).setCellValue(createHelper.createRichTextString(year));
	        
//	        k = 0;
//	        do {
//	        	deadlines = parsers.get(k).getDeadlines(links);	
//	        	k++;
//	        } while(deadlines.isEmpty() && k < parsers.size());
//	        	
//	        k--;
//	        int j = 10;
//
//	        for(String key: deadlines.keySet()) {
////				System.out.println("Heading: " + key);
//				LinkedHashMap<String, String> deadlines1 = deadlines.get(key);
//				for(String d: deadlines1.keySet()) {
////					System.out.println(d + ": " + deadlines1.get(d));
//					row.createCell(j).setCellValue(createHelper.createRichTextString("Parser used: " + k + " (Heading) " + key + " ///// " + d + " ///// " + deadlines1.get(d)));
//					j++;
//				}
//			}
	        
	        LinkedHashMap<String, List<String>> committees;
	        k = 0;
	        do {
	        	committees = parsers.get(k).getOrganisers(links, country);
	        	k++;
	        } while(committees.isEmpty() && k < parsers.size());
	        
	        int j = 10;
	        String allMembers = "";
	        if(!committees.isEmpty()) {
	        	for(String subteam: committees.keySet()) {
	        		allMembers += subteam + ": ";
					List<String> subteamMembers = committees.get(subteam);
					for(String subteamMember: subteamMembers) {
						allMembers += subteamMember + " //// ";
					}
					System.out.println("(wtf) " + allMembers);
		        	row.createCell(++j).setCellValue(createHelper.createRichTextString(allMembers));
		        	allMembers = "";
				}
	        }	
			i++;
//	        try {
//				sql.addNewConference(acronym, title, sponsor, proceedings, description, venue, year, antiquity, conferenceDays, committees, deadlines);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
		}
        
        // Create the output file
        FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream("workbook.xls");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// Write the output of the program to the file
        try {
			wb.write(fileOut);
			fileOut.close();
			wb.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        
		
//      links = test.getAllLinks();
//		 
//		print("\nLinks: (%d)", links.size());
//		int i = 1;
//		for (String link : links) {
//			print("%d. * a: <%s>", i, link);
//			i++;
//		}
		
//		for (String link : links) {
//			if(link.matches(".*venue.*"))
//				print("%d. * a: <%s>", i, link);
//			i++;
//		}
		
//		long tEnd = System.currentTimeMillis();
//		long tDelta = tEnd - tStart;
//		double elapsedSeconds = tDelta / 1000.0;
//		print("Time taken to fetch links: %f seconds", elapsedSeconds);
		

	}
	
//	private static void print(String msg, Object... args) {
//	    System.out.println(String.format(msg, args));
//	}
}
