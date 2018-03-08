package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class Information {
	private static ArrayList<String> links;
	private static ArrayList<String> sponsors;
	private static ArrayList<String> proceedings;
	private static ArrayList<String> committees;
	
	private static Connection connection;
	private static PreparedStatement preparedStmt;
	private static DBConnection conn;

	public Information() {
		conn = new DBConnection();
		connection = conn.createConnection();
		links = new ArrayList<String>();
		sponsors = new ArrayList<String>();
		proceedings = new ArrayList<String>();
		committees = new ArrayList<String>();
		try {
			this.getLinksFromDB();
			this.getSponsorsFromDB();
			this.getProceedingsFromDB();
			this.getCommitteeNamesFromDB();
		} catch (SQLException e) {
		}
	}
	
//	public static void main(String[] args) {
//		try {
//			Information info = new Information();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * Get the list of links that are used in the system
	 * @throws SQLException
	 */
	private void getLinksFromDB() throws SQLException {
		String query = "select link from links_to_crawl";
		preparedStmt = connection.prepareStatement(query);
		this.checkAndAddToList(preparedStmt.executeQuery(), links);
	}
	
	/**
	 * Get the list of sponsors that are used in the system
	 * @throws SQLException
	 */
	private void getSponsorsFromDB() throws SQLException {
		String query = "select sponsor from available_sponsors";
		preparedStmt = connection.prepareStatement(query);
		this.checkAndAddToList(preparedStmt.executeQuery(), sponsors);
	}
	
	/**
	 * Get a list of proceedings that are used in the system
	 * @throws SQLException
	 */
	private void getProceedingsFromDB() throws SQLException {
		String query = "select proceeding from available_proceedings";
		preparedStmt = connection.prepareStatement(query);
		this.checkAndAddToList(preparedStmt.executeQuery(), proceedings);
	}
	
	/**
	 * Get a list of potential committee names that are used in the system
	 * @throws SQLException
	 */
	private void getCommitteeNamesFromDB() throws SQLException {
		String query = "select potential_com_name from potential_committee_names";
		preparedStmt = connection.prepareStatement(query);
		this.checkAndAddToList(preparedStmt.executeQuery(), committees);
	}
	
	/**
	 * Add values from the result set to the list if they are not duplicates
	 * @param result set from a query
	 * @param list to be populated
	 * @throws SQLException
	 */
	private void checkAndAddToList(ResultSet rs, ArrayList<String> list) throws SQLException {
		String value;
		while (rs.next()) {
			// Find the id from the set
			value = rs.getString(1);
			if(!list.contains(value))
				list.add(value);
		}
	}
	
	/** 
	 * Retrieve all links from the configuration in the database 
	 * @param sponsor
	 * @throws SQLException 
	 */
	ArrayList<String> listLinks() throws SQLException {
		String checkQuery = "select link from links_to_crawl";
		preparedStmt = connection.prepareStatement(checkQuery);
		ResultSet rs = preparedStmt.executeQuery();
		
		ArrayList<String> toReturn = new ArrayList<>();

		while (rs.next())
			toReturn.add(rs.getString(1));
		
		return toReturn;
	}
	
	/** 
	 * Retrieve all sponsors from the configuration in the database 
	 * @param sponsor
	 * @throws SQLException 
	 */
	ArrayList<String> listSponsors() throws SQLException {
		String checkQuery = "select sponsor from available_sponsors";
		preparedStmt = connection.prepareStatement(checkQuery);
		ResultSet rs = preparedStmt.executeQuery();
		
		ArrayList<String> toReturn = new ArrayList<>();

		while (rs.next())
			toReturn.add(rs.getString(1));
		
		return toReturn;
	}
	
	/** 
	 * Retrieve all proceedings from the configuration in the database 
	 * @param sponsor
	 * @throws SQLException 
	 */
	ArrayList<String> listProceedings() throws SQLException {
		String checkQuery = "select proceeding from available_proceedings";
		preparedStmt = connection.prepareStatement(checkQuery);
		ResultSet rs = preparedStmt.executeQuery();
		
		ArrayList<String> toReturn = new ArrayList<>();

		while (rs.next())
			toReturn.add(rs.getString(1));
		
		return toReturn;
	}
	
	/** 
	 * Retrieve all committee names from the configuration in the database 
	 * @param sponsor
	 * @throws SQLException 
	 */
	ArrayList<String> listCommitteeNames() throws SQLException {
		String checkQuery = "select potential_com_name from potential_committee_names";
		preparedStmt = connection.prepareStatement(checkQuery);
		ResultSet rs = preparedStmt.executeQuery();
		
		ArrayList<String> toReturn = new ArrayList<>();

		while (rs.next())
			toReturn.add(rs.getString(1));
		
		return toReturn;
	}
	
	/** 
	 * Add a link to the configuration in the database 
	 * @param sponsor
	 * @throws SQLException 
	 */
	boolean addLink(String link) throws SQLException {
		String checkQuery = "select link from links_to_crawl where link = ?";
		String insertQuery = "insert links_to_crawl (link) values (?)";
		boolean check = this.checkIfExists(checkQuery, insertQuery, link);

		// If the check comes back as true then this link already exists in the database
		if (check) {
			return false;
		} else {
			return true;
		}
	}
	
	/** 
	 * Add a sponsor to the configuration in the database 
	 * @param sponsor
	 * @throws SQLException 
	 */
	boolean addSponsor(String sponsor) throws SQLException {
		String checkQuery = "select sponsor from available_sponsors where sponsor = ?";
		String insertQuery = "insert available_sponsors (sponsor) values (?)";
		boolean check = this.checkIfExists(checkQuery, insertQuery, sponsor);

		// If the check comes back as true then this sponsor already exists in the database
		if (check) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Add a proceeding to the configuration in the database 
	 * @param proceeding
	 * @throws SQLException 
	 */
	boolean addProceeding(String proceeding) throws SQLException {
		String checkQuery = "select proceeding from available_proceedings where proceeding = ?";
		String insertQuery = "insert available_proceedings (proceeding) values (?)";
		boolean check = this.checkIfExists(checkQuery, insertQuery, proceeding);

		// If the check comes back as true then this proceeding already exists in the database
		if (check) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Add a committee name to the configuration in the database 
	 * @param name
	 * @throws SQLException 
	 */
	boolean addCommitteeName(String name) throws SQLException {
		String checkQuery = "select potential_com_name from potential_committee_names where potential_com_name = ?";
		String insertQuery = "insert potential_committee_names (potential_com_name) values (?)";
		boolean check = this.checkIfExists(checkQuery, insertQuery, name);

		// If the check comes back as true then this committee name already exists in the database
		if (check) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Checks if the passed in value exists in the database using appropriate queries
	 * @param checkQuery
	 * @param insertQuery
	 * @param value
	 * @return true/false
	 * @throws SQLException
	 */
	private boolean checkIfExists(String checkQuery, String insertQuery, String value) throws SQLException {
		preparedStmt = connection.prepareStatement(checkQuery);
		preparedStmt.setString(1, value);
		ResultSet rs = preparedStmt.executeQuery();

		if (rs.next()) {
			return true;
		} else {
			preparedStmt = connection.prepareStatement(insertQuery);
			preparedStmt.setString(1, value);
			preparedStmt.executeUpdate();
			return false;
		}
	}
	
	/** 
	 * Remove a sponsor from the configuration in the database 
	 * @param sponsor
	 * @return true/false
	 * @throws SQLException 
	 */
	boolean removeLink(String link) throws SQLException {
		String deleteQuery = "delete from links_to_crawl where link = ?";
		return this.removeUsingQuery(deleteQuery, link, false);
	}
	
	/** 
	 * Remove a sponsor from the configuration in the database 
	 * @param sponsor
	 * @return true/false
	 * @throws SQLException 
	 */
	boolean removeSponsor(String sponsor) throws SQLException {
		String deleteQuery = "delete from available_sponsors where sponsor = ?";
		return this.removeUsingQuery(deleteQuery, sponsor, false);
	}
	
	/**
	 * Remove a proceeding from the configuration in the database 
	 * @param proceeding
	 * @return true/false
	 * @throws SQLException 
	 */
	boolean removeProceeding(String proceeding) throws SQLException {
		String deleteQuery = "delete from available_proceedings where proceeding = ?";
		return this.removeUsingQuery(deleteQuery, proceeding, false);
	}
	
	/**
	 * Remove a committee name from the configuration in the database 
	 * @param name
	 * @return true/false
	 * @throws SQLException 
	 */
	boolean removeCommitteeName(String name) throws SQLException {
		String deleteQuery = "delete from potential_committee_names where potential_com_name = ?";
		return this.removeUsingQuery(deleteQuery, name, false);
	}
	
	/**
	 * Remove every link from the configuration in the database
	 * @param name
	 * @return true/false
	 * @throws SQLException 
	 */
	boolean removeAllLinks() throws SQLException {
		String deleteQuery = "delete from links_to_crawl";
		return this.removeUsingQuery(deleteQuery, null, true);
	}
	
	/**
	 * Remove every sponsor from the configuration in the database
	 * @param name
	 * @return true/false
	 * @throws SQLException 
	 */
	boolean removeAllSponsors() throws SQLException {
		String deleteQuery = "delete from available_sponsors";
		return this.removeUsingQuery(deleteQuery, null, true);
	}
	
	/**
	 * Remove every proceedings from the configuration in the database
	 * @param name
	 * @return true/false
	 * @throws SQLException 
	 */
	boolean removeAllProceedings() throws SQLException {
		String deleteQuery = "delete from available_proceedings";
		return this.removeUsingQuery(deleteQuery, null, true);
	}
	
	/**
	 * Remove every committee name from the configuration in the database
	 * @param name
	 * @return true/false
	 * @throws SQLException 
	 */
	boolean removeAllCommitteeNames() throws SQLException {
		String deleteQuery = "delete from potential_committee_names";
		return this.removeUsingQuery(deleteQuery, null, true);
	}
	
	/**
	 * Use the given query, value (if any is passed in) and a boolean to
	 * switch between removing everything or just one value
	 * @param query
	 * @param value
	 * @param true/false
	 * @return true/false
	 * @throws SQLException
	 */
	private boolean removeUsingQuery(String query, String value, boolean removeAll) throws SQLException {
		if(!removeAll) {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, value);
		} else {
			preparedStmt = connection.prepareStatement(query);
		}
		
		int affectedRows = preparedStmt.executeUpdate();
		
		if(affectedRows == 0)
			return false;
		else
			return true;
	}
	
	/**
	 * @return list of links
	 */
	public ArrayList<String> getLinks() {
		return links;
	}
	
	/**
	 * @return list of sponsors
	 */
	public ArrayList<String> getSponsors() {
		return sponsors;
	}
	
	/**
	 * @return list of proceedings
	 */
	public ArrayList<String> getProceedings() {
		return proceedings;
	}

	/**
	 * @return list of committee names
	 */
	public ArrayList<String> getCommitteeNames() {
		return committees;
	}
}
