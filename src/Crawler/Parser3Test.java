package crawler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import venue.Country;

class Parser3Test {

	private Parser parser = new Parser3();
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
	void testGetDescription() {
		assertEquals("", parser.getDescription("https://icpe2018.spec.org/home.html"));
		assertEquals("The 17th IEEE International Symposium on Parallel and Distributed Computing, 25-27 June 2018, Geneva, Switzerland", parser.getDescription("http://lsds.hesge.ch/ISPDC2018/"));
		assertEquals("", parser.getDescription("https://unescoprivacychair.urv.cat/psd2018/index.php"));
	}

	@Test
	void testGetVenue() {
		Country country = new Country();
		assertEquals("", parser.getVenue(titles.get(0), descriptions.get(0), country, links1));
		assertEquals("Switzerland", parser.getVenue(titles.get(1), descriptions.get(1), country, links2));
		assertEquals("", parser.getVenue(titles.get(2), "", country, links3));
	}

	@Test
	void testGetDeadlines() {
		ArrayList<String> deadlines = new ArrayList<>(Arrays.asList("MAY 27, 2018", "June 29, 2018", ""));
		ArrayList<String> empty = new ArrayList<>();
		
		assertEquals(empty, parser.getDeadlines(links1));
		assertEquals(empty, parser.getDeadlines(links2));
		assertEquals(deadlines, parser.getDeadlines(links3));
	}

	@Test
	void testGetAdditionalDeadlineInfo() {
		ArrayList<String> empty = new ArrayList<>();
		ArrayList<String> deadlines2 = new ArrayList<>(Arrays.asList("No", "", "", "", "No", "", "", "", "No", "", "", ""));
		assertEquals(empty, parser.getAdditionalDeadlineInfo(links1));
		assertEquals(empty, parser.getAdditionalDeadlineInfo(links2));
		assertEquals(deadlines2, parser.getAdditionalDeadlineInfo(links3));
	}

	@Test
	void testGetConferenceDays() {
		assertEquals("", parser.getConferenceDays(titles.get(0), descriptions.get(0), "https://icpe2018.spec.org/home.html"));
		assertEquals("", parser.getConferenceDays(titles.get(2), "", "http://lsds.hesge.ch/ISPDC2018/"));
		assertEquals("September 26-28, 2018", parser.getConferenceDays(titles.get(1), descriptions.get(1), "https://unescoprivacychair.urv.cat/psd2018/index.php"));
	}

}
