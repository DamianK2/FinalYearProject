package crawler;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.junit.jupiter.api.Test;

import database.Information;

class Parser7Test {

	private Parser parser = new Parser7(new Information(), new Crawler());
	
	@Test
	void testGetDeadlines() {
		ArrayList<String> links = new ArrayList<String>(Arrays.asList("http://www.es.mdh.se/icst2018/"));
		LinkedHashMap<String, LinkedHashMap<String, String>> deadlineTypes = parser.getDeadlines(links);
		assertTrue(deadlineTypes.containsKey("Conference (CFP, EasyChair):"));
		assertTrue(deadlineTypes.containsKey("PhD Symposium (CFP, EasyChair):"));
		assertTrue(deadlineTypes.containsKey("Workshops (CFWP):"));
		assertTrue(deadlineTypes.containsKey("Tools, demos and artefacts track"));

		LinkedHashMap<String, String> deadlines = deadlineTypes.get("Conference (CFP, EasyChair):");
		assertTrue(deadlines.containsKey("Submission of the Full Research Papers"));
		assertEquals("Oct. 16th 2017", deadlines.get("Submission of the Full Research Papers"));
		assertTrue(deadlines.containsKey("Author notification"));
		assertEquals("Dec. 18th 2017", deadlines.get("Author notification"));
		
		deadlines = deadlineTypes.get("PhD Symposium (CFP, EasyChair):");
		assertTrue(deadlines.containsKey("Submission deadline"));
		assertEquals("Jan. 31st 2018", deadlines.get("Submission deadline"));
		
		deadlines = deadlineTypes.get("Workshops (CFWP):");
		assertTrue(deadlines.containsKey("Proposals submitted"));
		assertEquals("Sep. 8th 2017", deadlines.get("Proposals submitted"));
		assertTrue(deadlines.containsKey("Workshop papers submitted"));
		assertEquals("Jan. 12th 2018", deadlines.get("Workshop papers submitted"));
		
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("https://research.spec.org/fileadmin/user_upload/documents/icpe_2018/ICPE-2018_Sponsorship.pdf"))));
		assertEquals(new LinkedHashMap<String, LinkedHashMap<String, String>>(), parser.getDeadlines(new ArrayList<String>(Arrays.asList("https://ieeecompsac.computer.org/2018/important-dates/"))));
	}
}
