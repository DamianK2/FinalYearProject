package crawler;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

public class Parser6 extends Parser {

	@Override
	public String getConferenceDays(String title, String description, ArrayList<String> linkList) {		
		Document doc = null;
		String regex = "";
		String link = this.searchLinks("[iI]mportant", linkList);
		if(link.isEmpty())
			return "";
		else {
			doc = this.getURLDoc(link);
			
			for(Element el: doc.getAllElements()) {
				for(TextNode textNode: el.textNodes()) {
					String found = this.findConfDays(textNode.text());
					if(!found.isEmpty())
						return found;
				}
			}
			
			
		}
		
		return "";
	}
}
