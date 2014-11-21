package tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import db.IDBConnection;
import db.IQueryResult;
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
				System.out.println(result.getString("simulation"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testCreateAxisTiltRelationship() {
		
		try {
			assertTrue(dao.createAxisTiltRelationship("test1", 0.4f));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testCreateTimeStepRelationship() {
		
		try {
			assertTrue(dao.createTimeStepRelationship("test1", 1));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testCreateOrbitalEccentricityRelationship() {
		
		try {
			assertTrue(dao.createOrbitalEccentricityRelationship("test1", 0.67f));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testCreateGridSpacingRelationship() {
		
		try {
			assertTrue(dao.createGridSpacingRelationship("test1", 15));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testCreatePresentationIntervalRelationship() {
		
		try {
			assertTrue(dao.createPresentationIntervalRelationship("test1", 2));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testCreateSimulationLengthRelationship() {
		
		try {
			assertTrue(dao.createSimulationLengthRelationship("test1", 1200));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testCreateTemperatureRelationship() {
		
		try {
			assertTrue(dao.createTemperatureRelationship("test1", 0, 0, 0f, 288));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testSetSimulationName() {
		
		try {
			IQueryResult result = dao.setSimulationName("kungfu panda", 15, 30, 1200, 1f, 0.67f, 0.23f);
			System.out.println(result);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
