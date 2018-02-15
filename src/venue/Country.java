package venue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class Country {
	// Add country exceptions at the start
	private static final ArrayList<String> countries = new ArrayList<>(Arrays.asList("USA", "UK", "Catalonia", "European Union", "Goias", "Korea", "Sri Lanka"));

	public Country() {
		this.storeCountries();
	}
	
	private void storeCountries() {
		Locale[] locales = Locale.getAvailableLocales();
		String name;
		for(Locale locale: locales) {
			name = locale.getDisplayCountry();
			if(!name.equals(""))
				countries.add(name);
		}
	}
	
	public ArrayList<String> getCountries() {
		return countries;
	}
	
}
