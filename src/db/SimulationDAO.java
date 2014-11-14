package db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import messaging.Message;
import messaging.Publisher;
import messaging.events.DeliverMessage;
import common.ComponentBase;
import common.IGrid;
import common.ThreadManager;

public class SimulationDAO extends ComponentBase implements ISimDAO {

	private final IDBConnection conn;

	private String simulationName;

	// TODO move someplace else

	// Define the node creation statements
	private static String CREATE_SIMULATION_KEY					= "create_simulation_node";
	private static String CREATE_TEMP_KEY						= "create_temperature_node";
	private static String CREATE_AXIS_TILT_KEY					= "create_axis_node";
	private static String CREATE_ECCENTRICITY_KEY				= "create_eccentricity_node";
	private static String CREATE_GRID_SPACING_KEY				= "create_grid_spacing_node";
	private static String CREATE_TIME_STEP_KEY					= "create_time_step_node";
	private static String CREATE_PRESENTATION_INTERVAL_KEY		= "create_presentation_interval_node";
	private static String CREATE_SIMULATION_LENGTH_KEY			= "create_simulation_length_node";

	private static final String CREATE_SIMULATION_NODE 			  = "CREATE UNIQUE (n: Simulation { name : ? })";
	private static final String CREATE_TEMP_NODE 				  = "CREATE UNIQUE (n: Temperature { value : ? })";
	private static final String CREATE_AXIS_TILT_NODE 			  = "CREATE UNIQUE (n: AxislTilt { value : ? })";
	private static final String CREATE_ECCENTRICITY_NODE 		  = "CREATE UNIQUE (n: OrbitalEccentricity { value : ? })";
	private static final String CREATE_GRID_SPACING_NODE 		  = "CREATE UNIQUE (n: GridSpacing { value : ? })";
	private static final String CREATE_TIME_STEP_NODE 			  = "CREATE UNIQUE (n: TimeStep { value : ? })";
	private static final String CREATE_PRESENTATION_INTERVAL_NODE = "CREATE UNIQUE (n: PresentationInterval { value : ? })";
	private static final String CREATE_SIMULATION_LENGTH_NODE 	  = "CREATE UNIQUE (n: SimulationLength { value : ? })";

	// Define the relationships creation statements
	private static String CREATE_TEMP_REL_KEY					= "create_temp_rel";
	private static String CREATE_AXIS_REL_KEY					= "create_axis_rel";
	private static String CREATE_ECCENTRICITY_REL_KEY			= "create_eccentricity_rel";
	private static String CREATE_GRID_REL_KEY					= "create_grid_rel";
	private static String CREATE_TIME_REL_KEY					= "create_time_rel";
	private static String CREATE_PRESENTATIONAL_REL_KEY			= "create_presentational_rel";
	private static String CREATE_LENGTH_REL_KEY					= "create_length_rel";
	
	private static final String CREATE_TEMP_REL 				= "MATCH (a: Simulation), (b: Temperature) "
																+ "WHERE a.name = '?' AND b.value = '?' "
																+ "CREATE UNIQUE (a)-[r: HAS_TEMP { latitude : '?', longitude : '?', date : '?', time : '?' }]->(b)";
	private static final String CREATE_AXIS_REL 				= "MATCH (a: Simulation), (b: AxislTilt) "
																+ "WHERE a.name = '?' AND b.value = '?' "
																+ "CREATE UNIQUE (a)-[r: HAS_AXIS]->(b)";
	private static final String CREATE_ECCENTRICITY_REL 		= "MATCH (a: Simulation), (b: OrbitalEccentricity) "
																+ "WHERE a.name = '?' AND b.value = '?' "
																+ "CREATE UNIQUE (a)-[r: HAS_ECCENTRICITY]->(b)";
	private static final String CREATE_GRID_REL 				= "MATCH (a: Simulation), (b: GridSpacing) "
																+ "WHERE a.name = '?' AND b.value = '?' "
																+ "CREATE UNIQUE (a)-[r: HAS_GRID]->(b)";
	private static final String CREATE_TIME_REL 				= "MATCH (a: Simulation), (b: TimeStep) "
																+ "WHERE a.name = '?' AND b.value = '?' "
																+ "CREATE UNIQUE (a)-[r: HAS_TIME]->(b)";
	private static final String CREATE_PRESENTATIONAL_REL 		= "MATCH (a: Simulation), (b: PresentationInterval) "
																+ "WHERE a.name = '?' AND b.value = '?' "
																+ "CREATE UNIQUE (a)-[r: HAS_PRESENTATION]->(b)";
	private static final String CREATE_LENGTH_REL 				= "MATCH (a: Simulation), (b: SimulationLength) "
																+ "WHERE a.name = '?' AND b.value = '?' "
																+ "CREATE UNIQUE (a)-[r: HAS_LENGTH]->(b)";

	// Define the Query Statements
	private final static String MATCH_NODE_BY_NAME_KEY 			= "match_node_name";
	private final static String MATCH_NODE_BY_DATA_KEY 			= "match_node_values";
	private final static String GET_GRID_BY_DATE_TIME_KEY 		= "match_area_by_date";
	
