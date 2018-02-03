package crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

public class Parser5 extends Parser {

	public String getConferenceDays(String title, String description, String homeLink) {
		
		Document doc = this.getURLDoc(homeLink);
		
		for(Element el: doc.getAllElements()) {
			for(TextNode textNode: el.textNodes()) {
				String found = this.findConfDays(textNode.text());
				if(!found.equals("")) {
					return found;
				}
			}
		}
		
		return "";
	}
}
