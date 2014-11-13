package db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import common.ThreadManager;

public class SimulationDAO implements ISimDAO, Callable<IQueryResult> {
	
	private final IDBConnection conn;
	
	private PreparedStatement query;
	
	public SimulationDAO(final IDBConnection conn) {
		
		if (conn == null)
			throw new IllegalArgumentException("Invalid DB Connection object");
		
		this.conn = conn;
	}
	
	// TODO Define protocols to call from Query GUI Engine that:
	// 1. accepts the parameters for the query
	// 2. the query name
	// 3. using the query name, calls conn.getPreparedStatement
	// 4. populates the returned stmt
	// 5. set that stmt to this.query
	// 6. call triggerQuery
	
	// TODO we can make this a pluggable inner class to handle String Queries and batch queries
	private Future<IQueryResult> triggerQuery() {
		return ThreadManager.getManager().submit(this);
	}

	@Override
	public IQueryResult call() {
		
		try {
			return new Neo4jQueryResult(conn.query(query));
		} catch (SQLException e) {
			return new Neo4jQueryResult(e);
		}
	}
}
