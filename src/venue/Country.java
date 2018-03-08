package venue;

import java.util.ArrayList;
import java.util.Locale;

public class Country {
	// Add country exceptions at the start
	private static final ArrayList<String> countries = new ArrayList<>();

	public Country() {
		this.addExceptions();
		this.storeCountries();
	}
	
	private void storeCountries() {
		Locale[] locales = Locale.getAvailableLocales();
		String name;
		for(Locale locale: locales) {
			name = locale.getDisplayCountry();
			if(!name.isEmpty())
				countries.add(name);
		}
	}
	
	private void addExceptions() {
		countries.add("USA");
		countries.add("UK");
		countries.add("Catalonia");
		countries.add("European Union");
		countries.add("Goias");
		countries.add("Korea");
		countries.add("Sri Lanka");
	}
	
	public synchronized ArrayList<String> getCountries() {
		return countries;
	}
	
}
