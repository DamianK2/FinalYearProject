package Main;

import java.util.ArrayList;

import Crawler.Crawler;

public class Main {

	public static void main(String[] args) {
		long tStart = System.currentTimeMillis();
		ArrayList<String> links = new ArrayList<String>();
		String url = "https://icpe2017.spec.org/icpe2017.html";
		
        Crawler test = new Crawler(url);
        links = test.getAllLinks();
        
		print("\nLinks: (%d)", links.size());
		int i = 1;
		for (String link : links) {
			print("%d. * a: <%s>", i, link);
			i++;
		}
		
		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		double elapsedSeconds = tDelta / 1000.0;
		print("Time taken to fetch links: %f seconds", elapsedSeconds);
	}
	
    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
}
