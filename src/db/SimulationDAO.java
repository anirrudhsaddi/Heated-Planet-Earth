package db;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import messaging.Message;
import messaging.Publisher;
import messaging.events.DeliverMessage;
import messaging.events.ResultMessage;
import common.ComponentBase;
import common.IGrid;
import common.ThreadManager;

public class SimulationDAO extends ComponentBase implements ISimulationDAO {

	private final IDBConnection conn;

	// TODO move someplace else
	
	// Define the DB constraints
	private static final String CREATE_NODE_NAME_CONSTRAINT			= "CREATE CONSTRAINT ON (n: Simulation) ASSERT n.name IS UNIQUE";
	
	private static final String FIND_SIMULATIONS_KEY				= "find_simulation_names";
			
	private static final String FIND_SIMULATIONS_QUERY				= "MATCH (a: Simulation) RETURN a";

	// Define the node creation statements
	private static final String CREATE_SIMULATION_KEY				= "create_simulation_node";
	private static final String CREATE_TEMP_KEY						= "create_temperature_node";
	private static final String CREATE_AXIS_TILT_KEY				= "create_axis_node";
	private static final String CREATE_ECCENTRICITY_KEY				= "create_eccentricity_node";
	private static final String CREATE_GRID_SPACING_KEY				= "create_grid_spacing_node";
	private static final String CREATE_TIME_STEP_KEY				= "create_time_step_node";
	private static final String CREATE_PRESENTATION_INTERVAL_KEY	= "create_presentation_interval_node";
	private static final String CREATE_SIMULATION_LENGTH_KEY		= "create_simulation_length_node";

	private static final String CREATE_SIMULATION_NODE 			  	= "CREATE UNIQUE (n: Simulation { name : \"?\" }) 			RETURN n";
	private static final String CREATE_TEMP_NODE 				  	= "CREATE UNIQUE (n: Temperature { value : ? }) 			RETURN n";
	private static final String CREATE_AXIS_TILT_NODE 			  	= "CREATE UNIQUE (n: AxislTilt { value : ? }) 				RETURN n";
	private static final String CREATE_ECCENTRICITY_NODE 		  	= "CREATE UNIQUE (n: OrbitalEccentricity { value : ? }) 	RETURN n";
	private static final String CREATE_GRID_SPACING_NODE 		  	= "CREATE UNIQUE (n: GridSpacing { value : ? }) 			RETURN n";
	private static final String CREATE_TIME_STEP_NODE 			  	= "CREATE UNIQUE (n: TimeStep { value : ? }) 				RETURN n";
	private static final String CREATE_PRESENTATION_INTERVAL_NODE 	= "CREATE UNIQUE (n: PresentationInterval { value : ? }) 	RETURN n";
	private static final String CREATE_SIMULATION_LENGTH_NODE 	  	= "CREATE UNIQUE (n: SimulationLength { value : ? }) 		RETURN n";

	// Define the relationships creation statements
	private static final String CREATE_TEMP_REL_KEY					= "create_temp_rel";
	private static final String CREATE_AXIS_REL_KEY					= "create_axis_rel";
	private static final String CREATE_ECCENTRICITY_REL_KEY			= "create_eccentricity_rel";
	private static final String CREATE_GRID_REL_KEY					= "create_grid_rel";
	private static final String CREATE_TIME_REL_KEY					= "create_time_rel";
	private static final String CREATE_PRESENTATIONAL_REL_KEY		= "create_presentational_rel";
	private static final String CREATE_LENGTH_REL_KEY				= "create_length_rel";
	
	private static final String CREATE_TEMP_REL 					= "MATCH (a: Simulation) WHERE a.name = \"?\" "
																	+ "CREATE UNIQUE (a)-[r: HAS_TEMP { latitude: ?, longitude: ?, datetime: ? }]->(b: Temperate {value: ?}) "
																	+ "RETURN a,r,b";
	
	private static final String CREATE_AXIS_REL 					= "MATCH (a: Simulation) WHERE a.name = \"?\" "
																	+ "CREATE UNIQUE (a)-[r: HAS_AXIS]->(b: AxisTilt {value: ?}) "
																	+ "RETURN a,r,b";
	
	private static final String CREATE_ECCENTRICITY_REL 			= "MATCH (a: Simulation) WHERE a.name = \"?\" "
																	+ "CREATE UNIQUE (a)-[r: HAS_ECCENTRICITY]->(b: OrbitalEccentricity {value: ?}) "
																	+ "RETURN a,r,b";
	
	private static final String CREATE_GRID_REL 					= "MATCH (a: Simulation) WHERE a.name = \"?\" "
																	+ "CREATE UNIQUE (a)-[r: HAS_GRID]->(b: GridSpacing {value: ?}) "
																	+ "RETURN a,r,b";
	
