package tests;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import db.IDBConnection;
import db.Neo4jConstants;
import db.SimulationDAO;
import db.SimulationNeo4j;

public class TestSeverStartup {
	
	private static IDBConnection db;
	private static SimulationDAO dao;

	@Test
	public void aTestDbStart() {
		try {
			db = new SimulationNeo4j();
			assertNotNull(db);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			dao = new SimulationDAO(db);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testCreateSimulationNodes() {
		
		boolean ret = false;
		try {
			ret = dao.createSimulationNode("test1");
			assertTrue(ret);
			
			ret = dao.createSimulationNode("test2");
			assertTrue(ret);
			
			ret = dao.createSimulationNode("test3");
			assertTrue(ret);
			
			ret = dao.createSimulationNode("test4");
			assertTrue(ret);
			
			ret = dao.createSimulationNode("test5");
			assertTrue(ret);
			
			ret = dao.createSimulationNode("test6");
			assertTrue(ret);
			
			ret = dao.createSimulationNode("test7");
			assertTrue(ret);
			
			ret = dao.createSimulationNode("test8");
			assertTrue(ret);
			
			ret = dao.createSimulationNode("test9");
			assertTrue(ret);
			
			ret = dao.createSimulationNode("test10");
			assertTrue(ret);
			
			ResultSet result = dao.findNamedSimulations();
			if (!result.isBeforeFirst())
				fail("ResultSet was empty - there should be 10 names");
			
			while(result.next()) {
				System.out.println(result);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testCreateAxisTiltNode() {
		
		try {
			boolean ret = dao.createAxisTiltRelationship("test1", 0.4f);
			assertTrue(ret);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
