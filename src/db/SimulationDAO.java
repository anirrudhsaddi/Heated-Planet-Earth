package db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import messaging.Message;
import messaging.events.DeliverMessage;
import common.ComponentBase;
import common.IGrid;
import common.ThreadManager;

public class SimulationDAO extends ComponentBase implements ISimDAO {
	
	private final String simulationName;
	private final IDBConnection conn;
	
	private final static String MATCH_NODE_BY_NAME_KEY = "";
	private final static String MATCH_NODE_BY_NAME_QUERY = "";
	
	private final static String MATCH_NODE_BY_DATA_KEY = "";
	private final static String MATCH_NODE_BY_DATA_QUERY = "";
	
	// TODO should we get the entire grid? or just a specific area?
	private final static String GET_GRID_BY_DATE_TIME_KEY = "";
	private final static String GET_GRID_BY_DATE_TIME_QUERY = "";
	
	public SimulationDAO(final String simulationName, final IDBConnection conn) throws SQLException {
		
		if (simulationName == null)
			throw new IllegalArgumentException("Invalid Simulation Name provided");
		
		if (conn == null)
			throw new IllegalArgumentException("Invalid DB Connection object");
		
		this.simulationName = simulationName;
		this.conn = conn;
		
		this.conn.createPreparedStatement(MATCH_NODE_BY_NAME_KEY, MATCH_NODE_BY_NAME_QUERY);
		this.conn.createPreparedStatement(MATCH_NODE_BY_DATA_KEY, MATCH_NODE_BY_DATA_QUERY);
		this.conn.createPreparedStatement(GET_GRID_BY_DATE_TIME_KEY, GET_GRID_BY_DATE_TIME_QUERY);
	}
	
	// TODO initial create of node?
	
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
