package crawler;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import venue.Country;

class ParserTest {

	private Parser parser = new Parser(new Information());
	
	@Test
	void testGetTitle() {
		File ispass = new File("TestPages/ISPASS2018.html");
		File psd = new File("TestPages/PSD2018.html");
		Document doc = null;
		Document doc2 = null;
		try {
			doc = Jsoup.parse(ispass, "UTF-8");
			doc2 = Jsoup.parse(psd, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		assertEquals("ISPASS-2018 Home", parser.getTitle(doc));
		assertEquals("PSD2018 - Privacy in Statistical Databases - UNESCO Privacy Chair", parser.getTitle(doc2));

	}
	
	@Test
	void testGetAcronym() {
		assertEquals("ICPE", parser.getAcronym("ICPE", ""));
		assertEquals("ICPE", parser.getAcronym("ICPE 2018", ""));
		assertEquals("ICPE", parser.getAcronym("Come join us. ICPE is a conference.", ""));
		assertEquals("", parser.getAcronym("Come join us. ACM is not a conference.", ""));
	}
	
	@Test
	void testGetSponsors() {
		assertEquals("", parser.getSponsors("John Doe likes kitesufring.", "Kitesurfing is a style of kiteboarding specific to wave riding, which uses standard surfboards or boards shaped specifically for the purpose."));
		assertEquals("ACM/SPEC", parser.getSponsors("In this title we can find ACM. We can also find SPEC", ""));
		assertEquals("", parser.getSponsors("In this title we can't find any sponsor.", ""));
		assertEquals("UNESCO", parser.getSponsors("In this title we can find UNESCO", ""));
	}

	@Test
	void testGetProceedings() {
		File icpe = new File("TestPages/ICPE2018_proceedings.html");
		File psd = new File("TestPages/PSD2018_proceedings.html");
		Document doc = null;
		Document doc2 = null;
		try {
			doc = Jsoup.parse(icpe, "UTF-8");
			doc2 = Jsoup.parse(psd, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		assertEquals("ACM", parser.getProceedings(doc));
		assertEquals("Springer", parser.getProceedings(doc2));
	}
	
	@Test
	void testFindProceedingLinks() {
		assertEquals(new ArrayList<String>(Arrays.asList("https://icpe2018.spec.org/submissions.html", 
				"https://unescoprivacychair.urv.cat/psd2018/index.php?m=proceedings", 
				"http://www.icsoft.org/CallForPapers.aspx")), 
				parser.findProceedingLinks(new ArrayList<String>(Arrays.asList("https://unescoprivacychair.urv.cat/psd2018/index.php",
						"https://icpe2018.spec.org/submissions.html",
						"https://unescoprivacychair.urv.cat/psd2018/index.php?m=topics",
						"https://unescoprivacychair.urv.cat/psd2018/index.php?m=proceedings",
						"http://www.icsoft.org/CallForPapers.aspx"))));
	}

	@Test
	void testGetDescription() {
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
		assertEquals("Ninth ACM/SPEC International Conference on Performance Engineering, ICPE 2018"
				+ " - A Joint Meeting of WOSP/SIPEW sponsored by ACM SIGMETRICS and ACM SIGSOFT in"
				+ " Cooperation with SPEC.", parser.getDescription(doc));
		assertEquals("", parser.getDescription(doc2));
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
		assertEquals("Germany", parser.getVenue("", "", country, doc));
		assertEquals("Switzerland", parser.getVenue("", "", country, doc2));
		assertEquals("Spain", parser.getVenue("", "", country, doc3));
		assertEquals("", parser.getVenue("", "", country, null));
	}
	
	@Test
	void testFindVenueLinks() {
		assertEquals(new ArrayList<String>(Arrays.asList("http://lsds.hesge.ch/ISPDC2018/venue/", 
				"https://conference.imp.fu-berlin.de/icpe18/registration-Info")), 
				parser.findVenueLinks(new ArrayList<String>(Arrays.asList("http://lsds.hesge.ch/ISPDC2018/venue/",
						"http://lsds.hesge.ch/ISPDC2018/people/",
						"https://conference.imp.fu-berlin.de/icpe18/registration-Info",
						"https://icpe2018.spec.org/workshops.html"))));
	}

//	//TODO test the map
////	@Test
////	void testGetDeadlines() {
////		LinkedHashMap<String, LinkedHashMap<String, String>> deadlines = new LinkedHashMap<>();
////		LinkedHashMap<String, LinkedHashMap<String, String>> empty = new LinkedHashMap<>();
////		assertEquals(deadlines, parser.getDeadlines(links1));
////		assertEquals(empty, parser.getDeadlines(links2));
////		assertEquals(empty, parser.getDeadlines(links3));
////	}

	@Test
	void testGetConferenceYear() {
		assertEquals("2018", parser.getConferenceYear("19 April 2018", ""));
		assertEquals("2019", parser.getConferenceYear("27-29 June 2019", ""));
		assertEquals("2018", parser.getConferenceYear("11-13 February 2018", ""));
		assertEquals("", parser.getConferenceYear("11 February", ""));
	}

	@Test
	void testGetAntiquity() {
		assertEquals("Ninth", parser.getAntiquity("", "Ninth", new ArrayList<String>(Arrays.asList("https://icpe2018.spec.org/home.html"))));
		assertEquals("Seventeenth", parser.getAntiquity("", "17th", new ArrayList<String>(Arrays.asList("https://unescoprivacychair.urv.cat/psd2018/index.php"))));
		assertEquals("", parser.getAntiquity("", "", new ArrayList<String>(Arrays.asList("http://lsds.hesge.ch/ISPDC2018/"))));
		assertEquals("Twentieth", parser.getAntiquity("", "20th", new ArrayList<String>(Arrays.asList("http://lsds.hesge.ch/ISPDC2018/"))));
		assertEquals("Forty-Fourth", parser.getAntiquity("", "44th", new ArrayList<String>(Arrays.asList("http://lsds.hesge.ch/ISPDC2018/"))));
	}

	@Test
	void testGetConferenceDays() {
		assertEquals("", parser.getConferenceDays("", "", new ArrayList<String>(Arrays.asList("https://icpe2018.spec.org/home.html"))));
		assertEquals("25-27 June 2018", parser.getConferenceDays("La La La catch this date: 25-27 June 2018, if you can", "", new ArrayList<String>(Arrays.asList("https://unescoprivacychair.urv.cat/psd2018/index.php"))));
		assertEquals("", parser.getConferenceDays("25                                  - 27 June 2018", "", new ArrayList<String>(Arrays.asList("http://lsds.hesge.ch/ISPDC2018/"))));
	}
	
//	@Test
//	void testgetOrganisers() {
//		Country country = new Country();
////		parser.getOrganisers(new ArrayList<String>(Arrays.asList("https://www.isf.cs.tu-bs.de/cms/events/sefm2018/committees/")), country);
//		
//		LinkedHashMap<String, List<String>> committees = parser.getOrganisers(links2, country);
//		assertTrue(committees.containsKey("General Chair"));
//		assertTrue(committees.containsKey("Local Chair"));
//		assertTrue(committees.containsKey("Steering Committee"));
//		assertTrue(committees.containsKey("Program Committee"));
//		
//		List<String> members = committees.get("General Chair");
//		assertEquals("Nabil Abdennadher, University of Applied Sciences and Arts, Western Switzerland", members.get(0));
//		members = committees.get("Steering Committee");
//		assertEquals("John Morrison, University College Cork, Ireland", members.get(5));
//		assertEquals("Dana Petcu, Western Univ. of Timisoara and e-Austria, Timisoara, Romania", members.get(7));
//		assertEquals("Marek Tudruj, Polish Acad. of Sciences and PJIIT, Warsaw, Poland", members.get(9));
//		members = committees.get("Program Committee");
//		assertEquals(" Aniello Castiglione, Department of Computer Science, University of Salerno, Italy", members.get(5));
//		assertEquals(" Valentin Cristea, University Politehnica of Bucharest, Romania", members.get(11));
//	}
//
//	@Disabled("This method is temporary only")
//	@Test
//	void testTempMethod() {
//
//	}
//
//	@Disabled("This method simply overwrites the array in the class")
//	@Test
//	void testAddSearchWords() {
//
//	}
//
//	@Disabled("This method simply overwrites the array in the class")
//	@Test
//	void testAddNewSearchWords() {
//
//	}
//	
//	@Disabled("This method simply overwrites the array in the class")
//	@Test
//	void testAddCommitteeSearchWords() {
//		
//	}
//
//	@Test
//	void testToOrdinal() {
//		assertEquals("First", parser.toOrdinal(1));
//		assertEquals("Second", parser.toOrdinal(2));
//		assertEquals("Third", parser.toOrdinal(3));
//		assertEquals("Twenty-First", parser.toOrdinal(21));
//		assertEquals("Twelfth", parser.toOrdinal(12));
//	}
//
//	@Test
//	void testSearchCountries() {
//		Country country = new Country();
//		assertEquals("Italy", parser.searchCountries("Mary was born in Italy.", country));
//		assertEquals("Germany", parser.searchCountries("The annual conference takes place in Germany this year.", country));
//		assertEquals("Spain", parser.searchCountries("As per usual, John decided to go to Spain for his holidays.", country));
//	}
//
//	@Test
//	void testChangeToRegex() {
//		assertEquals(".*keyword.*", parser.changeToRegex("keyword"));
//		assertEquals(".*apple.*", parser.changeToRegex("apple"));
//		assertEquals(".*pear.*", parser.changeToRegex("pear"));
//		assertEquals(".*testing.*", parser.changeToRegex("testing"));
//		assertEquals(".*thisisareallylongwordwithoutspaces.*", parser.changeToRegex("thisisareallylongwordwithoutspaces"));
//	}
//
//	@Disabled("This method uses JSoup library functions only")
//	@Test
//	void testGetURLDoc() {
//	}
//
//	
//	@Test
//	void testFindPattern() {
//		Pattern pattern = Pattern.compile("\\d{4}");
//		assertEquals("2018", parser.findPattern("submission deadline: 2018", pattern));
//		assertEquals("2020", parser.findPattern("camera ready papers: 2020", pattern));
//		assertEquals("2019", parser.findPattern("notification deadline: 2019", pattern));
//	}
//
//	@Test
//	void testFindConfDays() {
//		assertEquals("12-14 February, 2018", parser.findConfDays("The conference will take place between 12-14 February, 2018."));
//		assertEquals("February, 12-14, 2018", parser.findConfDays("The conference will take place between February, 12-14, 2018."));
//		assertEquals("February 12-14, 2018", parser.findConfDays("The conference will take place between February 12-14, 2018."));
//	}
}
