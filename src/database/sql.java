package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A Java MySQL PreparedStatement INSERT example. Demonstrates the use of a SQL
 * INSERT statement against a MySQL database, called from a Java program, using
 * a Java PreparedStatement.
 * 
 * Created by Alvin Alexander, http://alvinalexander.com
 */
public class sql {

	private static Connection connection;
	private static PreparedStatement preparedStmt;

	public sql() {
		this.createConnection();
	}

	/**
	 * Create a mysql database connection
	 */
	public void createConnection() {
		String driver = "org.gjt.mm.mysql.Driver";
		String url = "jdbc:mysql://localhost:3306/conferences";
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, "root", "");
		} catch (ClassNotFoundException e) {
			System.err.println("Couldn't find class with name: " + driver);
		} catch (SQLException e) {
			System.err.println("Couldn't establish a connection with database using \"" + url + "\"");
		}
	}

	/**
	 * Close the mysql database connection
	 * @throws SQLException
	 */
	public void closeConnection() throws SQLException {
		connection.close();
	}

	public void addNewConference(String acronym, String title, String sponsors, String proceedings, String description,
			String venue, String currentYear, String antiquity, String conferenceDays,
			LinkedHashMap<String, List<String>> organisers,
			LinkedHashMap<String, LinkedHashMap<String, String>> deadlines) throws SQLException {
		String query;
		int venueID = this.addToVenues(venue);
		int id = this.addToWebsites(acronym, title, sponsors, proceedings, description, venueID, currentYear, antiquity,
				conferenceDays);
		
		query = "insert deadlines (id, deadline_type, deadline_id) values (?, ?, ?)";
		
		// Iterate through the map
		for(String key: deadlines.keySet()) {
			LinkedHashMap<String, String> actualDeadlines = deadlines.get(key);
			
			// The key will either be a string or an integer
			try {
				// If the key is an integer, it means there was no type of deadline on the website
				Integer.parseInt(key);
				key = "";
			} catch (NumberFormatException e) {
				// If it isn't an integer then there is a type present and we don't need to change anything
			}
			// This will be the same for one iteration of the map values so only call it once
			int typeID = this.addToDeadlineTypes(key);
			
			// Populate the tables in the database
			for(String dTitle: actualDeadlines.keySet()) {
				this.addToTable(query, id, typeID, this.addToDeadlineTitles(dTitle, actualDeadlines.get(dTitle)));
			}
		}
		
		query = "insert committees (id, titleID, memberID)" + " values (?, ?, ?)";
		int comTitleID;
		
		// Iterate through the map
		for(String committeeTitle: organisers.keySet()) {
			comTitleID = this.addToCommitteeTitles(committeeTitle);
			// Populate the tables in the database
			for(String member: organisers.get(committeeTitle)) {
				this.addToTable(query, id, comTitleID, this.addToMemberNames(member));
			}
		}
		
	}

