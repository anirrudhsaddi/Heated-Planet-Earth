package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import messaging.Message;
import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.ResultMessage;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import util.TestConnection;
import db.IQueryResult;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestClientAccess {

	private static TestConnection	t;

	@BeforeClass
	public static void initTestResources() {

		try {
			t = new TestConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@AfterClass
	public static void cleanTestResources() {
		
		File file = new File("/neo4j/db/");
		String[] listed;
		
		if (file.isDirectory()) {
			listed = file.list();
			for (int i = 0; i < listed.length; i++) {
				File f = new File(file, listed[i]);
				f.delete();
			}
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

			while (result.next()) {
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

	@Test
	public void fTestFindTemperature() {

		Calendar c = Calendar.getInstance();
		new ResultMessageCatcher();

		try {
			assertTrue(t.dao.createOrMatchTemperatureRelationship("kungfu panda", 0, 0, c.getTimeInMillis(), 288));
			assertTrue(t.dao.createOrMatchTemperatureRelationship("kungfu panda", 1, 0, c.getTimeInMillis(), 288));
			assertTrue(t.dao.createOrMatchTemperatureRelationship("kungfu panda", 2, 0, c.getTimeInMillis(), 288));
			assertTrue(t.dao.createOrMatchTemperatureRelationship("kungfu panda", 0, 1, c.getTimeInMillis(), 288));
			assertTrue(t.dao.createOrMatchTemperatureRelationship("kungfu panda", 0, 2, c.getTimeInMillis(), 288));
			assertTrue(t.dao.createOrMatchTemperatureRelationship("kungfu panda", 1, 1, c.getTimeInMillis(), 288));
			assertTrue(t.dao.createOrMatchTemperatureRelationship("kungfu panda", 1, 2, c.getTimeInMillis(), 288));
			assertTrue(t.dao.createOrMatchTemperatureRelationship("kungfu panda", 2, 1, c.getTimeInMillis(), 288));
			assertTrue(t.dao.createOrMatchTemperatureRelationship("kungfu panda", 2, 2, c.getTimeInMillis(), 288));

		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}

		try {

			t.dao.findTemperaturesAt("kungfu panda", c, 2, 0, 2, 0);

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

	private class ResultMessageCatcher implements MessageListener {

		public ResultMessageCatcher() {
			Publisher.getInstance().subscribe(ResultMessage.class, this);
		}

		@Override
		public void onMessage(Message msg) {
			ResultMessage m = (ResultMessage) msg;
			System.out.println(m.needsCalculation());
			Iterator<Integer[]> coords = m.genCoordinates();
			while(coords.hasNext()) {
				Integer[] gend = coords.next();
				int longitude = gend[0];
				int latitude = gend[1];
				System.out.println(m.getTemperature(longitude, latitude));
			}
		}
	}
}
