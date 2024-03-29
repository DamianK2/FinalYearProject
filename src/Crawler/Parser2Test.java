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

class Parser2Test {
	
	private Parser parser; 
	
	@BeforeEach
	void setup() {
		parser = new Parser2(new Information(), new Crawler());
	}
	
	@AfterEach
	void teardown() {
		parser = null;
	}
	
	
	@Test
	void testGetAcronym() {
		assertEquals("ICPE", parser.getAcronym("", "ICPE"));
		assertEquals("ICPE", parser.getAcronym("", "ICPE 2018"));
		assertEquals("ICPE", parser.getAcronym("", "Come join us. ICPE is a conference"));
		assertEquals("", parser.getAcronym("", "Come join us. ACM is not a conference."));
	}
	
	@Test
	void testGetSponsors() {
		assertEquals("", parser.getSponsors("John Doe likes kitesufring.", "Kitesurfing is a style of kiteboarding specific to wave riding, which uses standard surfboards or boards shaped specifically for the purpose."));
		assertEquals("ACM/SPEC", parser.getSponsors("", "In this title we can find ACM. We can also find SPEC"));
		assertEquals("", parser.getSponsors("", "In this title we can't find any sponsor."));
		assertEquals("UNESCO", parser.getSponsors("", "In this title we can find UNESCO"));
	}
	
	@Test
	void testGetDescription() {
		File icpe = new File("TestPages/ICPE2018.html");
		File psd = new File("TestPages/PSD2018.html");
		File compsac = new File("TestPages/COMPSAC2018.html");
		Document doc = null;
		Document doc2 = null;
		Document doc3 = null;
		try {
			doc = Jsoup.parse(icpe, "UTF-8");
			doc2 = Jsoup.parse(psd, "UTF-8");
			doc3 = Jsoup.parse(compsac, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		assertEquals("", parser.getDescription(doc));
		assertEquals("", parser.getDescription(doc2));
		assertEquals("Staying Smarter in a Smartening World\n" + 
				"Computer technologies are producing profound changes in society."
				+ " Emerging developments in areas such as Deep Learning, supported "
				+ "by increasingly powerful and increasingly miniaturized hardware, "
				+ "are beginning to be deployed in architectures, systems, and applications "
				+ "that are redefining the relationships between humans and technology. As", parser.getDescription(doc3));
	}
	
	@Test
	void testGetVenue() {
		Country country = new Country();

		assertEquals("Germany", parser.getVenue(" Germany", "", country, null));
		assertEquals("Switzerland", parser.getVenue("Parsing conferences to get Switzerland.", "", country, null));
		assertEquals("Spain", parser.getVenue("John Doe went on vacation to Spain.", "", country, null));
		assertEquals("", parser.getVenue("", "", country, null));
	}
	
	@Test
	void testGetDeadlines() {
		ArrayList<String> links = new ArrayList<String>(Arrays.asList("http://lsds.hesge.ch/ISPDC2018/",
				"http://www.icsoft.org/ImportantDates.aspx"));
		LinkedHashMap<String, LinkedHashMap<String, String>> deadlineTypes = parser.getDeadlines(links);
		assertTrue(deadlineTypes.containsKey("0"));
		assertTrue(deadlineTypes.containsKey("3"));
		assertTrue(deadlineTypes.containsKey("5"));

		LinkedHashMap<String, String> deadlines = deadlineTypes.get("0");
		assertTrue(deadlines.containsKey("Abstracts submissions deadline"));
		assertEquals("February 12, 2018", deadlines.get("Abstracts submissions deadline"));
		deadlines = deadlineTypes.get("2");
		assertTrue(deadlines.containsKey("Notification of authors"));
		assertEquals("April 10, 2018", deadlines.get("Notification of authors"));
		deadlines = deadlineTypes.get("4");
		assertTrue(deadlines.containsKey("Author registration"));
		assertEquals("April 30, 2018", deadlines.get("Author registration"));
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("https://ieeecompsac.computer.org/2018/important-dates/"))));
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("https://research.spec.org/fileadmin/user_upload/documents/icpe_2018/ICPE-2018_Sponsorship.pdf"))));
	}
	
	@Test
	void testGetConferenceYear() {
		assertEquals("2018", parser.getConferenceYear("", "19 April 2018"));
		assertEquals("2019", parser.getConferenceYear("", "27-29 June 2019"));
		assertEquals("2018", parser.getConferenceYear("", "11-13 February 2018"));
		assertEquals("", parser.getConferenceYear("", "11 February"));
	}
	
	@Test
	void testGetAntiquity() {
		assertEquals("Ninth", parser.getAntiquity("Ninth", "", null));
		assertEquals("Seventeenth", parser.getAntiquity("17th", "", null));
		assertEquals("", parser.getAntiquity("", "", null));
		assertEquals("Twentieth", parser.getAntiquity("20th", "", null));
		assertEquals("Forty-Fourth", parser.getAntiquity("44th", "", null));
	}

	@Test
	void testGetConferenceDays() {
		assertEquals("", parser.getConferenceDays("", "", null));
		assertEquals("25-27 June 2018", parser.getConferenceDays("", "La La La catch this date: 25-27 June 2018, if you can", null));
		assertEquals("", parser.getConferenceDays("", "25                                  - 27 June 2018", null));
	}

	@Test
	void testgetOrganisers() {
		Country country = new Country();
		File icpe = new File("TestPages/SPLASH2018_committee.html");
		File ispdc = new File("TestPages/ISPDC2018_committee.html");
		Document doc = null;
		Document doc2 = null;
		try {
			doc = Jsoup.parse(icpe, "UTF-8");
			doc2 = Jsoup.parse(ispdc, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		
		LinkedHashMap<String, List<String>> committees = parser.getOrganisers(doc, country);
		assertTrue(committees.containsKey("OOPSLA PC Chair"));
		assertTrue(committees.containsKey("SPLASH-I Chair"));
		assertTrue(committees.containsKey("Video Chair"));
		assertTrue(committees.containsKey("Sponsorship Chair"));
		assertTrue(committees.containsKey("Artifact Evaluation Chair"));
		
		List<String> members = committees.get("Artifact Evaluation Chair");
		assertEquals("Sam Tobin-Hochstadt , Indiana University", members.get(0));
		members = committees.get("Video Chair");
		assertEquals("Leif Andersen , Northeastern University, United States", members.get(1));
		members = committees.get("OOPSLA PC Chair");
		assertEquals("Manu Sridharan , Uber, United States", members.get(0));
		
		assertTrue(parser.getOrganisers(doc2, country).isEmpty());
	}
}
