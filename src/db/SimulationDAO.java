package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import messaging.Message;
import messaging.Publisher;
import messaging.events.PauseMessage;
import messaging.events.PersistMessage;
import messaging.events.ResultMessage;
import messaging.events.ResumeMessage;
import messaging.events.StopMessage;
import common.ComponentBase;
import common.ThreadManager;

public class SimulationDAO extends ComponentBase implements ISimulationDAO {

	private final IDBConnection	conn;

	// private final ConcurrentLinkedQueue<Message> msgQueue = new
	// ConcurrentLinkedQueue<Message>();

	/**
	 * Given a <code>IDBConnection</code>, create the Simulation
	 * <code>Data Access Object</code>
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	public SimulationDAO(final IDBConnection conn) throws SQLException {

		if (conn == null)
			throw new IllegalArgumentException("Invalid DB Connection object provided");

		this.conn = conn;

		this.conn.createPreparedStatement(Neo4jConstants.FIND_SIMULATIONS_KEY, Neo4jConstants.FIND_SIMULATIONS_QUERY);
		this.conn.createPreparedStatement(Neo4jConstants.FIND_TEMPERATURES_KEY, Neo4jConstants.FIND_TEMPERATURES_QUERY);
		this.conn.createPreparedStatement(Neo4jConstants.FIND_TIME_STEP_KEY, Neo4jConstants.FIND_TIME_STEP_QUERY);
		this.conn.createPreparedStatement(Neo4jConstants.FIND_SIMULATION_LENGTH_KEY,
				Neo4jConstants.FIND_SIMULATION_LENGTH_QUERY);
		this.conn.createPreparedStatement(Neo4jConstants.FIND_AXIS_TILT_KEY, Neo4jConstants.FIND_AXIS_TILT_QUERY);
		this.conn.createPreparedStatement(Neo4jConstants.FIND_ORBITAL_ECCENTRICITY_KEY,
				Neo4jConstants.FIND_ORBITAL_ECCENTRICITY_QUERY);
		this.conn.createPreparedStatement(Neo4jConstants.FIND_GRID_SPACING_KEY, Neo4jConstants.FIND_GRID_SPACING_QUERY);
		this.conn.createPreparedStatement(Neo4jConstants.FIND_PRESENTATION_INTERVAL_KEY,
				Neo4jConstants.FIND_PRESENTATION_INTERVAL_QUERY);

		this.conn.createPreparedStatement(Neo4jConstants.FIND_SIMULATIONS_BY_NAME_KEY,
				Neo4jConstants.FIND_SIMULATIONS_BY_NAME_QUERY);
		this.conn.createPreparedStatement(Neo4jConstants.MATCH_NODE_BY_NAME_KEY,
				Neo4jConstants.MATCH_NODE_BY_NAME_QUERY);
		this.conn.createPreparedStatement(Neo4jConstants.MATCH_NODE_BY_DATA_KEY,
				Neo4jConstants.MATCH_NODE_BY_DATA_QUERY);
		this.conn.createPreparedStatement(Neo4jConstants.GET_GRID_BY_DATE_TIME_KEY,
				Neo4jConstants.GET_GRID_BY_DATE_TIME_QUERY);
		this.conn.createPreparedStatement(Neo4jConstants.GET_GRID_BY_DATE_TIME_RANGE_KEY,
				Neo4jConstants.GET_GRID_BY_DATE_TIME_RANGE_QUERY);
		this.conn.createPreparedStatement(Neo4jConstants.GET_DATE_TIME_KEY, Neo4jConstants.GET_DATE_TIME_QUERY);

		this.conn.createPreparedStatement(Neo4jConstants.CREATE_SIMULATION_KEY, Neo4jConstants.CREATE_SIMULATION_NODE);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_TEMP_KEY, Neo4jConstants.CREATE_TEMP_NODE);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_AXIS_TILT_KEY, Neo4jConstants.CREATE_AXIS_TILT_NODE);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_ORBITAL_ECCENTRICITY_KEY,
				Neo4jConstants.CREATE_ORBITAL_ECCENTRICITY_NODE);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_GRID_SPACING_KEY,
				Neo4jConstants.CREATE_GRID_SPACING_NODE);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_TIME_STEP_KEY, Neo4jConstants.CREATE_TIME_STEP_NODE);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_PRESENTATION_INTERVAL_KEY,
				Neo4jConstants.CREATE_PRESENTATION_INTERVAL_NODE);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_SIMULATION_LENGTH_KEY,
				Neo4jConstants.CREATE_SIMULATION_LENGTH_NODE);

		this.conn.createPreparedStatement(Neo4jConstants.CREATE_TEMP_REL_KEY, Neo4jConstants.CREATE_TEMP_REL);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_AXIS_REL_KEY, Neo4jConstants.CREATE_AXIS_REL);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_ECCENTRICITY_REL_KEY,
				Neo4jConstants.CREATE_ECCENTRICITY_REL);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_GRID_REL_KEY, Neo4jConstants.CREATE_GRID_REL);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_TIME_REL_KEY, Neo4jConstants.CREATE_TIME_REL);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_PRESENTATION_REL_KEY,
				Neo4jConstants.CREATE_PRESENTATIONAL_REL);
		this.conn.createPreparedStatement(Neo4jConstants.CREATE_LENGTH_REL_KEY, Neo4jConstants.CREATE_LENGTH_REL);

		this.conn.query(Neo4jConstants.CREATE_NODE_NAME_CONSTRAINT);
		this.conn.query(Neo4jConstants.CREATE_TEMPERATURE_CONSTRAINT);
		this.conn.query(Neo4jConstants.CREATE_AXIST_TILT_CONSTRAINT);
		this.conn.query(Neo4jConstants.CREATE_ORBITAL_ECCENTRICITY_CONSTRAINT);
		this.conn.query(Neo4jConstants.CREATE_GRID_SAPCING_CONSTRAINT);
		this.conn.query(Neo4jConstants.CREATE_TIME_STEP_CONSTRAINT);
		this.conn.query(Neo4jConstants.CREATE_PRESENTATION_INTERVAL_CONSTRAINT);
		this.conn.query(Neo4jConstants.CREATE_SIMULATION_LENGTH_CONSTRAINT);

		Publisher.getInstance().subscribe(PersistMessage.class, this);

		// ResultSet result = conn.query("MATCH (s:Simulation) RETURN s.name");
		// while(result.next())
		// System.out.println(result);

		// ResultSet result =
		// conn.query("MATCH (t:Temperature) RETURN t.value");
		// while(result.next())
		// System.out.println(result);

		ResultSet result = conn
				.query("MATCH (s:Simulation)-[r:HAS_TEMP]->(t:Temperature) RETURN s.name, r.longitude, r.latitude, r.datetime, t.value");
		while (result.next())
			System.out.println(result);
	}

	// Tested
	@Override
	public IQueryResult findNamedSimulations() throws SQLException {

		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.FIND_SIMULATIONS_KEY);
		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			throw new SQLException("No Simulations found");

		return new Neo4jResult(set);
	}

	@Override
	public boolean doesSimulationExist(String name) throws SQLException {

		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.FIND_SIMULATIONS_BY_NAME_KEY);
		query.setString(1, name);

		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			return false;
		set.next();
		try {
			return name.equals(set.getString("simulation"));
		} catch (NullPointerException e) {
			return false;
		}

	}

	// Tested
	@Override
	public boolean createOrMatchSimulationNode(String name) throws SQLException {

		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_SIMULATION_KEY);
		query.setString(1, name);

		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			return false;
		set.next();
		return name.equals(set.getString("simulation"));

	}

	// Tested
	@Override
	public boolean createOrMatchTemperatureRelationship(String name, int latitude, int longitude, long datetime,
			double temperature) throws SQLException {

		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_TEMP_KEY);
		query.setDouble(1, temperature);

		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			return false;

		set.next();

		query = conn.getPreparedStatement(Neo4jConstants.CREATE_TEMP_REL_KEY);
		query.setString(1, name);
		query.setInt(2, longitude);
		query.setInt(3, latitude);
		query.setLong(4, datetime);
		query.setDouble(5, temperature);

		set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			return false;
		set.next();

		boolean success = true;
		success &= name.equals(set.getString("simulation"));
		success &= latitude == set.getInt("latitude");
		success &= longitude == set.getInt("longitude");
		success &= datetime == set.getLong("dateTime");
		success &= temperature == set.getDouble("temperature");
		return success;
	}

	// Tested
	@Override
	public boolean createOrMatchAxisTiltRelationship(String name, float axisTilt) throws SQLException {

		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_AXIS_TILT_KEY);
		query.setFloat(1, axisTilt);

		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			return false;

		query = conn.getPreparedStatement(Neo4jConstants.CREATE_AXIS_REL_KEY);
		query.setString(1, name);
		query.setFloat(2, axisTilt);

		set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			return false;
		set.next();
		boolean success = true;
		success &= name.equals(set.getString("simulation"));
		success &= axisTilt == set.getFloat("axisTilt");
		return success;
	}

	// Tested
	@Override
	public boolean createOrMatchOrbitalEccentricityRelationship(String name, float orbitalEccentricity)
			throws SQLException {

		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_ORBITAL_ECCENTRICITY_KEY);
		query.setFloat(1, orbitalEccentricity);

		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			return false;

		query = conn.getPreparedStatement(Neo4jConstants.CREATE_ECCENTRICITY_REL_KEY);
		query.setString(1, name);
		query.setFloat(2, orbitalEccentricity);

		set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			return false;
		set.next();

		boolean success = true;
		success &= name.equals(set.getString("simulation"));
		success &= orbitalEccentricity == set.getFloat("orbitalEccentricity");
		return success;
	}

	// Tested
	@Override
	public boolean createOrMatchGridSpacingRelationship(String name, int gridSpacing) throws SQLException {

		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_GRID_SPACING_KEY);
		query.setInt(1, gridSpacing);

		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			return false;

		query = conn.getPreparedStatement(Neo4jConstants.CREATE_GRID_REL_KEY);
		query.setString(1, name);
		query.setInt(2, gridSpacing);

		set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			return false;
		set.next();

		boolean success = true;
		success &= name.equals(set.getString("simulation"));
		success &= gridSpacing == set.getInt("gridSpacing");
		return success;
	}

	// Tested
	@Override
	public boolean createOrMatchTimeStepRelationship(String name, int timeStep) throws SQLException {

		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_TIME_STEP_KEY);
		query.setInt(1, timeStep);

		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			return false;

		query = conn.getPreparedStatement(Neo4jConstants.CREATE_TIME_REL_KEY);
		query.setString(1, name);
		query.setInt(2, timeStep);

		set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			return false;
		set.next();

		boolean success = true;
		success &= name.equals(set.getString("simulation"));
		success &= timeStep == set.getInt("timeStep");
		return success;
	}

	// Tested
	@Override
	public boolean createOrMatchPresentationIntervalRelationship(String name, float presentationInterval)
			throws SQLException {

		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_PRESENTATION_INTERVAL_KEY);
		query.setFloat(1, presentationInterval);

		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			return false;

		query = conn.getPreparedStatement(Neo4jConstants.CREATE_PRESENTATION_REL_KEY);
		query.setString(1, name);
		query.setFloat(2, presentationInterval);

		set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			return false;
		set.next();

		boolean success = true;
		success &= name.equals(set.getString("simulation"));
		success &= presentationInterval == set.getFloat("presentationInterval");
		return success;
	}

	// Tested
	@Override
	public boolean createOrMatchSimulationLengthRelationship(String name, int simulationLength) throws SQLException {

		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_SIMULATION_LENGTH_KEY);
		query.setInt(1, simulationLength);

		ResultSet set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			return false;

		query = conn.getPreparedStatement(Neo4jConstants.CREATE_LENGTH_REL_KEY);
		query.setString(1, name);
		query.setInt(2, simulationLength);

		set = conn.query(query);
		if (!set.isBeforeFirst() || set == null)
			return false;
		set.next();

		boolean success = true;
		success &= name.equals(set.getString("simulation"));
		success &= simulationLength == set.getInt("simulationLength");
		return success;
	}

	// Tested
	@Override
	public IQueryResult setSimulationName(String name, int gridSpacing, int timeStep, int simulationLength,
			float presentationInterval, float axisTilt, float orbitalEccentricity) throws Exception {

		if (!createOrMatchSimulationNode(name))
			throw new SQLException("Failed to create Node(Simulation {" + name + "})");

		if (!doesSimulationExist(name))
			throw new SQLException("Failed to create Node(Simulation {" + name + "})");

		if (!createOrMatchGridSpacingRelationship(name, gridSpacing))
			throw new SQLException("Failed to create Relationship Node(Simulation {" + name
					+ "})-[:HAS_GRID]->Node(GridSpacing {" + gridSpacing + "})");

		if (!createOrMatchTimeStepRelationship(name, timeStep))
			throw new SQLException("Failed to create Relationship Node(Simulation {" + name
					+ "})-[:HAS_TIME]->Node(TimeStep {" + timeStep + "})");

		if (!createOrMatchSimulationLengthRelationship(name, simulationLength))
			throw new SQLException("Failed to create Relationship Node(Simulation {" + name
					+ "})-[:HAS_LENGTH]->Node(SimulationLength {" + simulationLength + "})");

		if (!createOrMatchPresentationIntervalRelationship(name, presentationInterval))
			throw new SQLException("Failed to create Relationship Node(Simulation {" + name
					+ "})-[:HAS_PRESENTATION]->Node(PresentationInterval {" + presentationInterval + "})");

		if (!createOrMatchAxisTiltRelationship(name, axisTilt))
			throw new SQLException("Failed to create Relationship Node(Simulation {" + name
					+ "})-[:HAS_AXIS]->Node(AxisTilt {" + axisTilt + "})");

		if (!createOrMatchOrbitalEccentricityRelationship(name, orbitalEccentricity))
			throw new SQLException("Failed to create Relationship Node(Simulation {" + name
					+ "})-[:HAS_ECCENTRICITY]->Node(OrbitalEccentricity {" + gridSpacing + "})");

		return new Neo4jResult(name, gridSpacing, timeStep, simulationLength, presentationInterval, axisTilt,
				orbitalEccentricity);
	}

	// Tested
	@Override
	public Future<IQueryResult> findSimulationByName(String name) throws SQLException {

		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.MATCH_NODE_BY_NAME_KEY);

		query.setString(1, name);

		return ThreadManager.getManager().submit(new Query(query));
	}

	// Tested
	@Override
	public Future<IQueryResult> findSimulationByData(int gridSpacing, int timeStep, int simulationLength,
			float presentationInterval, float axisTilt, float orbitalEccentricity) throws SQLException {

		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.MATCH_NODE_BY_DATA_KEY);

		query.setInt(1, gridSpacing);
		query.setInt(2, timeStep);
		query.setInt(3, simulationLength);
		query.setFloat(4, presentationInterval);
		query.setFloat(5, axisTilt);
		query.setFloat(6, orbitalEccentricity);

		return ThreadManager.getManager().submit(new Query(query));
	}

	@Override
	public void findTemperaturesAt(String name, long startDateTime, long endDateTime, int westLongitude,
			int eastLongitude, int northLatitude, int southLatitude) throws SQLException, InterruptedException,
			ExecutionException {

		/*
		 * If so, we then need to derive the end datetime from the simulation
		 * length (which is in months. we also have the start date by default
		 * (see ControlGui)). Using your engine, if data exists for that range,
		 * we need to error out and tell them so. Otherwise, we need to run a
		 * simulation from the last recorded datetime (if there is one) to the
		 * end date. This you can achieve by calling a findTemperatureAt with
		 * the endDate
		 */