	private final static String MATCH_NODE_BY_NAME_QUERY 		= "MATCH "
																+ "(n:Simulation)-[ "
																+ ":HAS_PRESENTATION|:HAS_TIME|:HAS_GRID|:HAS_ECCENTRICITY:|HAS_AXIS "
																+ "]->(o) "
																+ "WHERE n.name = ? "
																+ "RETURN { name: n.name, "
																+ "		result: filter(x IN o.values WHERE x = ? OR x = ? OR x = ? OR x = ? OR x = ? )"
																+ "}";
	private final static String MATCH_NODE_BY_DATA_QUERY 		= "MATCH "
																+ "(n:Simulation)-[ "
																+ ":HAS_PRESENTATION|:HAS_TIME|:HAS_GRID|:HAS_ECCENTRICITY:|HAS_AXIS "
																+ "]->(o) "
																+ "RETURN { name: n.name, "
																+ "		result: filter(x IN o.values WHERE x = ? OR x = ? OR x = ? OR x = ? OR x = ? )"
																+ "}";
	private final static String GET_GRID_BY_DATE_TIME_QUERY 	= "MATCH "
																+ "(n:Simulation)-[ "
																+ "r:HAS_TEMP "
																+ "]->(o:Temperature) "
																+ "WHERE ? <= r.latitude <= ? AND ? <= r.longitude <= ? AND ? <= r.date <= ? AND ? <= r.time <= ?" // ranges
																+ "RETURN o";

	/**
	 * Given a <code>IDBConnection</code>, create the Simulation <code>Data Access Object</code>
	 * @param conn
	 * @throws SQLException
	 */
	public SimulationDAO(final IDBConnection conn) throws SQLException {

		if (simulationName == null)
			throw new IllegalArgumentException(
					"Invalid Simulation Name provided");

		if (conn == null)
			throw new IllegalArgumentException("Invalid DB Connection object");

		this.conn = conn;

		this.conn.createPreparedStatement(MATCH_NODE_BY_NAME_KEY, MATCH_NODE_BY_NAME_QUERY);
		this.conn.createPreparedStatement(MATCH_NODE_BY_DATA_KEY, MATCH_NODE_BY_DATA_QUERY);
		this.conn.createPreparedStatement(GET_GRID_BY_DATE_TIME_KEY, GET_GRID_BY_DATE_TIME_QUERY);
		
		this.conn.createPreparedStatement(CREATE_SIMULATION_KEY, CREATE_SIMULATION_NODE);
		this.conn.createPreparedStatement(CREATE_TEMP_KEY, CREATE_TEMP_NODE);
		this.conn.createPreparedStatement(CREATE_AXIS_TILT_KEY, CREATE_AXIS_TILT_NODE);
		this.conn.createPreparedStatement(CREATE_ECCENTRICITY_KEY, CREATE_ECCENTRICITY_NODE);
		this.conn.createPreparedStatement(CREATE_GRID_SPACING_KEY, CREATE_GRID_SPACING_NODE);
		this.conn.createPreparedStatement(CREATE_TIME_STEP_KEY, CREATE_TIME_STEP_NODE);
		this.conn.createPreparedStatement(CREATE_PRESENTATION_INTERVAL_KEY, CREATE_PRESENTATION_INTERVAL_NODE);
		this.conn.createPreparedStatement(CREATE_SIMULATION_LENGTH_KEY, CREATE_SIMULATION_LENGTH_NODE);
		
		this.conn.createPreparedStatement(CREATE_TEMP_REL_KEY, CREATE_TEMP_REL);
		this.conn.createPreparedStatement(CREATE_AXIS_REL_KEY, CREATE_AXIS_REL);
		this.conn.createPreparedStatement(CREATE_ECCENTRICITY_REL_KEY, CREATE_ECCENTRICITY_REL);
		this.conn.createPreparedStatement(CREATE_GRID_REL_KEY, CREATE_GRID_REL);
		this.conn.createPreparedStatement(CREATE_TIME_REL_KEY, CREATE_TIME_REL);
		this.conn.createPreparedStatement(CREATE_PRESENTATIONAL_REL_KEY, CREATE_PRESENTATIONAL_REL);
		this.conn.createPreparedStatement(CREATE_LENGTH_REL_KEY, CREATE_LENGTH_REL);
		
		Publisher.getInstance().subscribe(DeliverMessage.class, this);
	}
	
	@Override
	public boolean createSimulationNode(String name) {
		
		PreparedStatement query = conn.getPreparedStatement(CREATE_SIMULATION_KEY);
		// TODO Set values
		try {
			return new Neo4jResult(conn.query(query));
		} catch (SQLException e) {
			return new Neo4jResult(e);
		}
	}
	
	@Override
	public boolean createTemperatureNode(int temperature) {
		
		PreparedStatement query = conn.getPreparedStatement(CREATE_TEMP_KEY);
		// TODO Set values
		try {
			return new Neo4jResult(conn.query(query));
		} catch (SQLException e) {
			return new Neo4jResult(e);
		}
	}
	
