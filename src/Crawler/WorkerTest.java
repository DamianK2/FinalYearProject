//package crawler;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//
//class WorkerTest {
//
//	private static ArrayList<String> linkList = new ArrayList<>();
//	private static ArrayList<Thread> threads = new ArrayList<>();
//	
//	@Disabled("Constructor receives links and stores them in the class")
//	@Test
//	void testWorker() {
//		
//	}
//
//	@Test
//	void testRun() {
//		Document doc = null;
//		try {
//			doc = Jsoup.connect("https://icpe2018.spec.org/home.html").get();
//		} catch (IOException e) {
//			System.out.println("Something went wrong when getting the first element from the list of links.");
//			e.printStackTrace();
//		}
//        Elements links = doc.select("ul a[href]");
//        
//        // Gets the link with definite sublinks
//        System.out.println(links.get(7));
//        
//        Thread thread = new Thread(new Worker(links.get(7), linkList));
//        threads.add(thread);
//    	thread.start();
//    	
//    	try {
//			threads.get(0).join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//    	   	
//    	assertEquals("https://icpe2018.spec.org/submissions/research-papers-track.html", linkList.get(0));
//    	assertEquals("https://icpe2018.spec.org/submissions/industry-and-experience-track.html", linkList.get(1));
//    	assertEquals("https://icpe2018.spec.org/submissions/artifact-evaluation-track.html", linkList.get(2));
//    	assertEquals("https://icpe2018.spec.org/submissions/posters-and-demonstrations-track.html", linkList.get(3));
//    	assertEquals("https://icpe2018.spec.org/submissions/tutorials-track.html", linkList.get(4));
//    	assertEquals("https://icpe2018.spec.org/submissions/workshops.html", linkList.get(5));
//    	assertEquals("https://icpe2018.spec.org/submissions/work-in-progress-and-vision-track.html", linkList.get(6));
//    	
//    	
//	}
//
//}
