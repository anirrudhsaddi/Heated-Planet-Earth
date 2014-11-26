package db;

import java.io.File;
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
	
	private static long current_db_size = 0;
	
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
		
		current_db_size = folderSize(new java.io.File(DB_PATH));
		System.out.printf("Size at DB start: %d bytes%n", current_db_size);
	}
	
	private static long folderSize(File directory) {
	    long length = 0;
	    for (File file : directory.listFiles()) {
	        if (file.isFile())
	            length += file.length();
	        else
	            length += folderSize(file);
	    }
	    return length;
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
		
		long currNTime = System.nanoTime();
		ResultSet result = stmt.executeQuery(query);
		System.out.printf("query '%s' took %dms %n", query, (System.nanoTime() - currNTime) / 1000000);
		
		return result;
	}
	
	private static void registerShutdownHook(final GraphDatabaseService db) {
		
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running application).
	    Runtime.getRuntime().addShutdownHook( new Thread() {
	        @Override
	        public void run() {
	        	
	        	long closing_size = folderSize(new java.io.File(DB_PATH));
	        	System.out.printf("Size at DB close: %d bytes. Delta: %d bytes%n", closing_size, (closing_size - current_db_size));
	        	db.shutdown();
	        }
	    } );
	}
}
