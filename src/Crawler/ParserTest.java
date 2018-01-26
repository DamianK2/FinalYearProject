package Crawler;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import venue.Country;

class ParserTest {

	private Parser parser = new Parser();
	private ArrayList<String> titles = new ArrayList<>(Arrays.asList("International Conference on Performance Engineering (ICPE) 2018: ICPE 2018", 
			"ISPDC 2018 � The 17th IEEE International Symposium on Parallel and Distributed Computing, 25-27 June 2018, Geneva, Switzerland", 
			"PSD2018 - Privacy in Statistical Databases - UNESCO Privacy Chair"));
	private ArrayList<String> descriptions = new ArrayList<>(Arrays.asList("Ninth ACM/SPEC International Conference on Performance Engineering, "
			+ "ICPE 2018 - A Joint Meeting of WOSP/SIPEW sponsored by ACM SIGMETRICS and ACM SIGSOFT in Cooperation with SPEC.", 
			"The 17th IEEE International Symposium on Parallel and Distributed Computing, 25-27 June 2018, Geneva, Switzerland"));
	private ArrayList<String> links1 = new ArrayList<>(Arrays.asList("https://icpe2018.spec.org/conference-program.html", "https://icpe2018.spec.org/venue.html",
			"https://icpe2018.spec.org/submissions.html", "https://icpe2018.spec.org/important-dates.html"));
	private ArrayList<String> links2 = new ArrayList<>(Arrays.asList("http://lsds.hesge.ch/ISPDC2018/", "http://lsds.hesge.ch/ISPDC2018/call-for-paper/",
			"http://lsds.hesge.ch/ISPDC2018/people/", "http://lsds.hesge.ch/ISPDC2018/venue/"));
	private ArrayList<String> links3 = new ArrayList<>(Arrays.asList("https://unescoprivacychair.urv.cat/psd2018/index.php?m=organization",
			"https://unescoprivacychair.urv.cat/psd2018/index.php?m=topics", "https://unescoprivacychair.urv.cat/psd2018/index.php?m=proceedings",
			"https://unescoprivacychair.urv.cat/psd2018/index.php?m=venue"));
	
	
	@Test
	void testGetTitle() {
		assertEquals(titles.get(0), parser.getTitle("https://icpe2018.spec.org/home.html"));
		assertEquals(titles.get(1), parser.getTitle("http://lsds.hesge.ch/ISPDC2018/"));
		assertEquals(titles.get(2), parser.getTitle("https://unescoprivacychair.urv.cat/psd2018/index.php"));
	}

	@Test
	void testGetSponsors() {
		assertEquals("", parser.getSponsors("John Doe likes kitesufring.", "Kitesurfing is a style of kiteboarding specific to wave riding, which uses standard surfboards or boards shaped specifically for the purpose."));
		assertEquals("", parser.getSponsors(titles.get(0), descriptions.get(0)));
		assertEquals("IEEE", parser.getSponsors(titles.get(1), descriptions.get(1)));
		assertEquals("UNESCO", parser.getSponsors(titles.get(2), "This class doesn't use the description"));
	}

	@Test
	void testGetProceedings() {
		ArrayList<String> links4 = new ArrayList<>(Arrays.asList("https://unescoprivacychair.urv.cat/psd2018/index.php?m=organization", "https://unescoprivacychair.urv.cat/psd2018/index.php?m=topics"));
		
		assertEquals("ACM", parser.getProceedings(links1));
		assertEquals("IEEE", parser.getProceedings(links2));
		assertEquals("Springer", parser.getProceedings(links3));
		assertEquals("", parser.getProceedings(links4));
	}

	@Test
	void testGetDescription() {
		assertEquals(descriptions.get(0), parser.getDescription("https://icpe2018.spec.org/home.html"));
		assertEquals("", parser.getDescription("http://lsds.hesge.ch/ISPDC2018/"));
		assertEquals("", parser.getDescription("https://unescoprivacychair.urv.cat/psd2018/index.php"));
	}

	@Test
	void testGetVenue() {
		Country country = new Country();
		assertEquals("Germany", parser.getVenue(titles.get(0), descriptions.get(0), country, links1));
		assertEquals("Switzerland", parser.getVenue(titles.get(1), descriptions.get(1), country, links2));
		assertEquals("", parser.getVenue(titles.get(2), "", country, links3));
	}

	@Test
	void testGetDeadlines() {
		ArrayList<String> deadlines = new ArrayList<>(Arrays.asList("Oct 25, 2017", "Dec 08, 2017", "Feb 14, 2018"));
		ArrayList<String> empty = new ArrayList<>();
		assertEquals(deadlines, parser.getDeadlines(links1));
		assertEquals(empty, parser.getDeadlines(links2));
		assertEquals(empty, parser.getDeadlines(links3));
	}

