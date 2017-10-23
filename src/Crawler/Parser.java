package Crawler;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Parser {
	private static ArrayList<String> linkList = new ArrayList<>();
	
	public Parser(ArrayList<String> links) {
		linkList = links;
	}
	
	// Initial method for getting the title
	public String getTitle() {
		Document doc = null;
        String firstURL = linkList.get(0);
		try {
			doc = Jsoup.connect(firstURL).get();
			System.out.println("Fetching from " + firstURL + "...");
		} catch (IOException e) {
			System.out.println("Something went wrong when getting the first element from the list of links.");
			e.printStackTrace();
		}
		return doc.title();
	}
	
	// Initial method for getting the description
	public String getDescription() {
		Document doc = null;
        String firstURL = linkList.get(0);
		try {
			doc = Jsoup.connect(firstURL).get();
			System.out.println("Fetching from " + firstURL + "...");
		} catch (IOException e) {
			System.out.println("Something went wrong when getting the first element from the list of links.");
			e.printStackTrace();
		}
		boolean check = true;
		String meta = null, parsedMeta;
		try {
			meta = doc.select("meta[name=description]").first().attr("content");
			parsedMeta = meta.replaceAll("(.{100})", "$1\n");
			System.out.println("Decription: " + parsedMeta);
		} catch(NullPointerException e) {
		   System.out.println("No meta with attribute \"name\"");
		   check = false;
		}
		if(!check) {
			check = true;
			try {
				meta = doc.select("meta[property=og:description]").first().attr("content");
				parsedMeta = meta.replaceAll("(.{100})", "$1\n");
				System.out.println("Description: " + parsedMeta);
			} catch(NullPointerException e) {
				System.out.println("No meta with attribute \"property\"");
				check = false;
			}
		}
		if(!check) {
			System.out.println("Well this is embarassing. No description found!");
			meta = "No description!";
		}
		
		return meta;
	}
}
