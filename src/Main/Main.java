package main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import crawler.Crawler;
import crawler.Parser;
import crawler.Parser2;
import crawler.Parser3;
import crawler.Parser4;
import crawler.Parser5;
import crawler.Parser6;
import crawler.Parser7;
import crawler.Parser8;
import crawler.Parser9;
import database.Conference;
import database.Information;
import venue.Country;

public class Main {
	final static int MAX_NOF_THREADS = 10;
	final static Semaphore semaphore = new Semaphore(MAX_NOF_THREADS);
	private static ArrayList<Thread> threads = new ArrayList<>();
	private static ArrayList<Double> times = new ArrayList<>();
	
	public static final ArrayList<String> URLS = new ArrayList<>(
							Arrays.asList(
										"https://icpe2018.spec.org/home.html",
										"http://lsds.hesge.ch/ISPDC2018/",
										"https://unescoprivacychair.urv.cat/psd2018/index.php",
										"https://2018.splashcon.org/home",
										"https://pldi18.sigplan.org/home",
										"https://2018.ecoop.org/",
										"https://2018.fseconference.org/home",
										"https://www.icse2018.org/",
										"https://conf.researchr.org/home/issta-2018",
										"https://conf.researchr.org/home/icgse-2018",
//										"https://itrust.sutd.edu.sg/hase2017/",
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
//		long tStartOverall = System.currentTimeMillis();
//		
//		for(int k = 0; k < 5; k++) {
			long tStart = System.currentTimeMillis();
			
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
	        int rowNumber = 1;
	        Crawler crawler = new Crawler();
	        parsers.add(new Parser(information, crawler));
	        parsers.add(new Parser2(information, crawler));
	        parsers.add(new Parser3(information, crawler));
	        parsers.add(new Parser4(information, crawler));
	        parsers.add(new Parser5(information, crawler));
	        parsers.add(new Parser6(information, crawler));
	        parsers.add(new Parser7(information, crawler));
	        parsers.add(new Parser8(information, crawler));
	        parsers.add(new Parser9(information, crawler));
	        
	        Conference sqlConnection = new Conference();
	        
	      // Create threads for each link to decrease crawling time
	      Thread thread;
	      for(String url: URLS) {
	        	thread = new Thread(new Worker(url, crawler, country, row, sheet, createHelper, parsers, rowNumber, sqlConnection));
	        	threads.add(thread);
	        	thread.start();
	        	rowNumber++;
	      }
	      
	      // Join the threads to prevent the program from finishing before the threads do
	      for(int i = 0; i < threads.size(); i++) {
				try {
					threads.get(i).join();
				} catch (InterruptedException e) {
					System.out.println("Something went wrong then joining the threads.");
					e.printStackTrace();
				}
	      }
	        
//			for(String url: information.getLinks()) {
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
	        
	        sqlConnection.closeConnection();
			
			long tEnd = System.currentTimeMillis();
			long tDelta = tEnd - tStart;
			double elapsedSeconds = tDelta / 1000.0;
//			times.add(elapsedSeconds);
//		}
//		
//		int i = 1;
//		for(double t: times) {
//			System.out.println("Time taken for " + i + " iteration: " + t);
//			i++;
//		}
//		
//		long tEndOverall = System.currentTimeMillis();
//		long tDelta = tEndOverall - tStartOverall;
//		double elapsedSeconds = tDelta / 1000.0;
//		System.out.println("Time taken to fetch information 10 times: " + elapsedSeconds + " seconds");
//		elapsedSeconds = elapsedSeconds / 10.0;
		System.out.println("Time taken to fetch information (average): " + elapsedSeconds + " seconds");
	}
}
