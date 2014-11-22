package tests;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import util.TestConnection;
import db.IQueryResult;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestClientAccess {
	
	private static TestConnection t;
	
	@BeforeClass
	public static void initTestResources() {
		
		try {
			t = new TestConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		} 
	}
	
	@Test
	public void aTestSetSimulationName() {
		
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
	
	@Test
	public void bTestGetAllNames() {
		
		try {
		
			ResultSet result = t.dao.findNamedSimulations();
			if (!result.isBeforeFirst())
				fail("ResultSet was empty");
			
			while(result.next()) {
				System.out.println(result.getString("simulation"));
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void cTestFindSimulationByName() {
		
		try {
			
			IQueryResult result = t.dao.findSimulationByName("kungfu panda").get();
			assertEquals(result.getSimulationName().get(0), "kungfu panda");
			
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.toString());
		} catch (ExecutionException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void dTestFindSimulationByData() {
		
		try {
			
			IQueryResult result = t.dao.findSimulationByData(15, 30, 1200, 1f, 0.67f, 0.23f).get();
			assertEquals(result.getSimulationName().get(0), "kungfu panda");
			assertEquals((int) result.getGridSpacing().get(0), 15);
			assertEquals((int) result.getTimeStep().get(0), 30);
			assertEquals((int) result.getSimulationLength().get(0), 1200);
			assertEquals((float) result.getPresentationInterval().get(0), 1f, 0);
			assertEquals((float) result.getAxisTilt().get(0), 0.67f, 0);
			assertEquals((float) result.getOrbitalEccentricity().get(0), 0.23f, 0);
			
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.toString());
		} catch (ExecutionException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
