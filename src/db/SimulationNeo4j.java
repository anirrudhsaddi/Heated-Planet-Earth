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
	
	// Define the prepared Cypher Statements
	private static PreparedStatement CREATE_QUERY_STMT;
	private static PreparedStatement CREATE_TEMP_STMT;
	private static PreparedStatement CREATE_AXIAL_STMT;
	private static PreparedStatement CREATE_ORBIAL_STMT;
	private static PreparedStatement CREATE_GRID_SPACING_STMT;
	private static PreparedStatement CREATE_TIME_STEP_STMT;
	private static PreparedStatement CREATE_PRESENTATION_RATE_STMT;
	
	private static PreparedStatement CREATE_QUERY_TEMP_REL_STMT;
	private static PreparedStatement CREATE_QUERY_AXIAL_REL_STMT;
	private static PreparedStatement CREATE_QUERY_ORBITAL_REL_STMT;
	private static PreparedStatement CREATE_QUERY_GRID_REL_STMT;
	private static PreparedStatement CREATE_QUERY_TIME_REL_STMT;
	private static PreparedStatement CREATE_QUERY_PRESENTATIONAL_REL_STMT;
	
	// Define the node creation statements
	private static final String CREATE_QUERY_NODE					= "CREATE (n:Query { name : ? })";
	private static final String CREATE_TEMP_NODE 					= "CREATE (n:Temperature { value : ? })";
	private static final String CREATE_AXIAL_DATA_NODE 				= "CREATE (n:Axial Data { value : ? })";
	private static final String CREATE_ORBIAL_DATA_NODE				= "CREATE (n:Orbital Eccentricity { value : ? })";
	private static final String CREATE_GRID_SPACING_DATA_NODE 		= "CREATE (n:Grid Spacing { value : ? })";
	private static final String CREATE_TIME_STEP_DATA_NODE 			= "CREATE (n:Time Step { value : ? })";
	private static final String CREATE_PRESENTATION_RATE_DATA_NODE 	= "CREATE (n:Presentation Rate { value : ? })";
	
	// Define the relationships creation statements
	private static final String CREATE_QUERY_TEMP_REL = "MATCH (a:Query),(b:Temperature) "
			+ "WHERE a.name = '?' AND b.value = '?' "
			+ "CREATE (a)-[r:RELTYPE { latitude : '?', longitude : '?', date : '?', time : '?' } ]->(b)";
	private static final String CREATE_QUERY_AXIAL_REL = "MATCH (a:Query),(b:Axial Data) "
			+ "WHERE a.name = '?' AND b.value = '?' "
			+ "CREATE (a)-[r:RELTYPE]->(b)";
	private static final String CREATE_QUERY_ORBITAL_REL = "MATCH (a:Query)(b:Orbital Eccentricity) "
			+ "WHERE a.name = '?' AND b.value = '?' "
			+ "CREATE (a)-[r:RELTYPE]->(b)";
	private static final String CREATE_QUERY_GRID_REL = "MATCH (a:Query)(b:Grid Spacing) "
			+ "WHERE a.name = '?' AND b.value = '?' "
			+ "CREATE (a)-[r:RELTYPE]->(b)";
	private static final String CREATE_QUERY_TIME_REL = "MATCH (a:Query)(b:Time Step) "
			+ "WHERE a.name = '?' AND b.value = '?' "
			+ "CREATE (a)-[r:RELTYPE]->(b)";
	private static final String CREATE_QUERY_PRESENTATIONAL_REL = "MATCH (a:Query)(b:Presentation Rate) "
			+ "WHERE a.name = '?' AND b.value = '?' "
			+ "CREATE (a)-[r:RELTYPE]->(b)";
	
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
		
		
	}
	
	public PreparedStatement createPreparedStatement(String queryName, String query) throws SQLException {
		
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
	
	public ResultSet query(String queryName, String... args) throws SQLException {
		
		if (queryName == null) throw new IllegalArgumentException("PreparedStatement must not be null");
		
		PreparedStatement stmt = getPreparedStatement(queryName);

		// apply args
		if (args != null && args.length > 0) {
			int index = 0;
			for (String arg : args) {
				stmt.setString(index++, arg);
			}
		}
		
		return stmt.executeQuery();
	}
	
	public ResultSet query(String query) throws SQLException {
		
		if (query == null) throw new IllegalArgumentException("Query string must not be null");
		
		Statement stmt = db.createStatement();
		return stmt.executeQuery(query);
	}
}
