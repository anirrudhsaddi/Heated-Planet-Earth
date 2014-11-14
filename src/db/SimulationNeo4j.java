package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;

public final class SimulationNeo4j implements IDBConnection {
	
	// Connection parameters
	private static final String JDBC_DRIVER = "org.neo4j.jdbc.Driver";  
	private static final String DB_PATH		= "/db/";
	private static final String NAME		= "simulation.db";
	private static final String URL 		= "jdbc:neo4j:file:" + DB_PATH + NAME;
	private static final String USER 		= "simulation_user";
	private static final String PASSWORD 	= "p3t22";									// TODO this could be a char array for security
	
	// Database connection object
	private static Connection db = null;
	
	// Prepared Statement repository
	private static final ConcurrentHashMap<String, PreparedStatement> SAVED_QUERIES = new ConcurrentHashMap<String, PreparedStatement>();
	
	static {
		
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Unable to load the JDBC Driver: " + e);
		}
		
		try {
			db = DriverManager.getConnection(URL);
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to create a connection to the database " + NAME + ": " + e);
		}
		
		try {
			SimulationNeo4j.initDB();
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to initialize the database " + NAME + ": " + e);
		}
		
	}
	
	private static void initDB() throws SQLException {
		
		// Create or Upgrade Nodes
		throw new IllegalStateException("Not initializing DB");
		
	}
	
	public PreparedStatement createPreparedStatement(String queryName, String query) throws SQLException {
		
		if (SAVED_QUERIES.containsKey(queryName)) getPreparedStatement(queryName);
		
		PreparedStatement stmt = db.prepareStatement(query);
		SAVED_QUERIES.put(queryName, stmt);
		return stmt;
	}
	
	public PreparedStatement getPreparedStatement(String queryName) {
		
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
	
	public ResultSet query(PreparedStatement stmt) throws SQLException {
		
		if (stmt == null) throw new IllegalArgumentException("PreparedStatement must not be null");
		
		return stmt.executeQuery();
	}
	
	public ResultSet query(String query) throws SQLException {
		
		if (query == null) throw new IllegalArgumentException("Query string must not be null");
		
		Statement stmt = db.createStatement();
		return stmt.executeQuery(query);
	}
}
