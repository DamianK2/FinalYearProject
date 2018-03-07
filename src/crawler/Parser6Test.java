package crawler;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

class Parser6Test {
	private Parser parser = new Parser6(new Information(), new Crawler());
	
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
