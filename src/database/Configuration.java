package database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Configuration {
	
	public static void main(String[] args) throws SQLException {
		Scanner scanner = new Scanner(System.in);
		Information info = new Information();
		
		if(args.length == 0) {
			System.err.println("Invalid number of arguments. Type in -h for help.");
		} else if(args[0].equals("-h")) {
			System.out.println("To see the list of currently used links, sponsors, proceedings or committees, type in -ls followed by -l, -s, -p or -c");
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
			try {
				String secondArg = args[1];
				ArrayList<String> list = new ArrayList<>();
				if(secondArg.equals("-l")) {
					System.out.println("Links:");
					list = info.listLinks();
				} else if(secondArg.equals("-s")) {
					System.out.println("Sponsors:");
					list = info.listSponsors();
				} else if(secondArg.equals("-p")) {
					System.out.println("Proceedings:");
					list = info.listProceedings();
				} else if(secondArg.equals("-c")) {
					System.out.println("Potential committee names:");
					list = info.listCommitteeNames();
				} else {
					System.err.println("Wrong second argument. Expected -l, -s, -p or -c.");
					System.exit(0);
				}
				
				for(String s: list)
					System.out.println("\t" + s);
				
			} catch(ArrayIndexOutOfBoundsException e) {
				System.err.println("Expected second argument but wasn't there.");
				System.exit(0);
			}
		} else if(args[0].equals("-ra")) {
			try {
				String secondArg = args[1];
				boolean removed = false;
				System.out.println("Are you sure you want to remove everything from the lists? (y/n)");
				boolean lgtm = awaitResponse(scanner);
				if(lgtm) {
					if(secondArg.equals("-l")) {
						removed = info.removeAllLinks();
					} else if(secondArg.equals("-s")) {
						removed = info.removeAllSponsors();
					} else if(secondArg.equals("-p")) {
						removed = info.removeAllProceedings();
					} else if(secondArg.equals("-c")) {
						removed = info.removeAllCommitteeNames();
					} else {
						System.err.println("Wrong second argument. Expected -l, -s, -p or -c.");
						System.exit(0);
					}
					
					if(removed)
						System.out.println("Successfully removed everything from the configuration.");
					else
						System.out.println("Nothing to remove.");
				} else
					System.out.println("Cancelled.");
			} catch(ArrayIndexOutOfBoundsException e) {
				System.err.println("Expected second argument but wasn't there.");
				System.exit(0);
			}
		} else if(args[0].equals("-a")) {
			String secondArg = "";
			String thirdArg = "";
			try {
				secondArg = args[1];
			} catch(ArrayIndexOutOfBoundsException e) {
				System.err.println("Expected second argument but wasn't there.");
				System.exit(0);
			}
			
			try {
				thirdArg = args[2];
			} catch(ArrayIndexOutOfBoundsException e) {
				System.err.println("Expected third argument but wasn't there.");
				System.exit(0);
			}
			
			boolean check = false;;
			if(secondArg.equals("-l")) {
				check = info.addLink(thirdArg);
			} else if(secondArg.equals("-s")) {
				check = info.addSponsor(thirdArg);
			} else if(secondArg.equals("-p")) {
				check = info.addProceeding(thirdArg);
			} else if(secondArg.equals("-c")) {
				check = info.addCommitteeName(thirdArg);
			} else {
				System.err.println("Wrong second argument. Expected -l, -s, -p or -c.");
				System.exit(0);
			}
			
			if(check)
				System.out.println("Succefully added \"" + thirdArg + "\" to the configuration.");
			else
				System.out.println("\"" + thirdArg + "\" already exists in the configuration.");
		} else if(args[0].equals("-r")) {
			String secondArg = "";
			String thirdArg = "";
			try {
				secondArg = args[1];
			} catch(ArrayIndexOutOfBoundsException e) {
				System.err.println("Expected second argument but wasn't there.");
				System.exit(0);
			}
			
			try {
				thirdArg = args[2];
			} catch(ArrayIndexOutOfBoundsException e) {
				System.err.println("Expected third argument but wasn't there.");
				System.exit(0);
			}
			
			boolean check = false;;
			if(secondArg.equals("-l")) {
				check = info.removeLink(thirdArg);
			} else if(secondArg.equals("-s")) {
				check = info.removeSponsor(thirdArg);
			} else if(secondArg.equals("-p")) {
				check = info.removeProceeding(thirdArg);
			} else if(secondArg.equals("-c")) {
				check = info.removeCommitteeName(thirdArg);
			} else {
				System.err.println("Wrong second argument. Expected -l, -s, -p or -c.");
				System.exit(0);
			}
			
			if(check)
				System.out.println("Succefully removed \"" + thirdArg + "\" from the configuration.");
			else
				System.out.println("\"" + thirdArg + "\" doesn't exist in the configuration.");
		} else {
			System.err.println("Expected -h, -ls, -ra, -a or -r but got: \"" + args[0] + "\"");
			System.exit(0);
		}
		scanner.close();
	}
	
	private static boolean awaitResponse(Scanner scanner) {
		String userInput;
		boolean correctInput = false;
		while(!correctInput) {
			userInput = scanner.nextLine();
			if(userInput.equalsIgnoreCase("y")) {
				return true;
			} else if(userInput.equalsIgnoreCase("n")) {
				return false;
			} else {
				System.out.println("Wrong input. Please type in y/n.");
			}
		}
		return false;
	}
}
