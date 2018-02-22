package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
	
	private void createConnection() {
		// Create a mysql database connection
		String driver = "org.gjt.mm.mysql.Driver";
		String url = "jdbc:mysql://localhost:3306/conferences";
		try {
			Class.forName(driver);
			this.connection = DriverManager.getConnection(url, "root", "");
		} catch (ClassNotFoundException e) {
			System.err.println("Couldn't find class with name: " + driver);
		} catch (SQLException e) {
			System.err.println("Couldn't establish a connection with database using \"" + url + "\"");
		}
	}
	
	public void addNewConference(String acronym, String title, String sponsors, String proceedings, String description, int venue, String currentYear, String antiquity, String conferenceDays) throws SQLException {
		this.addToWebsites(acronym, title, sponsors, proceedings, description, venue, currentYear, antiquity, conferenceDays);
	}
	
	public static void main(String[] args) {
		try {
			// the mysql insert statement
			// String venueQuery = "insert venues (venue) values (?)";
			//
			//
			// String deadlineType = "insert deadline_types (type) values (?)";
			// String deadlineTitle = "insert deadlines_titles (title, date) values (?, ?)";
			// String deadline = "insert deadlines (id, deadline_type, deadline_id) values (?, ?, ?)";

			String committeeTitle = "insert committee_titles (committee_title)" + " values (?)";
			String committeeMember = "insert member_names (member)" + " values (?)";
			String committee = "insert committees (id, titleID, memberID)" + " values (?, ?, ?)";

			// create the mysql insert preparedstatement
			

			String acronym = "ICPE";
			// preparedStmt.setInt (1, 0);
			
			

			String com = "General Chair";
			String queryCheck = "select id from committee_titles where committee_title = \"" + com + "\"";
			preparedStmt = connection.prepareStatement(queryCheck);
			ResultSet rs = preparedStmt.executeQuery();
			int comNameID;

			if (rs.next()) {
				comNameID = rs.getInt(1);
				System.out.println("committee name id: " + comNameID);
			} else {
				preparedStmt = connection.prepareStatement(committeeTitle, Statement.RETURN_GENERATED_KEYS);
				preparedStmt.setString(1, com);
				comNameID = preparedStmt.executeUpdate();

				if (comNameID == 0) {
					throw new SQLException("Creating user failed, no rows affected.");
				}

				try (ResultSet generatedKeys = preparedStmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						comNameID = generatedKeys.getInt(1);
						System.out.println("committee name id: " + comNameID);
					} else {
						throw new SQLException("Creating user failed, no ID obtained.");
					}
				}
			}

			String member = "John Doe, Dublin, UCD";
			queryCheck = "select id from member_names where member = \"" + member + "\"";
			preparedStmt = connection.prepareStatement(queryCheck);
			rs = preparedStmt.executeQuery();
			int memberID;

			if (rs.next()) {
				memberID = rs.getInt(1);
				System.out.println("member id: " + memberID);
			} else {
				preparedStmt = connection.prepareStatement(committeeMember, Statement.RETURN_GENERATED_KEYS);
				preparedStmt.setString(1, member);

				memberID = preparedStmt.executeUpdate();

				if (memberID == 0) {
					throw new SQLException("Creating user failed, no rows affected.");
				}

				try (ResultSet generatedKeys = preparedStmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						memberID = generatedKeys.getInt(1);
						System.out.println("member id: " + memberID);
					} else {
						throw new SQLException("Creating user failed, no ID obtained.");
					}
				}
			}

//			preparedStmt = connection.prepareStatement(committee);
//			preparedStmt.setInt(1, mainID);
//			preparedStmt.setInt(2, comNameID);
//			preparedStmt.setInt(3, memberID);
//
//			preparedStmt.execute();

			// PreparedStatement preparedStmt;
			// String getQuery = "select acronym, committee_name, member from websites,
			// com_names, member_names, committees where websites.id = 1 and websites.id =
			// committees.id and committees.titleID = com_names.id and committees.memberID =
			// member_names.id";
			// preparedStmt = connection.prepareStatement(getQuery);
			// ResultSet rs = preparedStmt.executeQuery();
			//
			// if(rs.next()) {
			// System.out.println(rs.getString(1));
			// System.out.println(rs.getString(2));
			// System.out.println(rs.getString(3));
			// }

			connection.close();
		} catch (Exception e) {
			System.err.println("Got an exception!");
			System.err.println(e.getMessage());
		}
	}

	private int addToWebsites(String acronym, String title, String sponsors, String proceedings, String description, int venue, String currentYear, String antiquity,
		String conferenceDays) throws SQLException {
		// Create the query
		String mainQuery = "insert websites (acronym, title, sponsors, proceedings, description, venue, current_year, antiquity, conference_days) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		preparedStmt = connection.prepareStatement(mainQuery, Statement.RETURN_GENERATED_KEYS);

		// Set the variables
		preparedStmt.setString(1, acronym);
		preparedStmt.setString(2, title);
		preparedStmt.setString(3, sponsors);
		preparedStmt.setString(4, proceedings);
		preparedStmt.setString(5, description);
		preparedStmt.setInt(6, venue);
		int yearInteger = 0;
		// Change the year string to an integer
		try {
			yearInteger = Integer.parseInt(currentYear);
		} catch (NumberFormatException e) {
		}

		preparedStmt.setInt(7, yearInteger);
		preparedStmt.setString(8, antiquity);
		preparedStmt.setString(9, conferenceDays);
		// Execute the statement and get the number of rows changed
		int id = preparedStmt.executeUpdate();

		if (id == 0) {
			throw new SQLException("Creating websites row entry failed. No rows affected.");
		}

		try (ResultSet generatedKeys = preparedStmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				// Get the id of the created row
				id = generatedKeys.getInt(1);
				System.out.println("main id: " + id);
			} else {
				throw new SQLException("Creating websites row entry failed, no ID obtained.");
			}
		}
		return id;
	}
}