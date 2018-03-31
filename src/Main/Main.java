package main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	private static int threads_completed = 0;
	final static Semaphore semaphore = new Semaphore(MAX_NOF_THREADS);
	private static ArrayList<Thread> threads = new ArrayList<>();
	private static ArrayList<Double> times = new ArrayList<>(); // Testing purposes
	static Logger logger = LogManager.getLogger(Main.class);

	public static void main(String[] args) {
		long tStartOverall = System.currentTimeMillis(); // Testing purposes

		for (int k = 0; k < 10; k++) { // Testing purposes
			System.out.println();
			System.out.println("==========Run " + k + "==========");
			long tStart = System.currentTimeMillis();

			Country country = new Country();
			logger.debug("Creating connection to the database for information and conference");
			Information information = new Information();
			Conference sqlConnection = new Conference();

			Workbook wb = new HSSFWorkbook();
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

			// Create threads for each link to decrease crawling time
			Thread thread;
			for (String url : information.getLinks()) {
				thread = new Thread(
						new Worker(url, crawler, country, row, sheet, createHelper, parsers, rowNumber, sqlConnection));
				threads.add(thread);
				logger.debug("Starting thread for: " + url);
				thread.start();
				rowNumber++;
			}

			logger.debug("Joining all threads");
			// Join the threads to prevent the program from finishing before the threads do
			for (int i = 0; i < threads.size(); i++) {
				try {
					threads.get(i).join();
				} catch (InterruptedException e) {
					logger.error("Thread was interuptted\n" + e.getMessage());
				}
			}

			logger.debug("Creating file to write information into");
			// Create the output file
			FileOutputStream fileOut = null;
			try {
				fileOut = new FileOutputStream("workbook.xls");
			} catch (FileNotFoundException e) {
				logger.fatal("Couldn't create xls file\n" + e.getMessage());
			}

			// Write the output of the program to the file
			try {
				logger.debug("Writing workbook to file");
				wb.write(fileOut);
				logger.debug("Closing file and workbook");
				fileOut.close();
				wb.close();
			} catch (IOException e) {
				logger.fatal("Couldn't write to xls file or close the workbook/file\n" + e.getMessage());
			}

			logger.debug("Closing connection to the database for information and conference");
			information.closeConnection();
			sqlConnection.closeConnection();

			long tEnd = System.currentTimeMillis();
			long tDelta = tEnd - tStart;
			double elapsedSeconds = tDelta / 1000.0;
			times.add(elapsedSeconds);
		}

		int i = 1;
		for (double t : times) {
			System.out.println("Time taken for " + i + " iteration: " + t);
			i++;
		}

		long tEndOverall = System.currentTimeMillis();
		long tDelta = tEndOverall - tStartOverall;
		double elapsedSeconds = tDelta / 1000.0;
		System.out.println("Time taken to extract information 10 times: " +
				elapsedSeconds + " seconds");
		elapsedSeconds = elapsedSeconds / 10.0;
		System.out.println("Time taken to extract information: " + elapsedSeconds + " seconds");
	}

	public static synchronized void updatePercentage() {
		threads_completed++;
		int percentage = (int) Math.round(((double) threads_completed/(double) threads.size()) * 100.0);
		System.out.println("Completed: " + percentage + "%");
	}
}
