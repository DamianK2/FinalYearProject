package crawler;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.junit.jupiter.api.Test;

class Parser8Test {
	private Parser parser = new Parser8(new Information(), new Crawler());
	
	@Test
	void testGetDeadlines() {
		ArrayList<String> links = new ArrayList<String>(Arrays.asList("http://www.icsoft.org/ImportantDates.aspx"));
		LinkedHashMap<String, LinkedHashMap<String, String>> deadlineTypes = parser.getDeadlines(links);
		assertTrue(deadlineTypes.containsKey("Workshops"));
		assertTrue(deadlineTypes.containsKey("Tutorials"));
		assertTrue(deadlineTypes.containsKey("Panels"));
		assertTrue(deadlineTypes.containsKey("European Project Space"));

		LinkedHashMap<String, String> deadlines = deadlineTypes.get("Workshops");
		assertTrue(deadlines.containsKey("Workshop Proposal:"));
		assertEquals("March 28, 2018", deadlines.get("Workshop Proposal:"));
		
		deadlines = deadlineTypes.get("Tutorials");
		assertTrue(deadlines.containsKey("Tutorial Proposal:"));
		assertEquals("June 22, 2018", deadlines.get("Tutorial Proposal:"));
		
		deadlines = deadlineTypes.get("European Project Space");
		assertTrue(deadlines.containsKey("Authors Notification:"));
		assertEquals("June 27, 2018", deadlines.get("Authors Notification:"));
		assertTrue(deadlines.containsKey("Registration:"));
		assertEquals("July 9, 2018", deadlines.get("Registration:"));
		
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("https://research.spec.org/fileadmin/user_upload/documents/icpe_2018/ICPE-2018_Sponsorship.pdf"))));
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("http://www.icsoft.org/Important-dates"))));
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("https://ieeecompsac.computer.org/2018/important-dates/"))));
	}

}
