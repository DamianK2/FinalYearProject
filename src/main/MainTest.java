package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainTest {
	static Logger log = LogManager.getLogger(MainTest.class);
	
	public static void main(String[] args) {
//		log.info("Wojtek went cycling");
//		log.error("Wojtek fell over");
		log.info("Marcin went cycling");
		log.error("Marcin fell over");
		testLog();
	}
	
	private static void testLog() {
//		log.fatal("Wojtek is in the hospital");
		log.fatal("Marcin is in the hospital");
	}

}
