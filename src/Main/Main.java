package Main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import Crawler.Crawler;
import Crawler.Parser;
import venue.Country;

public class Main {

	public static final String[] URLS = {"https://icpe2018.spec.org/home.html", "http://lsds.hesge.ch/ISPDC2018/", "https://unescoprivacychair.urv.cat/psd2018/index.php"};
	
	public static void main(String[] args) {
		//long tStart = System.currentTimeMillis();
		ArrayList<String> links = new ArrayList<String>();
		ArrayList<String> deadlines = new ArrayList<String>();
		
		Country country = new Country();
		
		// Look through title and description to find antiques if not there then 
		
		Workbook wb = new HSSFWorkbook();
        //Workbook wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet("new sheet");
        // Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue(createHelper.createRichTextString("Link"));
        row.createCell(1).setCellValue(createHelper.createRichTextString("Title"));
        row.createCell(2).setCellValue(createHelper.createRichTextString("Description"));
        row.createCell(3).setCellValue(createHelper.createRichTextString("Venue"));
        row.createCell(4).setCellValue(createHelper.createRichTextString("Submission deadline"));
        row.createCell(5).setCellValue(createHelper.createRichTextString("Notification deadline"));
        row.createCell(6).setCellValue(createHelper.createRichTextString("Camera ready deadline"));
        row.createCell(7).setCellValue(createHelper.createRichTextString("Work in progress papers"));
        row.createCell(8).setCellValue(createHelper.createRichTextString("Tool papers"));
        row.createCell(9).setCellValue(createHelper.createRichTextString("Workshop papers"));
        row.createCell(10).setCellValue(createHelper.createRichTextString("Current Year"));
        row.createCell(11).setCellValue(createHelper.createRichTextString("Antiquity"));
        
        int i = 0;
        Crawler crawl;
        Parser parser;
		for(String url: URLS) {
			links.clear();
			deadlines.clear();
			crawl = new Crawler(url);
			links = crawl.getAllLinks();
			parser = new Parser(links);
			row = sheet.createRow(i+1);
			row.createCell(0).setCellValue(createHelper.createRichTextString(url));
			String title = parser.getTitle();
	        row.createCell(1).setCellValue(createHelper.createRichTextString(title));
	        String description = parser.getDescription();
	        row.createCell(2).setCellValue(createHelper.createRichTextString(description));
	        String venue = parser.getVenue(title, description, country);
	        row.createCell(3).setCellValue(createHelper.createRichTextString(venue));
	        deadlines = parser.getDeadlines();				
	        int j = 4;
	        for(String deadline: deadlines) {
	        	System.out.println("(wtf) " + deadline);
	        	row.createCell(j).setCellValue(createHelper.createRichTextString(deadline));
	        	j++;
	        }
	        deadlines.clear();
	        deadlines = parser.getAdditionalDeadlineInfo();
	        for(String deadline: deadlines) {
	        	System.out.println("(wtf) " + deadline);
	        	row.createCell(j).setCellValue(createHelper.createRichTextString(deadline));
	        	j++;
	        }
	        String year = parser.getConferenceYear(title);
	        row.createCell(10).setCellValue(createHelper.createRichTextString(year));
	        String antiquity = parser.getAntiquity(description);
	        row.createCell(11).setCellValue(createHelper.createRichTextString(antiquity));
			i++;
		}
        
        // Write the output to a file
        FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream("workbook.xls");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
}
