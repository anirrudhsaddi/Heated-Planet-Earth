package db;

import java.sql.ResultSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import common.ThreadManager;

public class SimulationDAO implements ISimDAO, Callable<IQueryResult> {
	
	private final IDBConnection conn;
	
	public SimulationDAO(final IDBConnection conn) {
		
		if (conn == null)
			throw new IllegalArgumentException("Invalid DB Connection object");
		
		this.conn = conn;
	}
	
	// TODO figure this out
	public Future<IQueryResult> findNode(String name) {
		
		
		return ThreadManager.getManager().submit(this);
	}

	@Override
	public IQueryResult call() {
		
		ResultSet result = conn.query(queryName, args);
		
		// TODO get values from result set
		return new Neo4jQueryResult();
		
	}
}
