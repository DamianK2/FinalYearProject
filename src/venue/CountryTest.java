package venue;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CountryTest {

	@Test
	void testCountry() {
		Country country = new Country();
		ArrayList<String> countries = country.getCountries();
		assertTrue(countries.contains("USA"));
		assertTrue(countries.contains("Catalonia"));
		assertTrue(countries.contains("Goias"));
		assertTrue(countries.contains("Sri Lanka"));
	}

	@Disabled("The method is used to retrieve the information that was passed on from the Locale library")
	@Test
	void testGetCountries() {
	}

}
