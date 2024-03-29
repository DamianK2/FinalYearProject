package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Conference {

	private static Connection connection;
	private static PreparedStatement preparedStmt;
	private static DBConnection conn;
	private static final int MAX_CHARS_MEMBER = 300;
	private static final int MAX_CHARS_COM_TITLE = 200;
	static Logger logger = LogManager.getLogger(Conference.class);

	public Conference() {
		conn = new DBConnection();
		connection = conn.createConnection();
	}
	
	public void closeConnection() {
		conn.closeConnection();
	}
	
	/**
	 * Check if the conference already exists in the database
	 * @param acronym
	 * @param venueID
	 * @param currentYear
	 * @return id of row if it exists, otherwise -1
	 * @throws SQLException
	 */
	public synchronized int checkIfExists(String acronym, int venueID, String currentYear) throws SQLException {
		String checkQuery = "select id from websites where acronym = ? and venueID = ? and current_year = ?";
		preparedStmt = connection.prepareStatement(checkQuery);
		preparedStmt.setString(1, acronym);
		preparedStmt.setInt(2, venueID);
		preparedStmt.setString(3, currentYear);
		
		ResultSet rs = preparedStmt.executeQuery();
		int id;

		if (rs.next()) {
			// Find the id from the set
			id = rs.getInt(1);
			return id;
		} else {
			return -1;
		}
	}
	
	/**
	 * Updates the row in the websites table with the passed in id
	 * @param id
	 * @param acronym
	 * @param title
	 * @param sponsors
	 * @param proceedings
	 * @param description
	 * @param venueID
	 * @param currentYear
	 * @param antiquity
	 * @param conferenceDays
	 * @throws SQLException
	 */
	public synchronized void updateWebsites(int id, String acronym, String title, String sponsors, String proceedings, String description,
			int venueID, String currentYear, String antiquity, String conferenceDays, String link) throws SQLException {
		String updateQuery = "update websites set acronym = ?, title = ?, sponsors = ?, proceedings = ?, description = ?, venueID = ?, current_year = ?, antiquity = ?, conference_days = ?, link = ? where id = ?";
		preparedStmt = connection.prepareStatement(updateQuery, Statement.RETURN_GENERATED_KEYS);
		
		this.setValuesInStatement(acronym, title, sponsors, proceedings, description, venueID, currentYear, antiquity, conferenceDays, link);
		// Don't forget to tell which id we are updating
		preparedStmt.setInt(11, id);
		
		conn.executeStatement(preparedStmt, true);
	}
	
	/**
	 * Delete every entry associated with the passed in id in the table
	 * @param id
	 * @throws SQLException
	 */
	public synchronized void deleteFromTable(int id, String deleteQuery) throws SQLException {
		preparedStmt = connection.prepareStatement(deleteQuery, Statement.RETURN_GENERATED_KEYS);
		preparedStmt.setInt(1, id);
		// Delete everything with the given id from the table
		conn.executeStatement(preparedStmt, true);
	}
	
	/**
	 * Add the given deadlines into the deadlines table
	 * @param id
	 * @param deadlines
	 * @throws SQLException
	 */
	public synchronized void addToDeadlines(int id, LinkedHashMap<String, LinkedHashMap<String, String>> deadlines) throws SQLException {
		String checkQuery = "select id, deadline_type, deadline_id from deadlines where id = ? and deadline_type = ? and deadline_id = ?";
		String query = "insert deadlines (id, deadline_type, deadline_id) values (?, ?, ?)";
		
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
				int deadline_id = this.addToDeadlineTitles(dTitle, actualDeadlines.get(dTitle));
				if(!this.checkIfIDExistInDatabase(checkQuery, id, typeID, deadline_id))
					this.addToTable(query, id, typeID, deadline_id);
			}
		}
	}
	
	/**
	 * Add the given organisers into the committees table
	 * @param id
	 * @param organisers
	 * @throws SQLException
	 */
	public synchronized void addToCommittees(int id, LinkedHashMap<String, List<String>> organisers) throws SQLException {
		String checkQuery = "select id, titleID, memberID from committees where id = ? and titleID = ? and memberID = ?";
		String query = "insert committees (id, titleID, memberID) values (?, ?, ?)";
		int comTitleID;
		
		// Iterate through the map
		for(String committeeTitle: organisers.keySet()) {
			comTitleID = this.addToCommitteeTitles(committeeTitle);
			// If 0 is returned then it can't be added to the database
			if(comTitleID != 0) {
				// Populate the tables in the database
				for(String member: organisers.get(committeeTitle)) {
					int memberID = this.addToMemberNames(member);
					// If 0 is returned then it can't be added to the database
					if(memberID != 0) {
						// If it doesn't exist then add it to the database
						if(!this.checkIfIDExistInDatabase(checkQuery, id, comTitleID, memberID))
							this.addToTable(query, id, comTitleID, memberID);
					}
				}
			}
		}
	}
	
	/**
	 * Checks if the tables deadlines or committees based on passed in query
	 * already contain the IDs that are about to be added into the database
	 * @param query
	 * @param id
	 * @param secondID
	 * @param thirdID
	 * @return true/false
	 * @throws SQLException 
	 */
	private boolean checkIfIDExistInDatabase(String query, int id, int secondID, int thirdID) throws SQLException {
		preparedStmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		preparedStmt.setInt(1, id);
		preparedStmt.setInt(2, secondID);
		preparedStmt.setInt(3, thirdID);
		ResultSet rs = preparedStmt.executeQuery();
		
		// If something was returned then it exists in the database
		if(rs.next())
			return true;
		else
			return false;
	}

	/**
	 * Adds the venue to the venues table if it doesn't exist yet
	 * @param venue
	 * @return id of the row containing the venue
	 * @throws SQLException
	 */
	public synchronized int addToVenues(String venue) throws SQLException {
		// Create the search query
		String searchQuery = "select id from venues where venue = ?";
		preparedStmt = connection.prepareStatement(searchQuery);
		preparedStmt.setString(1, venue);
		String venueQuery = "insert venues (venue) values (?)";
		return this.checkAndReturn(venueQuery, venue);
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
	public synchronized int addToWebsites(String acronym, String title, String sponsors, String proceedings, String description,
			int venueID, String currentYear, String antiquity, String conferenceDays, String link) throws SQLException {
		// Create the query
		String mainQuery = "insert websites (acronym, title, sponsors, proceedings, description, venueID, current_year, antiquity, conference_days, link) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		preparedStmt = connection.prepareStatement(mainQuery, Statement.RETURN_GENERATED_KEYS);

		this.setValuesInStatement(acronym, title, sponsors, proceedings, description, venueID, currentYear, antiquity, conferenceDays, link);
		
		return conn.executeStatement(preparedStmt, false);
	}
	
	/**
	 * Set the selected values for the statement to be executed in the websites table
	 * @param acronym
	 * @param title
	 * @param sponsors
	 * @param proceedings
	 * @param description
	 * @param venueID
	 * @param currentYear
	 * @param antiquity
	 * @param conferenceDays
	 * @throws SQLException
	 */
	private void setValuesInStatement(String acronym, String title, String sponsors, String proceedings, String description,
			int venueID, String currentYear, String antiquity, String conferenceDays, String link) throws SQLException {
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
		preparedStmt.setString(10, link);
	}

	/**
	 * Add the type of deadline to the deadline_types table
	 * 
	 * @param type
	 * @return id of changed row
	 * @throws SQLException
	 */
	private int addToDeadlineTypes(String type) throws SQLException {
		// Check to lower case to avoid duplicates with capital letters etc.
		String searchType = "select id from deadline_types where lower(d_type) = ?";
		preparedStmt = connection.prepareStatement(searchType);
		preparedStmt.setString(1, type.toLowerCase());
		String deadlineType = "insert deadline_types (d_type) values (?)";
		return this.checkAndReturn(deadlineType, type);
	}
	
	/**
	 * Add the title and date to the deadlines_titles table
	 * 
	 * @param title
	 * @param date
	 * @return id of changed row
	 * @throws SQLException
	 */
	private int addToDeadlineTitles(String title, String date) throws SQLException {
		String deadlineTitle = "insert deadlines_titles (d_title, d_date) values (?, ?)";
		preparedStmt = connection.prepareStatement(deadlineTitle, Statement.RETURN_GENERATED_KEYS);
		// Set the variables
		preparedStmt.setString(1, title);
		preparedStmt.setString(2, date);
		
		return conn.executeStatement(preparedStmt, false);
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
			throw new SQLException("Creating row entry failed. No rows affected.");
		}
	}
	
	/**
	 * Adds the committee title to the committee_titles table if it doesn't exist yet
	 * @param comTitle
	 * @return id of row containing the title
	 * @throws SQLException
	 */
	private int addToCommitteeTitles(String comTitle) throws SQLException {
		if(comTitle.length() < MAX_CHARS_COM_TITLE) {
			// Check to lower case to avoid duplicates with capital letters etc.
			String searchComTitle = "select id from committee_titles where lower(committee_title) = ?";
			preparedStmt = connection.prepareStatement(searchComTitle);
			preparedStmt.setString(1, comTitle.toLowerCase());
			String committeeTitle = "insert committee_titles (committee_title)" + " values (?)";
			return this.checkAndReturn(committeeTitle, comTitle);
		} else
			return 0;
	}
	
	/**
	 * Adds the member to the member_names table if it doesn't exist yet
	 * @param member
	 * @return id of row containing the member
	 * @throws SQLException
	 */
	private int addToMemberNames(String member) throws SQLException {
		if(member.length() < MAX_CHARS_MEMBER) {
			String searchMember = "select id from member_names where member = ?";
			preparedStmt = connection.prepareStatement(searchMember);
			preparedStmt.setString(1, member);
			String committeeMember = "insert member_names (member)" + " values (?)";
			return this.checkAndReturn(committeeMember, member);
		} else
			return 0;
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
	private int checkAndReturn(String insertQuery, String toInsert) throws SQLException {
		ResultSet rs = preparedStmt.executeQuery();
		int id;

		if (rs.next()) {
			// Find the id from the set
			id = rs.getInt(1);
			return id;
		} else {
			preparedStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			preparedStmt.setString(1, toInsert);
			return conn.executeStatement(preparedStmt, false);
		}
	}
}