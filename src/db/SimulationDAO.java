package db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import messaging.Message;
import messaging.MessageListener;
import messaging.events.DeliverMessage;
import common.ComponentBase;
import common.IGrid;
import common.ThreadManager;

public class SimulationDAO extends ComponentBase implements ISimDAO {
	
	private final IDBConnection conn;
	
	private final static String MATCH_NODE_BY_NAME = "";
	private final static String MATCH_NODE_BY_DATA = "";
	
	// TODO should we get the entire grid? or just a specific area?
	private final static String GET_GRID_BY_DATE_TIME = "";
	
	private PreparedStatement query;
	
	public SimulationDAO(final IDBConnection conn) {
		
		if (conn == null)
			throw new IllegalArgumentException("Invalid DB Connection object");
		
		this.conn = conn;
	}
	
	// TODO initial create of node?
	
	// TODO Define protocols to call from Query GUI Engine that:
	// 1. accepts the parameters for the query
	// 2. the query name
	// 3. using the query name, calls conn.getPreparedStatement
	// 4. populates the returned stmt
	// 5. set that stmt to this.query
	

	@Override
	public void run() {
		
		while (!Thread.currentThread().isInterrupted() && !stopped.get()) {
			// just loop
		}
		
		try {
			return new Neo4jSimulationDataResult(conn.query(query));
		} catch (SQLException e) {
			return new Neo4jSimulationDataResult(e);
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
}
