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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.Information;

class Parser6Test {
	private Parser parser;
	
	@BeforeEach
	void setup() {
		parser = new Parser6(new Information(), new Crawler());
	}

	@AfterEach
	void teardown() {
		parser = null;
	}
	
	@Test
	void testGetDeadlines() {
		ArrayList<String> links = new ArrayList<String>(Arrays.asList("https://sites.uoit.ca/ifiptm2018/important-dates/index.php"));
		LinkedHashMap<String, LinkedHashMap<String, String>> deadlineTypes = parser.getDeadlines(links);
		assertTrue(deadlineTypes.containsKey("Paper Submission"));
		assertTrue(deadlineTypes.containsKey("Workshop and Tutorial"));
		assertTrue(deadlineTypes.containsKey("Graduate Symposium Proposals "));
		assertTrue(deadlineTypes.containsKey("Poster and Demonstration"));

		LinkedHashMap<String, String> deadlines = deadlineTypes.get("Paper Submission");
		assertTrue(deadlines.containsKey("Camera-ready version:  "));
		assertEquals("April 24, 2018", deadlines.get("Camera-ready version:  "));
		deadlines = deadlineTypes.get("Workshop and Tutorial");
		assertTrue(deadlines.containsKey("Workshop Proposal Submission: "));
		assertEquals("March 09, 2018", deadlines.get("Workshop Proposal Submission: "));
		assertTrue(deadlines.containsKey("Accept Notification: "));
		assertEquals("April 21, 2018", deadlines.get("Accept Notification: "));
		deadlines = deadlineTypes.get("Poster and Demonstration");
		assertTrue(deadlines.containsKey("Abstract submission due: "));
		assertEquals("March 15, 2018", deadlines.get("Abstract submission due: "));
		assertTrue(deadlines.containsKey("Revised abstract due: "));
		assertEquals("May 15, 2018", deadlines.get("Revised abstract due: "));
		
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("https://research.spec.org/fileadmin/user_upload/documents/icpe_2018/ICPE-2018_Sponsorship.pdf"))));
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("http://www.icsoft.org/Important-dates"))));
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("https://ieeecompsac.computer.org/2018/important-dates/"))));
	}
	
	@Test
	void testGetAntiquity() {
		File hase = new File("TestPages/HASE2017_history.html");
		Document doc = null;
		try {
			doc = Jsoup.parse(hase, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		assertEquals("Eighteenth", parser.getAntiquity("", "", doc));
		assertEquals("", parser.getAntiquity("", "", null));
	}

	@Test
	void testGetConferenceDays() {
		File icst = new File("TestPages/ICST2018.html");
		File hase = new File("TestPages/HASE2017_dates.html");
		File ispass = new File("TestPages/ISPASS2018.html");
		Document doc = null;
		Document doc2 = null;
		Document doc3 = null;
		try {
			doc = Jsoup.parse(icst, "UTF-8");
			doc2 = Jsoup.parse(hase, "UTF-8");
			doc3 = Jsoup.parse(ispass, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		assertEquals("April 9 – 13, 2018", parser.getConferenceDays("", "", doc));
		assertEquals("12 – 14 January 2017", parser.getConferenceDays("", "", doc2));
		assertEquals("", parser.getConferenceDays("", "", doc3));
		assertEquals("", parser.getConferenceDays("", "", null));
	}

}
