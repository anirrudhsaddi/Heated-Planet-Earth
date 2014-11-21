package util;

import java.sql.SQLException;

import db.IDBConnection;
import db.SimulationDAO;
import db.SimulationNeo4j;

public class TestConnection {
	
	public SimulationDAO dao;
	
	public TestConnection() throws SQLException {
		
		dao = new SimulationDAO(new SimulationNeo4j());
	}
}
