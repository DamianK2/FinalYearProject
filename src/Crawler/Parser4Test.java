package crawler;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import venue.Country;

class Parser4Test {
	
	private Parser parser = new Parser4(new Information(), new Crawler());

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
	
	@Test
	void testGetDeadlines() {
		System.out.println(parser.getDeadlines(new ArrayList<String>(Arrays.asList("https://2018.splashcon.org/home"))));
		
		ArrayList<String> links = new ArrayList<String>(Arrays.asList("https://2018.splashcon.org/home",
				"http://www.icsoft.org/ImportantDates.aspx"));
		LinkedHashMap<String, LinkedHashMap<String, String>> deadlineTypes = parser.getDeadlines(links);
		assertTrue(deadlineTypes.containsKey("0"));
		assertTrue(deadlineTypes.containsKey("2"));
		assertTrue(deadlineTypes.containsKey("5"));
		assertTrue(deadlineTypes.containsKey("6"));
		assertTrue(deadlineTypes.containsKey("7"));
		assertFalse(deadlineTypes.containsKey("8"));

		LinkedHashMap<String, String> deadlines = deadlineTypes.get("0");
		assertTrue(deadlines.containsKey(" OOPSLA Paper submission "));
		assertEquals("Mon 16 Apr 2018", deadlines.get(" OOPSLA Paper submission "));
		deadlines = deadlineTypes.get("2");
		assertTrue(deadlines.containsKey(" Onward! 2018 Papers Submission Deadline "));
		assertEquals("Mon 23 Apr 2018", deadlines.get(" Onward! 2018 Papers Submission Deadline "));
		deadlines = deadlineTypes.get("3");
		assertTrue(deadlines.containsKey(" SPLASH-I Proposals Due "));
		assertEquals("Fri 18 May 2018", deadlines.get(" SPLASH-I Proposals Due "));
		deadlines = deadlineTypes.get("5");
		assertTrue(deadlines.containsKey(" Onward! 2018 Essays First Notification "));
		assertEquals("Mon 11 Jun 2018", deadlines.get(" Onward! 2018 Essays First Notification "));
		deadlines = deadlineTypes.get("7");
		assertTrue(deadlines.containsKey(" GPCE 2018 Abstract Submission All important dates"));
		assertEquals("Fri 29 Jun 2018", deadlines.get(" GPCE 2018 Abstract Submission All important dates"));
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("https://research.spec.org/fileadmin/user_upload/documents/icpe_2018/ICPE-2018_Sponsorship.pdf"))));
	}
	
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
