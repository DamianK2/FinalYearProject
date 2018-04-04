package crawler;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.Information;
import venue.Country;

class Parser3Test {

	private Parser parser; 

	@BeforeEach
	void setup() {
		parser = new Parser3(new Information(), new Crawler());
	}

	@AfterEach
	void teardown() {
		parser = null;
	}

	@Test
	void testGetDescription() {
		File icpe = new File("TestPages/ICPE2018.html");
		File ispdc = new File("TestPages/ISPDC2018.html");
		Document doc = null;
		Document doc2 = null;
		try {
			doc = Jsoup.parse(icpe, "UTF-8");
			doc2 = Jsoup.parse(ispdc, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		assertEquals("", parser.getDescription(doc));
		assertEquals("The 17th IEEE International Symposium on Parallel and "
				+ "Distributed Computing, 25-28 June 2018, Geneva, Switzerland", parser.getDescription(doc2));
		assertEquals("", parser.getDescription(null));

	}

	@Test
	void testGetVenue() {
		Country country = new Country();

		assertEquals("Germany", parser.getVenue("", " Germany", country, null));
		assertEquals("Switzerland", parser.getVenue("", "Parsing conferences to get Switzerland.", country, null));
		assertEquals("Spain", parser.getVenue("", "John Doe went on vacation to Spain.", country, null));
		assertEquals("", parser.getVenue("", "", country, null));
	}

	@Test
	void testGetDeadlines() {
		ArrayList<String> links = new ArrayList<String>(Arrays.asList("https://unescoprivacychair.urv.cat/psd2018/index.php",
				"http://www.icsoft.org/ImportantDates.aspx"));
		LinkedHashMap<String, LinkedHashMap<String, String>> deadlineTypes = parser.getDeadlines(links);
		assertTrue(deadlineTypes.containsKey("0"));
		assertTrue(deadlineTypes.containsKey("2"));
		assertTrue(deadlineTypes.containsKey("4"));

		LinkedHashMap<String, String> deadlines = deadlineTypes.get("0");
		assertTrue(deadlines.containsKey("Submission deadline"));
		assertEquals("MAY 27, 2018", deadlines.get("Submission deadline"));
		deadlines = deadlineTypes.get("1");
		assertTrue(deadlines.containsKey("Acceptance notification"));
		assertEquals("June 29, 2018", deadlines.get("Acceptance notification"));
		deadlines = deadlineTypes.get("3");
		assertTrue(deadlines.containsKey("USB-only submission deadline"));
		assertEquals("July 9, 2018", deadlines.get("USB-only submission deadline"));
		deadlines = deadlineTypes.get("4");
		assertTrue(deadlines.containsKey("USB-only acceptance notification"));
		assertEquals("July 30, 2018", deadlines.get("USB-only acceptance notification"));
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("https://ieeecompsac.computer.org/2018/important-dates/"))));
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("https://research.spec.org/fileadmin/user_upload/documents/icpe_2018/ICPE-2018_Sponsorship.pdf"))));
	}

	@Test
	void testGetAntiquity() {
		File icpe = new File("TestPages/ICPE2018.html");
		File psd = new File("TestPages/PSD2018.html");
		File icsoft = new File("TestPages/ICSOFT2018.html");
		Document doc = null;
		Document doc2 = null;
		Document doc3 = null;
		try {
			doc = Jsoup.parse(icpe, "UTF-8");
			doc2 = Jsoup.parse(psd, "UTF-8");
			doc3 = Jsoup.parse(icsoft, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		assertEquals("", parser.getAntiquity("", "", doc));
		assertEquals("Eighth", parser.getAntiquity("", "", doc2));
		assertEquals("", parser.getAntiquity("", "", doc3));
		assertEquals("", parser.getAntiquity("", "", null));
	}

	@Test
	void testGetConferenceDays() {
		File icpe = new File("TestPages/ICPE2018.html");
		File psd = new File("TestPages/PSD2018.html");
		Document doc = null;
		Document doc2 = null;
		try {
			doc = Jsoup.parse(icpe, "UTF-8");
			doc2 = Jsoup.parse(psd, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		assertEquals("", parser.getConferenceDays("", "", null));
		assertEquals("", parser.getConferenceDays("La La La catch this date: 25-27 June 2018, if you can", "", doc));
		assertEquals("September 26-28, 2018", parser.getConferenceDays("", "", doc2));
	}	

	@Test
	void testgetOrganisers() {
		Country country = new Country();
		File hase = new File("TestPages/HASE2017_committee.html");
		File splash = new File("TestPages/SPLASH2018_committee.html");
		Document doc = null;
		Document doc2 = null;
		try {
			doc = Jsoup.parse(hase, "UTF-8");
			doc2 = Jsoup.parse(splash, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		LinkedHashMap<String, List<String>> committees = parser.getOrganisers(doc, country);
		assertTrue(committees.containsKey("General Chairs"));
		assertTrue(committees.containsKey("Finance Chair"));
		assertTrue(committees.containsKey("Panel Chairs"));
		assertTrue(committees.containsKey("Publicity Chairs"));

		List<String> members = committees.get("General Chairs");
		assertEquals("Bojan Cukic, University of North Carolina at Charlotte", members.get(1));
		members = committees.get("Panel Chairs");
		assertEquals("Kenji Yoshigoe, University of Arkansas at Little Rock", members.get(1));
		members = committees.get("Publicity Chairs");
		assertEquals("Charles Kamhoua, Information Directorate, Air Force Research Laboratory", members.get(0));

		assertTrue(parser.getOrganisers(doc2, country).isEmpty());
	}
}
