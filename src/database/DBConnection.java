package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Main;

public class DBConnection {
	private Connection connection;
	static Logger logger = LogManager.getLogger(DBConnection.class);
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
			logger.fatal("Couldn't find class with name: " + driver + "\n" + e.getMessage());
		} catch (SQLException e) {
			logger.fatal("Couldn't establish a connection with database using \"" + url + "\"\n" + e.getMessage());
		}
		return connection;
	}
	
	/**
	 * Close the mysql database connection
	 * @throws SQLException
	 */
	void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			logger.fatal("Couldn't close connection with database.\n" + e.getMessage());
		}
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
			logger.info("Creating the row failed. No rows affected. It probably already exists. ");
			return id;
		}

		if(!isUpdate) {
			try (ResultSet generatedKeys = preparedStmt.getGeneratedKeys()) {
				// Find out the new id of the item just inserted
				if (generatedKeys.next()) {
					id = generatedKeys.getInt(1);
					return id;
				} else {
					// The insert performed didn't insert anything
					logger.error("Creating row failed. No ID obtained." );
				}
			}
		}
		return id;
	}

}
