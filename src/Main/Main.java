package Main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import Crawler.Crawler;
import Crawler.Parser;

public class Main {

	public static final String[] URLS = {"https://icpe2018.spec.org/home.html", "http://lsds.hesge.ch/ISPDC2018/" ,"https://unescoprivacychair.urv.cat/psd2018/"};
	
	public static void main(String[] args) {
		long tStart = System.currentTimeMillis();
		ArrayList<String> links = new ArrayList<String>();
		
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
		
        int i;
        Crawler crawl;
        Parser parser;
		for(String url: URLS) {
			links.clear();
			i= 1;
			crawl = new Crawler(url);
			links = crawl.getAllLinks();
			parser = new Parser(links);
			row = sheet.createRow(i+1);
			row.createCell(0).setCellValue(createHelper.createRichTextString(url));
	        row.createCell(1).setCellValue(createHelper.createRichTextString(parser.getTitle()));
	        row.createCell(2).setCellValue(createHelper.createRichTextString(parser.getDescription()));
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        try {
			wb.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
