package tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.Test;

import db.IDBConnection;
import db.IQueryResult;
import db.SimulationDAO;
import db.SimulationNeo4j;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
	public void bTestCreateSimulationNodes() {
		
		boolean ret = false;
		try {
			ret = dao.createOrMatchSimulationNode("test1");
			assertTrue(ret);
			
			ret = dao.createOrMatchSimulationNode("test2");
			assertTrue(ret);
			
			ret = dao.createOrMatchSimulationNode("test3");
			assertTrue(ret);
			
			ret = dao.createOrMatchSimulationNode("test4");
			assertTrue(ret);
			
			ret = dao.createOrMatchSimulationNode("test5");
			assertTrue(ret);
			
			ret = dao.createOrMatchSimulationNode("test6");
			assertTrue(ret);
			
			ret = dao.createOrMatchSimulationNode("test7");
			assertTrue(ret);
			
			ret = dao.createOrMatchSimulationNode("test8");
			assertTrue(ret);
			
			ret = dao.createOrMatchSimulationNode("test9");
			assertTrue(ret);
			
			ret = dao.createOrMatchSimulationNode("test10");
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
	public void cTestCreateAxisTiltRelationship() {
		
		try {
			assertTrue(dao.createOrMatchAxisTiltRelationship("test1", 0.4f));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void dTestCreateTimeStepRelationship() {
		
		try {
			assertTrue(dao.createOrMatchTimeStepRelationship("test1", 1));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void eTestCreateOrbitalEccentricityRelationship() {
		
		try {
			assertTrue(dao.createOrMatchOrbitalEccentricityRelationship("test1", 0.67f));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void fTestCreateGridSpacingRelationship() {
		
		try {
			assertTrue(dao.createOrMatchGridSpacingRelationship("test1", 15));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void gTestCreatePresentationIntervalRelationship() {
		
		try {
			assertTrue(dao.createOrMatchPresentationIntervalRelationship("test1", 2));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void hTestCreateSimulationLengthRelationship() {
		
		try {
			assertTrue(dao.createOrMatchSimulationLengthRelationship("test1", 1200));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void iTestCreateTemperatureRelationship() {
		
		try {
			assertTrue(dao.createOrMatchTemperatureRelationship("test1", 0, 0, 0f, 288));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void jTestSetSimulationName() {
		
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
