package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
	private Connection connection;
	/**
	 * Create a mysql database connection
	 */
	public Connection createConnection() {
		String driver = "org.gjt.mm.mysql.Driver";
		String url = "jdbc:mysql://localhost:3306/conferences";
		connection = null;
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, "root", "");
		} catch (ClassNotFoundException e) {
			System.err.println("Couldn't find class with name: " + driver);
		} catch (SQLException e) {
			System.err.println("Couldn't establish a connection with database using \"" + url + "\"");
		}
		return connection;
	}
	
	/**
	 * Close the mysql database connection
	 * @throws SQLException
	 */
	void closeConnection() throws SQLException {
		connection.close();
	}
	
	/**
	 * Executes the predefined passed in statement
	 * @param preparedStmt
	 * @return id of created row
	 * @throws SQLException
	 */
	int executeStatement(PreparedStatement preparedStmt, boolean isUpdate) throws SQLException {
		// Execute the statement and get the number of rows changed
		int id = preparedStmt.executeUpdate();
		
		// Not a serious error, just no rows affected
		if (id == 0) {
			System.err.println("Creating failed, no rows affected.");
			return id;
		}

		if(!isUpdate) {
			try (ResultSet generatedKeys = preparedStmt.getGeneratedKeys()) {
				// Find out the new id of the item just inserted
				if (generatedKeys.next()) {
					id = generatedKeys.getInt(1);
//					System.out.println("created item id: " + id);
					return id;
				} else {
					// The insert performed didn't insert anything
					throw new SQLException("Creating failed, no ID obtained.");
				}
			}
		}
		return id;
	}

}