		// First, find the closest datetime-valued relationship
		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.GET_DATE_TIME_KEY);
		query.setString(1, name);
		query.setLong(2, startDateTime);

		ResultSet result = conn.query(query);
		if (!result.isBeforeFirst() || result == null)
			throw new SQLException("No datetime found. Query was empty.");

		while (result.next())
			System.out.println(result);

		if (!"dateTime".equals(result.getMetaData().getColumnName(1)))
			throw new SQLException("Failed to find any datetimes on or before query datetime");

		// This should be the closest date available to us
		long foundDateTime;
		try {
			foundDateTime = result.getLong("dateTime");
		} catch (NullPointerException e) {
			throw new SQLException("Failed to find any datetimes on or before query datetime");
		}

		ResultMessage msg;

		// Populate ResultMessage with the range of grids if the startDateTime
		// was found
		// Otherwise; send only one grid to be simulated on
		if (foundDateTime == startDateTime) {

			msg = extractRange(name, startDateTime, endDateTime, foundDateTime, southLatitude, northLatitude,
					westLongitude, eastLongitude);

		} else {

			msg = extractPoint(name, foundDateTime, southLatitude, northLatitude, westLongitude, eastLongitude);
		}

		Publisher.getInstance().send(msg);
	}

	private ResultMessage extractRange(String name, long startDateTime, long endDateTime, long foundDateTime,
			int southLatitude, int northLatitude, int westLongitude, int eastLongitude) throws SQLException {

		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.GET_GRID_BY_DATE_TIME_RANGE_KEY);
		query.setString(1, name);
		query.setLong(2, startDateTime);
		query.setLong(3, endDateTime);

		ResultSet result = conn.query(query);
		if (!result.isBeforeFirst() || result == null)
			throw new SQLException("Failed to find any temperatures within the start and end date times");

		ResultMessage msg = new ResultMessage(southLatitude, northLatitude, westLongitude, eastLongitude, false);

		ResultMessage add;
		Hashtable<Long, ResultMessage> tables = new Hashtable<Long, ResultMessage>();
		try {
			while (result.next()) {

				long dateTime = result.getLong("dateTime");
				if (tables.containsKey(dateTime)) {
					add = tables.get(dateTime);
					;
				} else {
					add = new ResultMessage(southLatitude, northLatitude, westLongitude, eastLongitude, false);
				}

				add.setTemperature(result.getInt("longitude"), result.getInt("latitude"),
						result.getDouble("temperature"));
				tables.put(dateTime, add);
			}

			msg.setTables(tables);
		} catch (NullPointerException e) {
			// No end date was found
			return extractPoint(name, foundDateTime, southLatitude, northLatitude, westLongitude, eastLongitude);
		}

		return msg;
	}

	private ResultMessage extractPoint(String name, long foundDateTime, int southLatitude, int northLatitude,
			int westLongitude, int eastLongitude) throws SQLException {

		// Now get all the temps
		PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.GET_GRID_BY_DATE_TIME_KEY);
		query.setString(1, name);
		query.setLong(2, foundDateTime);

		ResultSet result = conn.query(query);
		if (!result.isBeforeFirst() || result == null)
			throw new SQLException("Failed to find temperatures for before the start date time");

		ResultMessage msg = new ResultMessage(southLatitude, northLatitude, westLongitude, eastLongitude, true);

		while (result.next()) {
			msg.setTemperature(result.getInt("longitude"), result.getInt("latitude"), result.getDouble("temperature"));
		}

		return msg;
	}

	@Override
	public void onMessage(Message msg) {

		if (msg instanceof PersistMessage) {
			try {
				this.msgQueue.add(msg);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Unable to add Grid to Database");
				Publisher.getInstance().send(new StopMessage());
			}
		} else if (msg instanceof PauseMessage) {
			this.pause();
		} else if (msg instanceof ResumeMessage) {
			this.resume();
		}
	}

	@Override
	public void run() {

		while (!Thread.currentThread().isInterrupted() && !stopped.get()) {

			// TODO try to use wait/notify
			if (!paused.get() && !msgQueue.isEmpty()) {
				performAction(msgQueue.poll());
			}
		}
	}

	@Override
	protected void performAction(Message inMsg) {

		/*
		 * TODO It would be nice to thread these persists, but we encounter too
		 * many lock errors. So unfortunately, we have to run this in the
		 * current thread
		 */
		// ThreadManager.getManager().execute(new Persist((PersistMessage)
		// inMsg));

		new Persist((PersistMessage) inMsg).run();
	}

	private class Persist implements Runnable {

		private final PersistMessage	msg;

		public Persist(PersistMessage msg) {

			if (msg == null)
				throw new IllegalArgumentException("Invalid PersistMessage provided");

			this.msg = msg;
		}

		@Override
		public void run() {

			long dateTime = msg.getDateTime();
			String name = msg.getSimulationName();

			try {

				int longitude, latitude;
				double temperature;

				int count = 0;

				for (Integer[] coords : msg.genCoordinates()) {

					System.out.println(++count);

					longitude = coords[0];
					latitude = coords[1];

					temperature = msg.getTemperature(longitude, latitude);

					this.createTemp(temperature);

					this.tempRelQuery(name, temperature, longitude, latitude, dateTime);

					ResultSet validate = conn
							.query("MATCH (n:Simulation)-[r:HAS_TEMP]->(t:Temperature) WHERE n.name = \"" + name
									+ "\" AND r.datetime = " + dateTime + " AND t.value = " + temperature
									+ " RETURN n.name, r.longitude, r.latitude, r.datetime, t.value");
					while (validate.next())
						System.out.println(validate);

					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(dateTime);
					System.out.println(c.getTime() + ": Saving temp " + temperature + " at longitude " + longitude
							+ ", latitude: " + latitude);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Failed to process PersistMessage: " + e);
			}
		}

		private void createTemp(double temperature) throws SQLException {

			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				System.out.println("Block interupted.");
			}

			PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_TEMP_KEY);
			query.setDouble(1, temperature);

			ResultSet result = conn.query(query);
			if (!result.isBeforeFirst() || result == null) {
				// close(result, query);
				throw new SQLException("Unable to create or match temperature. Temperature Node " + temperature
						+ " does not exist.");
			}

			result.next();
			// System.out.println("Result from CREATE_TEMP_KEY: " + result);

			try {
				if (temperature != result.getDouble("temperature"))
					throw new SQLException("Failed to creat or find a node for temperature " + temperature);
			} catch (Exception e) {
				e.printStackTrace();
				throw new SQLException("Unable to retrieve temperature. Temperature Node " + temperature
						+ " does not exist.");
			}
		}

		private void tempRelQuery(String name, double temperature, int longitude, int latitude, long dateTime)
				throws SQLException {

			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				System.out.println("Block interupted.");
			}

			PreparedStatement query = conn.getPreparedStatement(Neo4jConstants.CREATE_TEMP_REL_KEY);
			query.setString(1, name);
			query.setDouble(2, temperature);
			query.setInt(3, longitude);
			query.setInt(4, latitude);
			query.setLong(5, dateTime);

			ResultSet result = conn.query(query);
			if (!result.isBeforeFirst() || result == null) {
				// close(result, query);
				throw new SQLException("Failed to execute query. Temperature Relationship does not exist");
			}

			result.next();
			// System.out.println("Result from CREATE_TEMP_REL_KEY: " + result);

			try {
				if (result.getDouble("temperature") != temperature)
					throw new SQLException("Persisted temperature does not match provided temperature");
			} catch (Exception e) {
				e.printStackTrace();
				throw new SQLException("Unable to persist create Simulation -> Temperature relationship");
			}
		}
	}

	private class Query implements Callable<IQueryResult> {

		private final PreparedStatement	query;

		public Query(PreparedStatement query) {

			if (query == null)
				throw new IllegalArgumentException("Invalid PreparedStatement provided");

			this.query = query;
		}

		@Override
		public IQueryResult call() throws Exception {

			try {

				IQueryResult r = new Neo4jResult(conn.query(query));
				return r;
			} catch (SQLException e) {
				return new Neo4jResult(e);
			}
		}
	}
}
