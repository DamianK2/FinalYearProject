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
import org.junit.jupiter.api.Test;

import venue.Country;

class Parser3Test {

	private Parser parser = new Parser3(new Information());
	
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
		System.out.println(parser.getOrganisers(doc, country));
		System.out.println(parser.getOrganisers(doc2, country));
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
