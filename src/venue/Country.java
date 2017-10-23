package venue;

import java.util.ArrayList;
import java.util.Locale;

public class Country {
	
	private static final ArrayList<String> countries = new ArrayList<>();

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
