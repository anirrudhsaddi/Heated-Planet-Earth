package db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.Callable;
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
	
	private final static String MATCH_NODE_BY_NAME_KEY = "match_node_name";
	private final static String MATCH_NODE_BY_NAME_QUERY = "MATCH "
			+ "(n:Simulation)-[:HAS_PRESENTATION|:HAS_TIME|:HAS_GRID|:HAS_ECCENTRICITY:|HAS_AXIS]->(o)"
			+ "WHERE n.name = ?"
			+ "RETURN { "
			+ "name: n.name, "
			+ "result: filter(x IN o.values WHERE x = ? OR x = ? OR x = ? OR x = ? OR x = ? )"
			+ "}";
	
	private final static String MATCH_NODE_BY_DATA_KEY = "match_node_values";
	private final static String MATCH_NODE_BY_DATA_QUERY = "MATCH "
			+ "(n:Simulation)-[:HAS_PRESENTATION|:HAS_TIME|:HAS_GRID|:HAS_ECCENTRICITY:|HAS_AXIS]->(o)"
			+ "RETURN { "
			+ "name: n.name, "
			+ "result: filter(x IN o.values WHERE x = ? OR x = ? OR x = ? OR x = ? OR x = ? )"
			+ "}";
	
	// TODO should we get the entire grid? or just a specific area?
	private final static String GET_GRID_BY_DATE_TIME_KEY = "match_area_by_date";
	private final static String GET_GRID_BY_DATE_TIME_QUERY = "MATCH "
			+ "RETURN {}";
	
	public SimulationDAO(final IDBConnection conn) throws SQLException {
		
		if (simulationName == null)
			throw new IllegalArgumentException("Invalid Simulation Name provided");
		
		if (conn == null)
			throw new IllegalArgumentException("Invalid DB Connection object");
		
		this.conn = conn;
		
		this.conn.createPreparedStatement(MATCH_NODE_BY_NAME_KEY, MATCH_NODE_BY_NAME_QUERY);
		this.conn.createPreparedStatement(MATCH_NODE_BY_DATA_KEY, MATCH_NODE_BY_DATA_QUERY);
		this.conn.createPreparedStatement(GET_GRID_BY_DATE_TIME_KEY, GET_GRID_BY_DATE_TIME_QUERY);
		
		Publisher.getInstance().subscribe(DeliverMessage.class, this);
	}
	
	// TODO initial create of node?
	public void setSimulationName(String simulationName) {
		
		if (simulationName == null)
			throw new IllegalArgumentException("Invalid Simulation Name provided");
		
		this.simulationName = simulationName;
	}
	
	@Override
	public Future<IQueryResult> findSimulationByName(String name, int gridSpacing, int timeStep, int simulationLength, float presentatinoInterval, float axisTilt, float eccentricity) {
		
		PreparedStatement query = conn.getPreparedStatement(MATCH_NODE_BY_NAME_KEY);
				
		return ThreadManager.getManager().submit(new Query(query));
	}

	@Override
	public Future<IQueryResult> findSimulationByData(int gridSpacing, int timeStep, int simulationLength, float presentatinoInterval, float axisTilt, float eccentricity) {
		
		PreparedStatement query = conn.getPreparedStatement(MATCH_NODE_BY_DATA_KEY);
		
		return ThreadManager.getManager().submit(new Query(query));
	}

	@Override
	public Future<IQueryResult> findTemperaturesAt(String name, Calendar datetime, int[] locations) {
		
		PreparedStatement query = conn.getPreparedStatement(GET_GRID_BY_DATE_TIME_KEY);
		
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
			System.err.printf("WARNING: No processor specified in class %s for message %s\n", this.getClass().getName(), msg.getClass().getName());
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
				throw new IllegalArgumentException("Invalid PreparedStatement provided");
			
			this.query = query;
		}

		@Override
		public IQueryResult call() throws Exception {

			try {
				return new Neo4jSimulationDataResult(conn.query(query));
			} catch (SQLException e) {
				return new Neo4jSimulationDataResult(e);
			}
		}
	}
}
