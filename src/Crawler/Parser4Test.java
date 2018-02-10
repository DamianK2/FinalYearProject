package crawler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import venue.Country;

class Parser4Test {
	
	private Parser parser = new Parser4();
	private ArrayList<String> titles = new ArrayList<>(Arrays.asList("International Conference on Performance Engineering (ICPE) 2018: ICPE 2018", 
			"ISPDC 2018 – The 17th IEEE International Symposium on Parallel and Distributed Computing, 25-27 June 2018, Geneva, Switzerland", 
			"PSD2018 - Privacy in Statistical Databases - UNESCO Privacy Chair"));
	private ArrayList<String> descriptions = new ArrayList<>(Arrays.asList("Ninth ACM/SPEC International Conference on Performance Engineering, "
			+ "ICPE 2018 - A Joint Meeting of WOSP/SIPEW sponsored by ACM SIGMETRICS and ACM SIGSOFT in Cooperation with SPEC.", 
			"The 17th IEEE International Symposium on Parallel and Distributed Computing, 25-27 June 2018, Geneva, Switzerland"));
	private ArrayList<String> links1 = new ArrayList<>(Arrays.asList("https://icpe2018.spec.org/home.html", "https://icpe2018.spec.org/conference-program.html", 
			"https://icpe2018.spec.org/venue.html", "https://icpe2018.spec.org/submissions.html", "https://icpe2018.spec.org/important-dates.html"));
	private ArrayList<String> links2 = new ArrayList<>(Arrays.asList("http://lsds.hesge.ch/ISPDC2018/", "http://lsds.hesge.ch/ISPDC2018/call-for-paper/",
			"http://lsds.hesge.ch/ISPDC2018/people/", "http://lsds.hesge.ch/ISPDC2018/venue/"));
	private ArrayList<String> links3 = new ArrayList<>(Arrays.asList("https://unescoprivacychair.urv.cat/psd2018/index.php?m=organization",
			"https://unescoprivacychair.urv.cat/psd2018/index.php?m=topics", "https://unescoprivacychair.urv.cat/psd2018/index.php?m=proceedings",
			"https://unescoprivacychair.urv.cat/psd2018/index.php?m=venue", "https://unescoprivacychair.urv.cat/psd2018/index.php"));

	@Test
	void testGetVenue() {
		Country country = new Country();
		assertEquals("", parser.getVenue(titles.get(0), descriptions.get(0), country, links1));
		assertEquals("", parser.getVenue(titles.get(1), descriptions.get(1), country, links2));
		assertEquals("Spain", parser.getVenue(titles.get(2), "", country, links3));
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
		assertEquals("Eighteenth", parser.getAntiquity("", "", new ArrayList<String>(Arrays.asList("https://itrust.sutd.edu.sg/hase2017/hase-history/"))));
	}

	@Test
	void testGetConferenceDays() {
		assertEquals("April 9-13, 2018", parser.getConferenceDays(titles.get(0), descriptions.get(0), new ArrayList<String>(Arrays.asList("https://icpe2018.spec.org/home.html"))));
		assertEquals("", parser.getConferenceDays(titles.get(2), "", new ArrayList<String>(Arrays.asList("http://lsds.hesge.ch/ISPDC2018/"))));
		assertEquals("", parser.getConferenceDays(titles.get(1), descriptions.get(1), new ArrayList<String>(Arrays.asList("https://unescoprivacychair.urv.cat/psd2018/index.php"))));
	}

}
