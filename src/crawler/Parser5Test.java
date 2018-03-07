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

import database.Information;

class Parser5Test {
	
	private Parser parser = new Parser5(new Information(), new Crawler());

	@Test
	void testGetDescription() {
		File splash = new File("TestPages/SPLASH2018.html");
		File psd = new File("TestPages/PSD2018.html");
		Document doc = null;
		Document doc2 = null;
		try {
			doc = Jsoup.parse(splash, "UTF-8");
			doc2 = Jsoup.parse(psd, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		assertEquals("The ACM SIGPLAN conference on Systems, Programming, Languages"
				+ " and Applications: Software for Humanity (SPLASH) embraces all "
				+ "aspects of software construction and delivery to make it the premier"
				+ " conference at the intersection of programming, languages, and software "
				+ "engineering. SPLASH 2018 will take place in Boston, Massachusetts, USA "
				+ "from Sun 4 - Fri 9 November 2018.", parser.getDescription(doc));
		
		assertEquals("Privacy in statistical databases is about finding tradeoffs"
				+ " to the tension between the increasing societal and economical "
				+ "demand for accurate information and the legal and ethical "
				+ "obligation to protect the privacy of individuals and enterprises"
				+ " which are the respondents providing the statistical data. "
				+ "In the case of statistical databases, the motivation for respondent"
				+ " privacy is one of survival: statistical agencies or survey institutes"
				+ " cannot expect to collect accurate information from individual or corporate"
				+ " respondents unless these feel the privacy of their responses is guaranteed.", parser.getDescription(doc2));
		assertEquals("", parser.getDescription(null));
	}
	
	@Test
	void testGetDeadlines() {
		ArrayList<String> links = new ArrayList<String>(Arrays.asList("https://itrust.sutd.edu.sg/hase2017/important-dates/",
				"http://www.icsoft.org/ImportantDates.aspx"));
		LinkedHashMap<String, LinkedHashMap<String, String>> deadlineTypes = parser.getDeadlines(links);
		assertTrue(deadlineTypes.containsKey("0"));
		assertTrue(deadlineTypes.containsKey("2"));
		assertTrue(deadlineTypes.containsKey("5"));
		assertFalse(deadlineTypes.containsKey("6"));
		assertFalse(deadlineTypes.containsKey("8"));

		LinkedHashMap<String, String> deadlines = deadlineTypes.get("0");
		assertTrue(deadlines.containsKey("Submission deadline"));
		assertEquals("19 September 2016 @ 11:59 PM GMT+8 [Closed]", deadlines.get("Submission deadline"));
		deadlines = deadlineTypes.get("2");
		assertTrue(deadlines.containsKey("Camera-ready submission [Closed]"));
		assertEquals("21 November 2016 @ 11:59 PM GMT+8", deadlines.get("Camera-ready submission [Closed]"));
		deadlines = deadlineTypes.get("5");
		assertTrue(deadlines.containsKey("HASE 2017 Symposium"));
		assertEquals("12 – 14 January 2017", deadlines.get("HASE 2017 Symposium"));
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("https://research.spec.org/fileadmin/user_upload/documents/icpe_2018/ICPE-2018_Sponsorship.pdf"))));
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("http://www.icsoft.org/Important"))));
	}
	
	@Test
	void testGetAntiquity() {
		File icst = new File("TestPages/ICST2018.html");
		File icpe = new File("TestPages/ICPE2018.html");
		File ispass = new File("TestPages/ISPASS2018.html");
		File compsac = new File("TestPages/COMPSAC2018.html");
		Document doc = null;
		Document doc2 = null;
		Document doc3 = null;
		Document doc4 = null;
		try {
			doc = Jsoup.parse(icst, "UTF-8");
			doc2 = Jsoup.parse(icpe, "UTF-8");
			doc3 = Jsoup.parse(ispass, "UTF-8");
			doc4 = Jsoup.parse(compsac, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		assertEquals("Eleventh", parser.getAntiquity("", "Ninth", doc));
		assertEquals("Ninth", parser.getAntiquity("", "", doc2));
		assertEquals("", parser.getAntiquity("", "", doc3));
		assertEquals("Forty-Second", parser.getAntiquity("", "", doc4));
		assertEquals("", parser.getAntiquity("", "", null));
	}

	@Test
	void testGetConferenceDays() {
		File splash = new File("TestPages/SPLASH2018.html");
		File psd = new File("TestPages/PSD2018.html");
		File compsac = new File("TestPages/COMPSAC2018.html");
		Document doc = null;
		Document doc2 = null;
		Document doc3 = null;
		try {
			doc = Jsoup.parse(splash, "UTF-8");
			doc2 = Jsoup.parse(psd, "UTF-8");
			doc3 = Jsoup.parse(compsac, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		} 
		assertEquals("Sun 4 - Fri 9 November 2018", parser.getConferenceDays("", "", doc));
		assertEquals("September 26-28, 2018", parser.getConferenceDays("", "", doc2));
		assertEquals("", parser.getConferenceDays("", "", doc3));
		assertEquals("", parser.getConferenceDays("", "", null));
	}

}