	@Test
	void testGetAdditionalDeadlineInfo() {
		ArrayList<String> deadlines = new ArrayList<>(Arrays.asList("Yes", "Jan 10, 2018", "Feb 08, 2018", "Feb 19, 2018", "No", "", "", "",
				"Yes", "Oct 13, 2017", "Oct 31, 2017", ""));
		ArrayList<String> empty = new ArrayList<>();

		assertEquals(deadlines, parser.getAdditionalDeadlineInfo(links1));
		assertEquals(empty, parser.getAdditionalDeadlineInfo(links2));
		assertEquals(empty, parser.getAdditionalDeadlineInfo(links3));
	}

	@Test
	void testGetConferenceYear() {
		assertEquals("2018", parser.getConferenceYear(titles.get(0)));
		assertEquals("2018", parser.getConferenceYear(titles.get(1)));
		assertEquals("2018", parser.getConferenceYear(titles.get(2)));
	}

	@Test
	void testGetAntiquity() {
		assertEquals("Ninth", parser.getAntiquity(descriptions.get(0), "https://icpe2018.spec.org/home.html"));
		assertEquals("Seventeenth", parser.getAntiquity(descriptions.get(1), "https://unescoprivacychair.urv.cat/psd2018/index.php"));
		assertEquals("", parser.getAntiquity("", "http://lsds.hesge.ch/ISPDC2018/"));
	}

	@Test
	void testGetConferenceDays() {
		assertEquals("", parser.getConferenceDays(titles.get(0), descriptions.get(0), "https://icpe2018.spec.org/home.html"));
		assertEquals("25-27 June 2018", parser.getConferenceDays(titles.get(1), descriptions.get(1), "https://unescoprivacychair.urv.cat/psd2018/index.php"));
		assertEquals("", parser.getConferenceDays(titles.get(2), "", "http://lsds.hesge.ch/ISPDC2018/"));
	}

	@Disabled("This method is temporary only")
	@Test
	void testTempMethod() {
		fail("Not yet implemented");
	}

	@Disabled("This method simply overwrites the array in the class")
	@Test
	void testAddSearchWords() {
		fail("Not yet implemented");
	}

	@Disabled("This method simply overwrites the array in the class")
	@Test
	void testAddNewSearchWords() {
		fail("Not yet implemented");
	}

	@Test
	void testToOrdinal() {
		assertEquals("First", parser.toOrdinal(1));
		assertEquals("Second", parser.toOrdinal(2));
		assertEquals("Third", parser.toOrdinal(3));
		assertEquals("Twenty-First", parser.toOrdinal(21));
		assertEquals("Twelfth", parser.toOrdinal(12));
	}

	@Test
	void testSearchCountries() {
		Country country = new Country();
		assertEquals("Italy", parser.searchCountries("Mary was born in Italy.", country));
		assertEquals("Germany", parser.searchCountries("The annual conference takes place in Germany this year.", country));
		assertEquals("Spain", parser.searchCountries("As per usual, John decided to go to Spain for his holidays.", country));
	}

	@Test
	void testChangeToRegex() {
		assertEquals(".*keyword.*", parser.changeToRegex("keyword"));
		assertEquals(".*apple.*", parser.changeToRegex("apple"));
		assertEquals(".*pear.*", parser.changeToRegex("pear"));
		assertEquals(".*testing.*", parser.changeToRegex("testing"));
		assertEquals(".*thisisareallylongwordwithoutspaces.*", parser.changeToRegex("thisisareallylongwordwithoutspaces"));
	}

	@Disabled("This method uses JSoup library functions only")
	@Test
	void testGetURLDoc() {
		fail("Not yet implemented");
	}

	
	@Test
	void testFindDeadline() {
		String[] test = {"submission deadline: 2019", "camera ready papers: 2018", "notification deadline: 2020"};
		Pattern pattern = Pattern.compile("\\d{4}");
		assertEquals("2018", parser.findDeadline(test, ".*camera.*", pattern));
		assertEquals("2020", parser.findDeadline(test, ".*notification.*", pattern));
		assertEquals("2019", parser.findDeadline(test, ".*submission.*", pattern));
	}

	@Test
	void testFindConfDays() {
		assertEquals("12-14 February, 2018", parser.findConfDays("The conference will take place between 12-14 February, 2018."));
		assertEquals("February, 12-14, 2018", parser.findConfDays("The conference will take place between February, 12-14, 2018."));
		assertEquals("February 12-14, 2018", parser.findConfDays("The conference will take place between February 12-14, 2018."));
	}

}