package crawler;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

class CrawlerTest {
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	void testGetAllLinks() {
		Crawler crawler = new Crawler();
		File psd = new File("TestPages/PSD2018.html");
		File splash = new File("TestPages/SPLASH2018.html");
		File icpe = new File("TestPages/CustomTestPage2018.html");
//		File splash = new File("TestPages/SPLASH2018.html");
//		File splash = new File("TestPages/SPLASH2018.html");
//		File splash = new File("TestPages/SPLASH2018.html");
//		File splash = new File("TestPages/SPLASH2018.html");
//		File splash = new File("TestPages/SPLASH2018.html");
		Document doc = null;
		Document doc2 = null;
		Document doc3 = null;
		try {
			doc = Jsoup.parse(psd, "UTF-8");
			doc2 = Jsoup.parse(splash, "UTF-8");
			doc3 = Jsoup.parse(icpe, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		}
		
		ArrayList<String> links = crawler.getAllLinks(doc, new ArrayList<String>(Arrays.asList("https://unescoprivacychair.urv.cat/psd2018/index.php")));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php", links.get(0));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php?m=organization", links.get(1));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php?m=topics", links.get(2));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php?m=submissions", links.get(3));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php?m=proceedings", links.get(4));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php#", links.get(5));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php?m=venue", links.get(6));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php?m=soon", links.get(7));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php?m=grants", links.get(8));
		links = crawler.getAllLinks(doc2, new ArrayList<String>(Arrays.asList("https://2018.splashcon.org/home")));
		links = crawler.getAllLinks(doc3, new ArrayList<String>(Arrays.asList("https://icpe2018.spec.org/")));
	}
	
	@Test
	void testFramesetPages() {
		Crawler crawler = new Crawler();
		File ispass = new File("TestPages/ISPASS2018.html");
		Document doc = null;
		try {
			doc = Jsoup.parse(ispass, "UTF-8");
			throw new IOException();
		} catch (IOException e) {
		}
		ArrayList<String> links = crawler.getAllLinks(doc, new ArrayList<String>(Arrays.asList("http://www.ispass.org/ispass2018/")));

		assertTrue(links.contains("http://www.ispass.org/ispass2018/ISPASS2018_files/main.html"));
		assertTrue(links.contains("http://www.ispass.org/ispass2018/ISPASS2018_files/title_logo.html"));
		assertTrue(links.contains("http://www.ispass.org/ispass2018/ISPASS2018_files/sidebar_logo.html"));
		assertTrue(links.contains("http://www.ispass.org/ispass2018/ISPASS2018_files/sidebar.html"));
	}
	
//	@Test
//	void testNullPointerException() {
//		Crawler crawler = new Crawler();
//		assertThrows(NullPointerException.class, () -> {
//			crawler.getURLDoc("https://www.testingtestingtesting.com");
//        });
//	}
	
	@Test
	void testConnection() {
		Crawler crawler = new Crawler();
		crawler.getURLDoc("https://www.google.com");
	}

}