//	public static void main(String[] args) {
//		try {
//			// PreparedStatement preparedStmt;
//			// String getQuery = "select acronym, committee_name, member from websites,
//			// com_names, member_names, committees where websites.id = 1 and websites.id =
//			// committees.id and committees.titleID = com_names.id and committees.memberID =
//			// member_names.id";
//			// preparedStmt = connection.prepareStatement(getQuery);
//			// ResultSet rs = preparedStmt.executeQuery();
//			//
//			// if(rs.next()) {
//			// System.out.println(rs.getString(1));
//			// System.out.println(rs.getString(2));
//			// System.out.println(rs.getString(3));
//			// }
//		} catch (Exception e) {
//			System.err.println("Got an exception!");
//			System.err.println(e.getMessage());
//		}
//	}

	/**
	 * Adds the venue to the venues table if it doesn't exist yet
	 * @param venue
	 * @return id of the row containing the venue
	 * @throws SQLException
	 */
	private int addToVenues(String venue) throws SQLException {
		// Create the search query
		String searchQuery = "select id from venues where venue = \"" + venue + "\"";
		String venueQuery = "insert venues (venue) values (?)";
		return this.checkAndReturn(searchQuery, venueQuery, venue);
	}

	/**
	 * Create a new entry in the websites table using the passed in parameters.
	 * @param acronym
	 * @param title
	 * @param sponsors
	 * @param proceedings
	 * @param description
	 * @param venueID
	 * @param currentYear
	 * @param antiquity
	 * @param conferenceDays
	 * @return id of the created row
	 * @throws SQLException
	 */
	private int addToWebsites(String acronym, String title, String sponsors, String proceedings, String description,
			int venueID, String currentYear, String antiquity, String conferenceDays) throws SQLException {
		// Create the query
		String mainQuery = "insert websites (acronym, title, sponsors, proceedings, description, venueID, current_year, antiquity, conference_days) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		preparedStmt = connection.prepareStatement(mainQuery, Statement.RETURN_GENERATED_KEYS);

		// Set the variables
		preparedStmt.setString(1, acronym);
		preparedStmt.setString(2, title);
		preparedStmt.setString(3, sponsors);
		preparedStmt.setString(4, proceedings);
		preparedStmt.setString(5, description);
		preparedStmt.setInt(6, venueID);
		int yearInteger = 0;
		// Change the year string to an integer
		try {
			yearInteger = Integer.parseInt(currentYear);
		} catch (NumberFormatException e) {
		}

		preparedStmt.setInt(7, yearInteger);
		preparedStmt.setString(8, antiquity);
		preparedStmt.setString(9, conferenceDays);
		
		return this.executeStatement(preparedStmt);
	}

	private int addToDeadlineTypes(String type) throws SQLException {
		// Check to lower case to avoid duplicates with capital letters etc.
		String searchType = "select id from deadline_types where lower(d_type) = \"" + type.toLowerCase() + "\"";
		String deadlineType = "insert deadline_types (d_type) values (?)";
		return this.checkAndReturn(searchType, deadlineType, type);
	}
	
	private int addToDeadlineTitles(String title, String date) throws SQLException {
		String deadlineTitle = "insert deadlines_titles (d_title, d_date) values (?, ?)";
		preparedStmt = connection.prepareStatement(deadlineTitle, Statement.RETURN_GENERATED_KEYS);
		// Set the variables
		preparedStmt.setString(1, title);
		preparedStmt.setString(2, date);
		
		return this.executeStatement(preparedStmt);
	}
	
	/**
	 * Adds the gathered ids into the specified table in the query which holds all 
	 * information(references) about other tables relative to the topic
	 * @param id
	 * @param titleID
	 * @param memberID
	 * @throws SQLException
	 */
	private void addToTable(String query, int mainID, int secondID, int thirdID) throws SQLException {
		preparedStmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
		// Set the variables
		preparedStmt.setInt(1, mainID);
		preparedStmt.setInt(2, secondID);
		preparedStmt.setInt(3, thirdID);
		
		// Execute the statement and get the number of rows changed
		int rows = preparedStmt.executeUpdate();

		if (rows == 0) {
			throw new SQLException("Creating committees row entry failed. No rows affected.");
		}
	}
	
	/**
	 * Adds the committee title to the committee_titles table if it doesn't exist yet
	 * @param comTitle
	 * @return id of row containing the title
	 * @throws SQLException
	 */
	private int addToCommitteeTitles(String comTitle) throws SQLException {
		// Check to lower case to avoid duplicates with capital letters etc.
		String searchComTitle = "select id from committee_titles where lower(committee_title) = \"" + comTitle.toLowerCase() + "\"";
		String committeeTitle = "insert committee_titles (committee_title)" + " values (?)";
		return this.checkAndReturn(searchComTitle, committeeTitle, comTitle);
	}
	
	/**
	 * Adds the member to the member_names table if it doesn't exist yet
	 * @param member
	 * @return id of row containing the member
	 * @throws SQLException
	 */
	private int addToMemberNames(String member) throws SQLException {
		String searchMember = "select id from member_names where member = \"" + member + "\"";
		String committeeMember = "insert member_names (member)" + " values (?)";
		return this.checkAndReturn(searchMember, committeeMember, member);
	}

	/**
	 * Search if the item to be inserted already exists. If not then create a new
	 * row and return the id, otherwise return the found id of the item.
	 * 
	 * @param searchQuery
	 * @param insertQuery
	 * @param toInsert
	 * @return id of the row containing the item
	 * @throws SQLException
	 */
	private int checkAndReturn(String searchQuery, String insertQuery, String toInsert) throws SQLException {
		preparedStmt = connection.prepareStatement(searchQuery);
		ResultSet rs = preparedStmt.executeQuery();
		int id;

		if (rs.next()) {
			// Find the id from the set
			id = rs.getInt(1);
			System.out.println("found item id: " + id);
			return id;
		} else {
			preparedStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			preparedStmt.setString(1, toInsert);
			return this.executeStatement(preparedStmt);
		}
	}
	
	/**
	 * Executes the predefined passed in statement
	 * @param preparedStmt
	 * @return id of created row
	 * @throws SQLException
	 */
	private int executeStatement(PreparedStatement preparedStmt) throws SQLException {
		// Execute the statement and get the number of rows changed
		int id = preparedStmt.executeUpdate();

		if (id == 0) {
			throw new SQLException("Creating failed, no rows affected.");
		}

		try (ResultSet generatedKeys = preparedStmt.getGeneratedKeys()) {
			// Find out the new id of the item just inserted
			if (generatedKeys.next()) {
				id = generatedKeys.getInt(1);
				System.out.println("created item id: " + id);
				return id;
			} else {
				throw new SQLException("Creating failed, no ID obtained.");
			}
		}
	}
}