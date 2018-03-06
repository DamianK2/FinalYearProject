package crawler;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import venue.Country;

class Parser4Test {
	
	private Parser parser = new Parser4(new Information());

	@Test
	void testGetDescription() {
		File icpe = new File("TestPages/ICPE2018.html");
		File splash = new File("TestPages/SPLASH2018.html");
		Document doc = null;
		Document doc2 = null;
		try {
			doc = Jsoup.parse(icpe, "UTF-8");
			doc2 = Jsoup.parse(splash, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		assertEquals("", parser.getDescription(doc));
		assertEquals("Welcome to SPLASH 2018! The ACM SIGPLAN conference on Systems,"
				+ " Programming, Languages and Applications: Software for Humanity "
				+ "(SPLASH) embraces all aspects of software construction and delivery "
				+ "to make it the premier conference at the intersection of programming, "
				+ "languages, and software engineering. SPLASH 2018 will take place in "
				+ "Boston, Massachusetts, USA from Sun 4 - Fri 9 November 2018.", parser.getDescription(doc2));
		assertEquals("", parser.getDescription(null));
	}
	
	@Test
	void testGetVenue() {
		Country country = new Country();
		File icpe = new File("TestPages/ICPE2018_venue.html");
		File ispdc = new File("TestPages/ISPDC2018_venue.html");
		File psd = new File("TestPages/PSD2018_venue.html");
		Document doc = null;
		Document doc2 = null;
		Document doc3 = null;
		try {
			doc = Jsoup.parse(icpe, "UTF-8");
			doc2 = Jsoup.parse(ispdc, "UTF-8");
			doc3 = Jsoup.parse(psd, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		assertEquals("", parser.getVenue("", "", country, doc));
		assertEquals("Switzerland", parser.getVenue("", "", country, doc2));
		assertEquals("Spain", parser.getVenue("", "", country, doc3));
		assertEquals("", parser.getVenue("", "", country, null));
	}
	
	//TODO test the map
//	@Test
//	void testGetDeadlines() {
//		ArrayList<String> deadlines = new ArrayList<>(Arrays.asList("MAY 27, 2018", "June 29, 2018", ""));
//		ArrayList<String> empty = new ArrayList<>();
//		
//		assertEquals(empty, parser.getDeadlines(links1));
//		assertEquals(empty, parser.getDeadlines(links2));
//		assertEquals(deadlines, parser.getDeadlines(links3));
//	}
	
	@Test
	void testGetAntiquity() {
		File pldi = new File("TestPages/PLDI2018.html");
		File icpe = new File("TestPages/ICPE2018.html");
		File psd = new File("TestPages/PSD2018.html");
		Document doc = null;
		Document doc2 = null;
		Document doc3 = null;
		try {
			doc = Jsoup.parse(pldi, "UTF-8");
			doc2 = Jsoup.parse(icpe, "UTF-8");
			doc3 = Jsoup.parse(psd, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		assertEquals("Fifth", parser.getAntiquity("", "", doc));
		assertEquals("", parser.getAntiquity("", "", doc2));
		assertEquals("", parser.getAntiquity("", "", doc3));
		assertEquals("", parser.getAntiquity("", "", null));
	}

	@Test
	void testGetConferenceDays() {
		File pldi = new File("TestPages/PLDI2018.html");
		File splash = new File("TestPages/SPLASH2018.html");
		File psd = new File("TestPages/PSD2018.html");
		Document doc = null;
		Document doc2 = null;
		Document doc3 = null;
		try {
			doc = Jsoup.parse(pldi, "UTF-8");
			doc2 = Jsoup.parse(splash, "UTF-8");
			doc3 = Jsoup.parse(psd, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		assertEquals("", parser.getConferenceDays("", "", doc));
		assertEquals("Sun 4 - Fri 9 November 2018", parser.getConferenceDays("", "", doc2));
		assertEquals("", parser.getConferenceDays("", "", doc3));
		assertEquals("", parser.getConferenceDays("", "", null));
	}

}
