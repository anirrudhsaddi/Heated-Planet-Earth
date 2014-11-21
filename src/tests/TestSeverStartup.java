package tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import util.TestConnection;
import db.IQueryResult;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSeverStartup {
	
	private static TestConnection t;

	@Test
	public void aTestDbStart() {

		try {
			t = new TestConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void bTestCreateSimulationNodes() {
		
		boolean ret = false;
		try {
			ret = t.dao.createOrMatchSimulationNode("test1");
			assertTrue(ret);
			
			ret = t.dao.createOrMatchSimulationNode("test2");
			assertTrue(ret);
			
			ret = t.dao.createOrMatchSimulationNode("test3");
			assertTrue(ret);
			
			ret = t.dao.createOrMatchSimulationNode("test4");
			assertTrue(ret);
			
			ret = t.dao.createOrMatchSimulationNode("test5");
			assertTrue(ret);
			
			ret = t.dao.createOrMatchSimulationNode("test6");
			assertTrue(ret);
			
			ret = t.dao.createOrMatchSimulationNode("test7");
			assertTrue(ret);
			
			ret = t.dao.createOrMatchSimulationNode("test8");
			assertTrue(ret);
			
			ret = t.dao.createOrMatchSimulationNode("test9");
			assertTrue(ret);
			
			ret = t.dao.createOrMatchSimulationNode("test10");
			assertTrue(ret);
			
			ResultSet result = t.dao.findNamedSimulations();
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
			assertTrue(t.dao.createOrMatchAxisTiltRelationship("test1", 0.4f));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void dTestCreateTimeStepRelationship() {
		
		try {
			assertTrue(t.dao.createOrMatchTimeStepRelationship("test1", 1));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void eTestCreateOrbitalEccentricityRelationship() {
		
		try {
			assertTrue(t.dao.createOrMatchOrbitalEccentricityRelationship("test1", 0.67f));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void fTestCreateGridSpacingRelationship() {
		
		try {
			assertTrue(t.dao.createOrMatchGridSpacingRelationship("test1", 15));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void gTestCreatePresentationIntervalRelationship() {
		
		try {
			assertTrue(t.dao.createOrMatchPresentationIntervalRelationship("test1", 2));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void hTestCreateSimulationLengthRelationship() {
		
		try {
			assertTrue(t.dao.createOrMatchSimulationLengthRelationship("test1", 1200));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void iTestCreateTemperatureRelationship() {
		
		try {
			assertTrue(t.dao.createOrMatchTemperatureRelationship("test1", 0, 0, 0f, 288));
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void jTestSetSimulationName() {
		
		try {
			IQueryResult result = t.dao.setSimulationName("kungfu panda", 15, 30, 1200, 1f, 0.67f, 0.23f);
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
