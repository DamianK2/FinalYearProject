package database;

import java.util.ArrayList;
import java.util.Scanner;

public class Information {
	private static ArrayList<String> sponsors;
	private static ArrayList<String> proceedings;
	private static ArrayList<String> committees;
	
	public Information() {
		sponsors = new ArrayList<String>();
		proceedings = new ArrayList<String>();
		committees = new ArrayList<String>();
		addKnownSponsors();
		addKnownProceedings();
		addPotentialCommitteeNames();
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String userInput;
		
		if(args[0].equals("-h")) {
			System.out.println("To see the list of currently used sponsors, proceedings and committees, type in -ls");
			System.out.println("To add the lists of currently known sponsors, proceedings and committees, type in -ak");
			System.out.println("To remove everything from the lists, type in -ra");
			System.out.println("To remove everything from one of the lists, type in -ra followed by -l, -s, -p or -c");
			System.out.println("To add/remove one item, type in 3 arguments e.g. -a -s \"ACM\"");
			System.out.println("First argument:");
			System.out.println("\tTo add use -a");
			System.out.println("\tTo remove use -r");
			System.out.println("Second argument:");
			System.out.println("\tTo select links use -l");
			System.out.println("\tTo select sponsors use -s");
			System.out.println("\tTo select proceedings use -p");
			System.out.println("\tTo select committee names use -c");
			System.out.println("Third argument:");
			System.out.println("\tType in the string to be added/removed");
		} else if(args[0].equals("-ls")) {
			if(!sponsors.isEmpty())
				System.out.println(sponsors.toString());
			else
				System.out.println("No sponsors in the list.");
			
		} else if(args[0].equals("-ak")) {
			System.out.println("Are you sure you want to add all the known sponsors, proceedings and committee names to the lists? (y/n)");
			
			boolean correctInput = false;
			while(!correctInput) {
				userInput = scanner.nextLine();
				if(userInput.equalsIgnoreCase("y")) {
					addKnownSponsors();
					addKnownProceedings();
					addPotentialCommitteeNames();
					correctInput = true;
				} else if(userInput.equalsIgnoreCase("n")) {
					System.exit(0);
				} else {
					System.out.println("Wrong input. Please type in y/n.");
				}
			}
		} else if(args[0].equals("-ra")) {
			try {
				String secondArg = args[1];
				if(secondArg.equals("-l")) {
					//TODO links
				} else if(secondArg.equals("-s")) {
					sponsors.clear();
				} else if(secondArg.equals("-p")) {
					proceedings.clear();
				} else if(secondArg.equals("-c")) {
					committees.clear();
				} else {
					System.out.println("Wrong second argument. Expected -l, -s, -p or -c.");
				}
			} catch(ArrayIndexOutOfBoundsException e) {
			}
			System.out.println("Are you sure you want to remove everything from the lists? (y/n)");
			
			boolean correctInput = false;
			while(!correctInput) {
				userInput = scanner.nextLine();
				if(userInput.equalsIgnoreCase("y")) {
					// TODO clear links
					sponsors.clear();
					proceedings.clear();
					committees.clear();
					correctInput = true;
				} else if(userInput.equalsIgnoreCase("n")) {
					System.exit(0);
				} else {
					System.out.println("Wrong input. Please type in y/n.");
				}
			}
		}
		scanner.close();
		
//		if(args.length < 3) {
//			System.err.println("Invalid number of arguments. Type in -h for help");
//		}
	}
	
	//TODO check for duplicates
	/**
	 * Adds the know sponsors
	 */
	private static void addKnownSponsors() {
		sponsors.add("ACM");
		sponsors.add("SPEC");
		sponsors.add("UNESCO");
		sponsors.add("IEEE");
		sponsors.add("AFIS");
		sponsors.add("INCOSE");
	}
	
	//TODO check for duplicates
	/**
	 * Adds the known proceedings
	 */
	private static void addKnownProceedings() {
		proceedings.add("ACM");
		proceedings.add("SPEC");
		proceedings.add("Springer");
		proceedings.add("IEEE");
		proceedings.add("IFIP");
	}
	
	//TODO check for duplicates
	/**
	 * Adds the known names that appear in the committee headings
	 */
	private static void addPotentialCommitteeNames() {
		committees.add("committee");
		committees.add("chair");
		committees.add("paper");
		committees.add("member");
	}
	
	/** 
	 * Add a sponsor to the existing list of sponsors
	 * @param sponsor
	 */
	private void addSponsor(String sponsor) {
		this.sponsors.add(sponsor);
	}
	
	/**
	 * Add a proceeding to the existing list of proceedings
	 * @param proceeding
	 */
	private void addProceeding(String proceeding) {
		this.proceedings.add(proceeding);
	}
	
	/**
	 * Add a committee name to the exisitng list of committee names
	 * @param name
	 */
	private void addCommitteeName(String name) {
		this.committees.add(name);
	}
	
	/** 
	 * Remove a sponsor from the existing list of sponsors
	 * @param sponsor
	 */
	private void removeSponsor(String sponsor) {
		this.sponsors.add(sponsor);
	}
	
	/**
	 * Remove a proceeding from the existing list of proceedings
	 * @param proceeding
	 */
	private void removeProceeding(String proceeding) {
		this.proceedings.add(proceeding);
	}
	
	/**
	 * Remove a committee name from the exisitng list of committee names
	 * @param name
	 */
	private void removeCommitteeName(String name) {
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
