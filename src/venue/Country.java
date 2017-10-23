package venue;

import java.util.ArrayList;
import java.util.Locale;

public class Country {
	
	private static final ArrayList<String> countries = new ArrayList<>();

	public ArrayList<String> getCountries() {
		Locale[] locales = Locale.getAvailableLocales();
		String name;
		for(Locale locale: locales) {
			name = locale.getDisplayCountry();
			if(!name.equals(""))
				countries.add(name);
		}
		return countries;
	}
}