	private static final String CREATE_TIME_REL 					= "MATCH (a: Simulation) WHERE a.name = \"?\" "
																	+ "CREATE UNIQUE (a)-[r: HAS_TIME]->(b: TimeStep {value: ?}) "
																	+ "RETURN a,r,b";
	
	private static final String CREATE_PRESENTATIONAL_REL 			= "MATCH (a: Simulation) WHERE a.name = \"?\" "
																	+ "CREATE UNIQUE (a)-[r: HAS_PRESENTATION]->(b: PresentationInterval { value: ?}) "
																	+ "RETURN a,r,b";
	
	private static final String CREATE_LENGTH_REL 					= "MATCH (a: Simulation) WHERE a.name = \"?\" "
																	+ "CREATE UNIQUE (a)-[r: HAS_LENGTH]->(b: SimulationLength { value: ?}) "
																	+ "RETURN a,r,b";

	// Define the Query Statements
	private static final String MATCH_NODE_BY_NAME_KEY 				= "match_node_name";
	private static final String MATCH_NODE_BY_DATA_KEY 				= "match_node_values";
	private static final String GET_GRID_BY_DATE_TIME_KEY 			= "match_area_by_date";
	private static final String GET_DATE_TIME_KEY					= "match_closest_datetime";
	
	private static final String MATCH_NODE_BY_NAME_QUERY 			= "MATCH (n:Simulation)-[ "
																	+ ":HAS_PRESENTATION|:HAS_TIME|:HAS_GRID|:HAS_ECCENTRICITY|:HAS_AXIS "
																	+ "]->(o) "
																	+ "WHERE n.name = \"?\" "
																	+ "RETURN { name: n.name, result: o }";
	
	private static final String MATCH_NODE_BY_DATA_QUERY 			= "MATCH (n:Simulation)-[ "
																	+ ":HAS_PRESENTATION|:HAS_TIME|:HAS_GRID|:HAS_ECCENTRICITY:|HAS_AXIS "
																	+ "]->(o) "
																	+ "RETURN { name: n.name, "
																	+ "		result: filter(x IN o.values WHERE x = ? OR x = ? OR x = ? OR x = ? OR x = ? )"
																	+ "}";
	
	private static final String GET_GRID_BY_DATE_TIME_QUERY 		= "MATCH (n:Simulation)-[ r:HAS_TEMP ]->(t:Temperature) "
																	+ "WHERE n.name = \"?\" AND r.datetime = ? "
																	+ "WITH r.latitude as latitude, r.longitude as longitude, t.value as temperature "
																	+ "RETURN temperature, latitude, longitude ";
	
	private static final String GET_DATE_TIME_QUERY					= "MATCH (n:Simulation)-[r:HAS_TEMPERATURE]-(t:Temperature) "
																	+ "WHERE n.name = \"?\" AND r.datetime <= ? "
																	+ "WITH max(r.datetime) as datetime"
																	+ "RETURN datetime";
	
	// Sampling and storage parameters
	private final int precision, geoAccuracy, temporalAccuracy;
	
	/*
	 * TODO 
	 * Note that there may be more than one saved simulation that matches the specified Physical Factors. In this case, the system should select the best available.????
	 */

	/**
	 * Given a <code>IDBConnection</code>, create the Simulation <code>Data Access Object</code>
	 * @param conn
	 * @throws SQLException
	 */
	public SimulationDAO(int precision, int geoAccuracy, int temporalAccuracy, final IDBConnection conn) throws SQLException {

		if (conn == null)
			throw new IllegalArgumentException("Invalid DB Connection object provided");

		this.conn = conn;
		this.precision = precision;
		this.geoAccuracy = geoAccuracy;
		this.temporalAccuracy = temporalAccuracy;
		
		this.conn.createPreparedStatement(FIND_SIMULATIONS_KEY, FIND_SIMULATIONS_QUERY);

		this.conn.createPreparedStatement(MATCH_NODE_BY_NAME_KEY, MATCH_NODE_BY_NAME_QUERY);
		this.conn.createPreparedStatement(MATCH_NODE_BY_DATA_KEY, MATCH_NODE_BY_DATA_QUERY);
		this.conn.createPreparedStatement(GET_GRID_BY_DATE_TIME_KEY, GET_GRID_BY_DATE_TIME_QUERY);
		this.conn.createPreparedStatement(GET_DATE_TIME_KEY, GET_DATE_TIME_QUERY);
		
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
		
		this.conn.query(CREATE_NODE_NAME_CONSTRAINT);
	}
	
