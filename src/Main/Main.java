package Main;

import java.util.ArrayList;

import Crawler.Crawler;

public class Main {

	public static final String[] URLS = {"https://icpe2017.spec.org/", "http://ispdc2017.dps.uibk.ac.at/" ,"https://unescoprivacychair.urv.cat/psd2016/"};
	
	public static void main(String[] args) {
		long tStart = System.currentTimeMillis();
		ArrayList<String> links = new ArrayList<String>();
		String url = URLS[0];
		String url1 = URLS[1];
		String url2 = URLS[2];
		

        Crawler test = new Crawler(url2);
        test.testing();
//      links = test.getAllLinks();
//		 
//		print("\nLinks: (%d)", links.size());
//		int i = 1;
//		for (String link : links) {
//			print("%d. * a: <%s>", i, link);
//			i++;
//		}
		
//		for (String link : links) {
//			if(link.matches(".*venue.*"))
//				print("%d. * a: <%s>", i, link);
//			i++;
//		}
		
//		long tEnd = System.currentTimeMillis();
//		long tDelta = tEnd - tStart;
//		double elapsedSeconds = tDelta / 1000.0;
//		print("Time taken to fetch links: %f seconds", elapsedSeconds);
		

}
	
    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
}