	@Override
	public boolean createAxisTilt(float axisTilt) {
		
		PreparedStatement query = conn.getPreparedStatement(CREATE_AXIS_TILT_KEY);
		// TODO Set values
		try {
			return new Neo4jResult(conn.query(query));
		} catch (SQLException e) {
			return new Neo4jResult(e);
		}
	}
	
	@Override
	public boolean createOrbitalEccentricityNode(float eccentricity) {
		
		PreparedStatement query = conn.getPreparedStatement(CREATE_ECCENTRICITY_KEY);
		// TODO Set values
		try {
			return new Neo4jResult(conn.query(query));
		} catch (SQLException e) {
			return new Neo4jResult(e);
		}
	}
	
	@Override
	public boolean createGridSpacingNode(int gridSpacing) {
		
		PreparedStatement query = conn.getPreparedStatement(CREATE_GRID_SPACING_KEY);
		// TODO Set values
		try {
			return new Neo4jResult(conn.query(query));
		} catch (SQLException e) {
			return new Neo4jResult(e);
		}
	}
	
	@Override
	public boolean createTimeStepNode(int timeStep) {
		
		PreparedStatement query = conn.getPreparedStatement(CREATE_TIME_STEP_KEY);
		// TODO Set values
		try {
			return new Neo4jResult(conn.query(query));
		} catch (SQLException e) {
			return new Neo4jResult(e);
		}
	}
	
	@Override
	public boolean createPresentationIntervalNode(float presentationInterval) {
		
		PreparedStatement query = conn.getPreparedStatement(CREATE_PRESENTATION_INTERVAL_KEY);
		// TODO Set values
		try {
			return new Neo4jResult(conn.query(query));
		} catch (SQLException e) {
			return new Neo4jResult(e);
		}
	}
	
	@Override
	public boolean createSimulationLengthNode(int simulationLength) {
		
		PreparedStatement query = conn.getPreparedStatement(CREATE_SIMULATION_LENGTH_KEY);
		// TODO Set values
		try {
			return new Neo4jResult(conn.query(query));
		} catch (SQLException e) {
			return new Neo4jResult(e);
		}
	}
	
	@Override
	public IQueryResult setSimulationName(String name, int gridSpacing, int timeStep, int simulationLength, float presentationInterval, float axisTilt, float eccentricity) throws Exception {
		
		// First do findSimulationByName
		// TODO What if we only want to look up by name?
		Future<IQueryResult> f = findSimulationByName(name, gridSpacing, timeStep, simulationLength, presentationInterval, axisTilt, eccentricity);
		
		IQueryResult result = f.get();
		
		this.simulationName = name;
		
		if (result.isEmpty()) {
			if (result.isErrored()) throw result.getError();
			
			// TODO Create links
			// TODO Set values
			// TODO return a custom built IQueryResult if all the creates worked
			return new Neo4jResult(name, gridSpacing, timeStep, simulationLength, presentationInterval, axisTilt, eccentricity);
		} else {
			return result;
		}
	}

	@Override
	public Future<IQueryResult> findSimulationByName(String name,
			int gridSpacing, int timeStep, int simulationLength,
			float presentationInterval, float axisTilt, float eccentricity) {

		PreparedStatement query = conn.getPreparedStatement(MATCH_NODE_BY_NAME_KEY);
		// TODO Set values
		return ThreadManager.getManager().submit(new Query(query));
	}

	@Override
	public Future<IQueryResult> findSimulationByData(int gridSpacing,
			int timeStep, int simulationLength, float presentationInterval,
			float axisTilt, float eccentricity) {

		PreparedStatement query = conn.getPreparedStatement(MATCH_NODE_BY_DATA_KEY);
		// TODO Set values
		return ThreadManager.getManager().submit(new Query(query));
	}

	@Override
	public Future<IQueryResult> findTemperaturesAt(String name,
			Calendar datetime, int[] locations) {

		PreparedStatement query = conn.getPreparedStatement(GET_GRID_BY_DATE_TIME_KEY);
		// TODO Set values
		return ThreadManager.getManager().submit(new Query(query));
	}

	@Override
	public void run() {

		while (!Thread.currentThread().isInterrupted() && !stopped.get()) {
			// just loop
		}
	}

	@Override
	public void onMessage(Message msg) {

		if (msg instanceof DeliverMessage) {
			offer(((DeliverMessage) msg).getGrid());
		} else {
			System.err
					.printf("WARNING: No processor specified in class %s for message %s\n",
							this.getClass().getName(), msg.getClass().getName());
		}
	}

	@Override
	protected void performAction(Message msg) {
		// Do nothing
	}

	private void offer(IGrid grid) {
		// check if we need to store this
		// if so, save physical data
		// loop through grid and create relationships
	}

	private class Query implements Callable<IQueryResult> {

		private final PreparedStatement query;

		public Query(PreparedStatement query) {
			if (query == null)
				throw new IllegalArgumentException(
						"Invalid PreparedStatement provided");

			this.query = query;
		}

		@Override
		public IQueryResult call() throws Exception {

			try {
				return new Neo4jResult(conn.query(query));
			} catch (SQLException e) {
				return new Neo4jResult(e);
			}
		}
	}
}
