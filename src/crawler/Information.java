package crawler;

import java.util.ArrayList;

public class Information {
	private ArrayList<String> sponsors;
	private ArrayList<String> proceedings;
	private ArrayList<String> committees;
	
	public Information() {
		this.sponsors = new ArrayList<String>();
		this.proceedings = new ArrayList<String>();
		this.committees = new ArrayList<String>();
		this.addKnownSponsors();
		this.addKnownProceedings();
		this.addPotentialCommitteeNames();
	}
	
	/**
	 * Adds the know sponsors
	 */
	private void addKnownSponsors() {
		this.sponsors.add("ACM");
		this.sponsors.add("SPEC");
		this.sponsors.add("UNESCO");
		this.sponsors.add("IEEE");
		this.sponsors.add("AFIS");
		this.sponsors.add("INCOSE");
	}
	
	/**
	 * Adds the known proceedings
	 */
	private void addKnownProceedings() {
		this.proceedings.add("ACM");
		this.proceedings.add("SPEC");
		this.proceedings.add("Springer");
		this.proceedings.add("IEEE");
		this.proceedings.add("IFIP");
	}
	
	/**
	 * Adds the known names that appear in the committee headings
	 */
	private void addPotentialCommitteeNames() {
		this.committees.add("committee");
		this.committees.add("chair");
		this.committees.add("paper");
		this.committees.add("member");
	}
	
	/** 
	 * Add a sponsor to the existing list of sponsors
	 * @param sponsor
	 */
	public void addSponsor(String sponsor) {
		this.sponsors.add(sponsor);
	}
	
	/**
	 * Add a proceeding to the existing list of proceedings
	 * @param proceeding
	 */
	public void addProceeding(String proceeding) {
		this.proceedings.add(proceeding);
	}
	
	/**
	 * Add a committee name to the exisitng list of committee names
	 * @param name
	 */
	public void addCommitteeName(String name) {
		this.committees.add(name);
	}
	
	/**
	 * @return list of sponsors
	 */
	public ArrayList<String> getSponsors() {
		return this.sponsors;
	}
	
	/**
	 * @return list of proceedings
	 */
	public ArrayList<String> getProceedings() {
		return this.proceedings;
	}

	/**
	 * @return list of committee names
	 */
	public ArrayList<String> getCommitteeNames() {
		return this.committees;
	}
}