	@Override
	public ResultSet findNamedSimulations() throws SQLException {
		PreparedStatement query = conn.getPreparedStatement(FIND_SIMULATIONS_KEY);
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst())
			throw new SQLException("No Simulations found");
		return set;
	}
	
	@Override
	public boolean createSimulationNode(String name) throws SQLException {
		
		PreparedStatement query = conn.getPreparedStatement(CREATE_SIMULATION_KEY);
		query.setString(0, name);
		
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst()) return false;
		return true;
		
	}
	
	@Override
	public boolean createTemperatureRelationship(String name, int temperature) throws SQLException {
		
		PreparedStatement query = conn.getPreparedStatement(CREATE_TEMP_KEY);
		query.setString(0, name);
		query.setInt(1, temperature);
		
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst()) return false;
		return true;
	}
	
	@Override
	public boolean createAxisTiltRelationship(String name, float axisTilt) throws SQLException {
		
		PreparedStatement query = conn.getPreparedStatement(CREATE_AXIS_TILT_KEY);
		query.setString(0, name);
		query.setFloat(1, axisTilt);
		
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst()) return false;
		return true;
	}
	
	@Override
	public boolean createOrbitalEccentricityRelationship(String name, float eccentricity) throws SQLException {
		
		PreparedStatement query = conn.getPreparedStatement(CREATE_ECCENTRICITY_KEY);
		query.setString(0, name);
		query.setFloat(1, eccentricity);
		
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst()) return false;
		return true;
	}
	
	@Override
	public boolean createGridSpacingRelationship(String name, int gridSpacing) throws SQLException {
		
		PreparedStatement query = conn.getPreparedStatement(CREATE_GRID_SPACING_KEY);
		query.setString(0, name);
		query.setInt(1, gridSpacing);
		
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst()) return false;
		return true;
	}
	
	@Override
	public boolean createTimeStepRelationship(String name, int timeStep) throws SQLException {
		
		PreparedStatement query = conn.getPreparedStatement(CREATE_TIME_STEP_KEY);
		query.setString(0, name);
		query.setInt(1, timeStep);
		
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst()) return false;
		return true;
	}
	
	@Override
	public boolean createPresentationIntervalRelationship(String name, float presentationInterval) throws SQLException {
		
		PreparedStatement query = conn.getPreparedStatement(CREATE_PRESENTATION_INTERVAL_KEY);
		query.setString(0, name);
		query.setFloat(1, presentationInterval);
		
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst()) return false;
		return true;
	}
	
	@Override
	public boolean createSimulationLengthRelationship(String name, int simulationLength) throws SQLException {
		
		PreparedStatement query = conn.getPreparedStatement(CREATE_SIMULATION_LENGTH_KEY);
		query.setString(0, name);
		query.setInt(1, simulationLength);
		
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst()) return false;
		return true;
	}
	
	@Override
	public IQueryResult setSimulationName(String name, int gridSpacing, int timeStep, int simulationLength, float presentationInterval, float axisTilt, float eccentricity) throws Exception {
		
		// First do findSimulationByName
		Future<IQueryResult> f = findSimulationByData(gridSpacing, timeStep, simulationLength, presentationInterval, axisTilt, eccentricity);
		
		// Wait for the result back from the DB
		IQueryResult result = f.get();
		
		// If the result is empty, then the simulation does not exist and we should create it (or it errored)
		if (result.isEmpty()) {
			
			if (result.isErrored()) throw result.getError();
			
			if (!createSimulationNode(name))
				throw new SQLException("Failed to create Node(Simulation {" + name + "})");
			
			if (!createGridSpacingRelationship(name, gridSpacing))
				throw new SQLException("Failed to create Relationship Node(Simulation {" + name + "})-[:HAS_GRID]->Node(GridSpacing {" + gridSpacing + "})");
			
			if (!createTimeStepRelationship(name, timeStep))
				throw new SQLException("Failed to create Relationship Node(Simulation {" + name + "})-[:HAS_TIME]->Node(TimeStep {" + timeStep + "})");
			
			if (!createSimulationLengthRelationship(name, simulationLength))
				throw new SQLException("Failed to create Relationship Node(Simulation {" + name + "})-[:HAS_LENGTH]->Node(SimulationLength {" + simulationLength + "})");
			
			if (!createPresentationIntervalRelationship(name, presentationInterval))
				throw new SQLException("Failed to create Relationship Node(Simulation {" + name + "})-[:HAS_PRESENTATION]->Node(PresentationInterval {" + presentationInterval + "})");
			
			if (!createAxisTiltRelationship(name, axisTilt))
				throw new SQLException("Failed to create Relationship Node(Simulation {" + name + "})-[:HAS_AXIS]->Node(AxisTilt {" + axisTilt + "})");
			
			if (!createOrbitalEccentricityRelationship(name, eccentricity))
				throw new SQLException("Failed to create Relationship Node(Simulation {" + name + "})-[:HAS_ECCENTRICITY]->Node(OrbitalEccentricity {" + gridSpacing + "})");
			
			return new Neo4jResult(name, gridSpacing, timeStep, simulationLength, presentationInterval, axisTilt, eccentricity);
		} else 
			return result;
	}

	@Override
	public Future<IQueryResult> findSimulationByName(String name) throws SQLException {

		PreparedStatement query = conn.getPreparedStatement(MATCH_NODE_BY_NAME_KEY);
		
		query.setString(1, name);
		
		return ThreadManager.getManager().submit(new Query(query));
	}

	@Override
	public Future<IQueryResult> findSimulationByData(int gridSpacing,
			int timeStep, int simulationLength, float presentationInterval,
			float axisTilt, float eccentricity) throws SQLException {

		PreparedStatement query = conn.getPreparedStatement(MATCH_NODE_BY_DATA_KEY);
		
		query.setInt(1, gridSpacing);
		query.setInt(2, timeStep);
		query.setInt(3,  simulationLength);
		query.setFloat(4, presentationInterval);
		query.setFloat(5, axisTilt);
		query.setFloat(6, eccentricity);
		
		return ThreadManager.getManager().submit(new Query(query));
	}

	@Override
	public void findTemperaturesAt(String name, Calendar targetDateTime, int westLongitude, int eastLongitude, int northLatitude, int southLatitude) throws SQLException, InterruptedException, ExecutionException {

		/*
		 *  If so, we then need to derive the end datetime from the simulation length (which is in months. we also have the start date by default (see ControlGui)).
		 *   Using your engine, if data exists for that range, we need to error out and tell them so. 
		 *   Otherwise, we need to run a simulation from the last recorded datetime (if there is one) to the end date. 
		 *   This you can achieve by calling a findTemperatureAt with the endDate
		 */
		
		ResultSet result;
		long queryDateTime = targetDateTime.getTimeInMillis();
		
		// First, find the closest datetime-valued relationship
		PreparedStatement query = conn.getPreparedStatement(GET_DATE_TIME_KEY);
		query.setString(1, name);
		query.setLong(2, targetDateTime.getTimeInMillis());
		result = conn.query(query);
		if (!result.isBeforeFirst())
			throw new SQLException("Failed to find a date");
		
		// This should be the closest date available to us
		long foundDateTime = Long.parseLong(result.getString("datetime"));
		
		// Now get all the temps
		query = conn.getPreparedStatement(GET_GRID_BY_DATE_TIME_KEY);
		
		query.setString(1, name);
		query.setLong(2, foundDateTime);
		
		result = conn.query(query);
		if (!result.isBeforeFirst())
			throw new SQLException("Failed to find a temperatures");

		Publisher.getInstance().send(new ResultMessage(result, (foundDateTime == queryDateTime)));
	}

	@Override
	public void run() {

		while (!Thread.currentThread().isInterrupted() && !stopped.get()) {
			// just loop
		}
	}

	@Override
	public void onMessage(Message msg) {

		if (msg instanceof DeliverMessage)
			try {
				offer(((DeliverMessage) msg).getGrid());
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("Unable to add Grid to Database");
			}
		else
			System.err.printf("WARNING: No processor specified in class %s for message %s\n", this.getClass().getName(), msg.getClass().getName());
	}

	@Override
	protected void performAction(Message msg) {
		// Do nothing
	}

	private void offer(IGrid grid) throws SQLException {
		
		// Determine if we store this or not based on the two accuracy values
		
		long dateTime = grid.getDateTime();
		BigDecimal valueToStore;
		
		String name = grid.getSimulationName();
		PreparedStatement query = conn.getPreparedStatement(CREATE_TIME_REL_KEY);
		
		int width = grid.getGridWidth(), height = grid.getGridHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++ ) {
				valueToStore = new BigDecimal(grid.getTemperature(x, y));
				query.setString(1, name);
				query.setInt(2, getLatitude(y, height));
				query.setInt(3, getLongitude(x, width));
				query.setLong(4, dateTime);
				query.setDouble(5, valueToStore.setScale(this.precision, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
		}
	}

	private class Query implements Callable<IQueryResult> {

		private final PreparedStatement query;

		public Query(PreparedStatement query) {
			if (query == null)
				throw new IllegalArgumentException("Invalid PreparedStatement provided");

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
	
	private int getLatitude(int y, int height) {
		return (y - (height / 2)) * (180 / height);
	}

	private int getLongitude(int x, int width) {
		return x < (width / 2) ? -(x + 1) *  (2 * 180 / width) : (360) - (x + 1) * (2 * 180 / width);
	}
}
