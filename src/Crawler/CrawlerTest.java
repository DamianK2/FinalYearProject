package crawler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CrawlerTest {

	@Disabled("Constructor receives links and stores them in the class")
	@Test
	void testCrawler() {
		
	}

	@Test
	void testGetAllLinks() {
		Crawler crawler = new Crawler("https://unescoprivacychair.urv.cat/psd2018/index.php");
		ArrayList<String> links = crawler.getAllLinks();
		
		System.out.println(links);
		
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

}
