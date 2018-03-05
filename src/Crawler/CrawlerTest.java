package crawler;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

class CrawlerTest {
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	void testGetAllLinks() {
		Crawler crawler = new Crawler("https://unescoprivacychair.urv.cat/psd2018/index.php");
		ArrayList<String> links = crawler.getAllLinks();
		
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php", links.get(0));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php?m=organization", links.get(1));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php?m=topics", links.get(2));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php?m=submissions", links.get(3));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php?m=proceedings", links.get(4));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php#", links.get(5));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php?m=venue", links.get(6));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php?m=soon", links.get(7));
		assertEquals("https://unescoprivacychair.urv.cat/psd2018/index.php?m=grants", links.get(8));
	}
	
	@Test
	void testFramesetPages() {
		Crawler crawler = new Crawler("http://www.ispass.org/ispass2018/");
		ArrayList<String> links = crawler.getAllLinks();

		assertTrue(links.contains("http://www.ispass.org/ispass2018/sidebar.php"));
		assertTrue(links.contains("http://www.ispass.org/ispass2018/workshopstutorials.php"));
		assertTrue(links.contains("http://www.ispass.org/ispass2018/title_logo.php"));
		assertTrue(links.contains("http://www.ispass.org/ispass2018/previous.php"));
	}
	
	@Test
	void testNullPointerException() {
		Crawler crawler = new Crawler("https://www.testingtestingtesting.com");
		assertThrows(NullPointerException.class, () -> {
			crawler.getAllLinks();
        });
	}

}
