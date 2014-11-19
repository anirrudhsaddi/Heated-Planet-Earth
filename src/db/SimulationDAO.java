package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import messaging.Message;
import messaging.Publisher;
import messaging.events.PersistMessage;
import messaging.events.ResultMessage;
import common.ComponentBase;
import common.IGrid;
import common.ThreadManager;

public class SimulationDAO extends ComponentBase implements ISimulationDAO {

	private final IDBConnection conn;
	
	/*
	 * TODO 
	 * Note that there may be more than one saved simulation that matches the specified Physical Factors. In this case, the system should select the best available.????
	 */

	/**
	 * Given a <code>IDBConnection</code>, create the Simulation <code>Data Access Object</code>
	 * @param conn
	 * @throws SQLException
	 */
	public SimulationDAO(final IDBConnection conn) throws SQLException {

		if (conn == null)
			throw new IllegalArgumentException("Invalid DB Connection object provided");

		this.conn = conn;
		
		this.conn.createPreparedStatement(Neo4jConstants.FIND_SIMULATIONS_KEY, Neo4jConstants.FIND_SIMULATIONS_QUERY);

		this.conn.createPreparedStatement(Neo4jConstants.MATCH_NODE_BY_NAME_KEY, Neo4jConstants.MATCH_NODE_BY_NAME_QUERY);
		this.conn.createPreparedStatement(Neo4jConstants.MATCH_NODE_BY_DATA_KEY, Neo4jConstants.MATCH_NODE_BY_DATA_QUERY);
		this.conn.createPreparedStatement(Neo4jConstants.GET_GRID_BY_DATE_TIME_KEY, Neo4jConstants.GET_GRID_BY_DATE_TIME_QUERY);
		this.conn.createPreparedStatement(Neo4jConstants.GET_DATE_TIME_KEY, Neo4jConstants.GET_DATE_TIME_QUERY);
		
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_SIMULATION_KEY, Neo4jConstants.CREATE_SIMULATION_NODE);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_TEMP_KEY, Neo4jConstants.CREATE_TEMP_NODE);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_AXIS_TILT_KEY, Neo4jConstants.CREATE_AXIS_TILT_NODE);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_ECCENTRICITY_KEY, Neo4jConstants.CREATE_ECCENTRICITY_NODE);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_GRID_SPACING_KEY, Neo4jConstants.CREATE_GRID_SPACING_NODE);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_TIME_STEP_KEY, Neo4jConstants.CREATE_TIME_STEP_NODE);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_PRESENTATION_INTERVAL_KEY, Neo4jConstants.CREATE_PRESENTATION_INTERVAL_NODE);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_SIMULATION_LENGTH_KEY, Neo4jConstants.CREATE_SIMULATION_LENGTH_NODE);
		
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_TEMP_REL_KEY, Neo4jConstants.CREATE_TEMP_REL);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_AXIS_REL_KEY, Neo4jConstants.CREATE_AXIS_REL);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_ECCENTRICITY_REL_KEY, Neo4jConstants.CREATE_ECCENTRICITY_REL);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_GRID_REL_KEY, Neo4jConstants.CREATE_GRID_REL);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_TIME_REL_KEY, Neo4jConstants.CREATE_TIME_REL);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_PRESENTATIONAL_REL_KEY, Neo4jConstants.CREATE_PRESENTATIONAL_REL);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_LENGTH_REL_KEY, Neo4jConstants.CREATE_LENGTH_REL);
		
		Publisher.getInstance().subscribe(PersistMessage.class, this);
		
		this.conn.query(Neo4jConstants.CREATE_NODE_NAME_CONSTRAINT);
	}
	
	@Override
	public ResultSet findNamedSimulations() throws SQLException {
		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.FIND_SIMULATIONS_KEY);
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst())
			throw new SQLException("No Simulations found");
		return set;
	}
	
	@Override
	public boolean createSimulationNode(String name) throws SQLException {
		
		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_SIMULATION_KEY);
		query.setString(0, name);
		
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst()) return false;
		return true;
		
	}
	
	@Override
	public boolean createTemperatureRelationship(String name, int temperature) throws SQLException {
		
		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_TEMP_KEY);
		query.setString(0, name);
		query.setInt(1, temperature);
		
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst()) return false;
		return true;
	}
	
	@Override
	public boolean createAxisTiltRelationship(String name, float axisTilt) throws SQLException {
		
		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_AXIS_TILT_KEY);
		query.setString(0, name);
		query.setFloat(1, axisTilt);
		
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst()) return false;
		return true;
	}
	
	@Override
	public boolean createOrbitalEccentricityRelationship(String name, float eccentricity) throws SQLException {
		
		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_ECCENTRICITY_KEY);
		query.setString(0, name);
		query.setFloat(1, eccentricity);
		
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst()) return false;
		return true;
	}
	
	@Override
	public boolean createGridSpacingRelationship(String name, int gridSpacing) throws SQLException {
		
		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_GRID_SPACING_KEY);
		query.setString(0, name);
		query.setInt(1, gridSpacing);
		
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst()) return false;
		return true;
	}
	
	@Override
	public boolean createTimeStepRelationship(String name, int timeStep) throws SQLException {
		
		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_TIME_STEP_KEY);
		query.setString(0, name);
		query.setInt(1, timeStep);
		
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst()) return false;
		return true;
	}
	
	@Override
	public boolean createPresentationIntervalRelationship(String name, float presentationInterval) throws SQLException {
		
		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_PRESENTATION_INTERVAL_KEY);
		query.setString(0, name);
		query.setFloat(1, presentationInterval);
		
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst()) return false;
		return true;
	}
	
	@Override
	public boolean createSimulationLengthRelationship(String name, int simulationLength) throws SQLException {
		
		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_SIMULATION_LENGTH_KEY);
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

		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.MATCH_NODE_BY_NAME_KEY);
		
		query.setString(1, name);
		
		return ThreadManager.getManager().submit(new Query(query));
	}

	@Override
	public Future<IQueryResult> findSimulationByData(int gridSpacing,
			int timeStep, int simulationLength, float presentationInterval,
			float axisTilt, float eccentricity) throws SQLException {

		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.MATCH_NODE_BY_DATA_KEY);
		
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
		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.GET_DATE_TIME_KEY);
		query.setString(1, name);
		query.setLong(2, targetDateTime.getTimeInMillis());
		result = conn.query(query);
		if (!result.isBeforeFirst())
			throw new SQLException("Failed to find a date");
		
		// This should be the closest date available to us
		long foundDateTime = Long.parseLong(result.getString("datetime"));
		
		// Now get all the temps
		query = conn.getPreparedStatement(Neo4jConstants.GET_GRID_BY_DATE_TIME_KEY);
		
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

		if (msg instanceof PersistMessage)
			try {
				offer(((PersistMessage) msg));
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

	// TODO not an IGrid
	private void offer(PersistMessage msg) throws SQLException {
		
		// Determine if we store this or not based on the two accuracy values
		
		long dateTime = msg.getDateTime();
		
		String name = msg.getSimulationName();
		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_TIME_REL_KEY);
		
		int width = msg.getGridWidth(), height = msg.getGridHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++ ) {
				query.setString(1, name);
				query.setInt(2, getLatitude(y, height));
				query.setInt(3, getLongitude(x, width));
				query.setLong(4, dateTime);
				query.setDouble(5, msg.getTemperature(x, y));
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
