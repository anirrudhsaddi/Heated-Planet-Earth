package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import db.SimulationNeo4j;

public class TestSeverStartup {

	@Test
	public void test() {
		try {
			SimulationNeo4j db = new SimulationNeo4j();
			assertNotNull(db);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
