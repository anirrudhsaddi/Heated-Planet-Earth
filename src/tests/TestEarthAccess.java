package tests;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Test;

import util.TestConnection;
import db.IQueryResult;

	public class TestEarthAccess {
	
	private static TestConnection t;
	
	@BeforeClass
	public static void initTestResources() {
		
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
	public void test() {
		fail("Not yet implemented");
	}

}
