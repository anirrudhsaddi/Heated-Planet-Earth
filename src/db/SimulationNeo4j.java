package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public final class SimulationNeo4j implements IDBConnection {
	
	// Connection parameters
	private static final String JDBC_DRIVER = "org.neo4j.jdbc.Driver";  
	private static final String CONFIG_PATH	= "neo4j/config/config.properties";
	private static final String DB_PATH		= "neo4j/db/";
	private static final String NAME		= "simulationDb";
	private static final String URL 		= "jdbc:neo4j:instance:" + NAME;
	
	// Database connection object
	private static GraphDatabaseService simulationDb;
	private static Connection db = null;
	
	// Prepared Statement repository
	private static final ConcurrentHashMap<String, PreparedStatement> SAVED_QUERIES = new ConcurrentHashMap<String, PreparedStatement>();
	
	static {
		
		simulationDb = new GraphDatabaseFactory()
			.newEmbeddedDatabaseBuilder(DB_PATH)
			.loadPropertiesFromFile(CONFIG_PATH)
			.newGraphDatabase();
		
		registerShutdownHook(simulationDb);
		
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Unable to load the JDBC Driver: " + e);
		}
		
		Properties props = new Properties();
		props.put("simulationDb", simulationDb);
		
		try {
			db = DriverManager.getConnection(URL, props);
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to create a connection to the database " + NAME + ": " + e);
		}
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
			simulationDb.shutdown();
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
	
	private static void registerShutdownHook(final GraphDatabaseService db) {
		
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running application).
	    Runtime.getRuntime().addShutdownHook( new Thread() {
	        @Override
	        public void run() {
	        	db.shutdown();
	        }
	    } );
	}
}
