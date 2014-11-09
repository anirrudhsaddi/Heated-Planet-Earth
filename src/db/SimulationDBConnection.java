package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

public final class SimulationDBConnection {
	
	// Connection parameters
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private static final String NAME		= "simulationdb";
	private static final String URL 		= "jdbc:mysql://localhost:3030/" + NAME;
	private static final String USER 		= "simulation_user";
	private static final String PASSWORD 	= "p3t22";									// TODO this could be a char array for security
	
	// Define the prepared SQL Statements
	private static PreparedStatement CREATE_NAMED_TABLE;
	
	// Define the one-time table creation statements queries
	private static final String CREATE_TEMP_TABLE = "CREATE TABLE TEMPERATURE ()";
	private static final String CREATE_PHYSICAL_DATA_TABLE = "CREATE TABLE PHYSICAL_DATA ()";
	
	// Database connection object
	private static Connection db = null;
	
	// Prepared Statement repository
	private static final ConcurrentHashMap<String, PreparedStatement> SAVED_QUERIES = new ConcurrentHashMap<String, PreparedStatement>();
	
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
			SimulationDBConnection.initDB();
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
		CREATE_NAMED_TABLE = db.prepareStatement("CREATE TABLE ? ()");
		
	}
	
	public static PreparedStatement createPreparedStatement(String queryName, String query) throws SQLException {
		
		PreparedStatement stmt = db.prepareStatement(query);
		repo.put(queryName, stmt);
		return stmt;
	}
	
	public static PreparedStatement getPreparedStatement(String queryName) {
		
		if (!SAVED_QUERIES.containsKey(queryName)) throw new IllegalArgumentException("Invalid Query name key");
		
		return SAVED_QUERIES.get(queryName);
	}
	
	public void close() {
		try {
			if (db != null || !db.isClosed()) db.close();
		} catch (SQLException e) {
			System.err.println("Failed to close database connection " + NAME + ": " + e);
		}
	}
	
	// TODO return future
	public void query(String queryName, String... args) {
		
		if (queryName == null) throw new IllegalArgumentException("PreparedStatement must not be null");
		
		PreparedStatement stmt = getPreparedStatement(queryName);
		
		ResultSet results = stmt.
	}
	
	public void query(String query) throws SQLException {
		
		if (query == null) throw new IllegalArgumentException("Query string must not be null");
		
		Statement stmt = db.createStatement();
		ResultSet result = stmt.executeQuery(query);
	}
}
