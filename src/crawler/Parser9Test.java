package crawler;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.Information;

class Parser9Test {
	private Parser parser;
	
	@BeforeEach
	void setup() {
		parser = new Parser9(new Information(), new Crawler());
	}

	@AfterEach
	void teardown() {
		parser = null;
	}
	
	@Test
	void testGetDeadlines() {
		ArrayList<String> links = new ArrayList<String>(Arrays.asList("https://ieeecompsac.computer.org/2018/important-dates/"));
		LinkedHashMap<String, LinkedHashMap<String, String>> deadlineTypes = parser.getDeadlines(links);
		assertTrue(deadlineTypes.containsKey("0"));
		assertTrue(deadlineTypes.containsKey("1"));
		assertTrue(deadlineTypes.containsKey("2"));
		assertTrue(deadlineTypes.containsKey("3"));
		assertTrue(deadlineTypes.containsKey("4"));
		assertFalse(deadlineTypes.containsKey("5"));

		LinkedHashMap<String, String> deadlines = deadlineTypes.get("1");
		assertTrue(deadlines.containsKey("Main conference symposia author notifications"));
		assertEquals("March 31, 2018", deadlines.get("Main conference symposia author notifications"));
		
		deadlines = deadlineTypes.get("3");
		assertTrue(deadlines.containsKey("Workshop paper, J1C2, Fast Abstract, and SRS author notifications"));
		assertEquals("May 10, 2018", deadlines.get("Workshop paper, J1C2, Fast Abstract, and SRS author notifications"));
		
		deadlines = deadlineTypes.get("4");
		assertTrue(deadlines.containsKey("Camera ready submissions and advance author registration due"));
		assertEquals("May 17, 2018", deadlines.get("Camera ready submissions and advance author registration due"));
		
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("https://research.spec.org/fileadmin/user_upload/documents/icpe_2018/ICPE-2018_Sponsorship.pdf"))));
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("http://www.icsoft.org/Important-dates"))));
	}

}
