package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class SimulationDB {
	
	// Connection parameters
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private static final String NAME		= "simulationdb";
	private static final String URL 		= "jdbc:mysql://localhost:3030/" + NAME;
	private static final String USER 		= "simulation_user";
	private static final String PASSWORD 	= "p3t22";
	
	// Define the prepared SQL Statements
	private static PreparedStatement CREATE_NAMED_TABLE;
	
	// Define the one-time table creation statements queries
	private static final String CREATE_TEMP_TABLE = "CREATE TABLE TEMPERATURE ()";
	private static final String CREATE_PHYSICAL_DATA_TABLE = "CREATE TABLE PHYSICAL_DATA ()";
	
	// Database connection object
	private static Connection db = null;
	
	static {
		
		try {
			Class.forName(JDBC_DRIVER).newInstance();
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Unable to load the JDBC Driver: " + e);
		} catch (InstantiationException e) {
			throw new IllegalStateException("Unable to load the JDBC Driver: " + e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Unable to load the JDBC Driver: " + e);
		}
		
		try {
			db = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to create a connection to the database " + NAME + ": " + e);
		}
		
		try {
			SimulationDB.initDB();
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to initialize the database " + NAME + ": " + e);
		}
		
	}
	
	private static void initDB() throws SQLException {
		
		// Create or Upgrade tables
		Statement stmt = db.createStatement();
		stmt.addBatch(CREATE_TEMP_TABLE);
		stmt.addBatch(CREATE_PHYSICAL_DATA_TABLE);
		int[] success = stmt.executeBatch();
		// TODO Check results?
		
		db.commit();
		
		// Instantie the prepared statements
		CREATE_NAMED_TABLE = db.prepareStatement("");
		
	}
	
	public static void close() {
		try {
			if (db != null || !db.isClosed()) db.close();
		} catch (SQLException e) {
			System.err.println("Failed to close database connection " + NAME + ": " + e);
		}
	}
}
