package tests;

import static org.junit.Assert.fail;

import java.io.File;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import messaging.Message;
import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.PersistMessage;
import messaging.events.ResultMessage;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import util.TestConnection;
import db.IQueryResult;

public class TestEarthAccess {

	private static TestConnection t;

	@BeforeClass
	public static void initTestResources() {

		try {
			t = new TestConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}

		try {

			IQueryResult result = t.dao.setSimulationName("kungfu panda", 15, 30, 1200, 1f, 0.67f, 0.23f);
			
			System.out.println("names: " + result.getSimulationName());
			System.out.println("length: " + result.getSimulationLength());
			System.out.println("timestep: " + result.getTimeStep());
			System.out.println("axistilt: " + result.getAxisTilt());
			System.out.println("orbitaleccentricity: " + result.getOrbitalEccentricity());
			System.out.println("gridspacing: " + result.getGridSpacing());
			System.out.println("presentation: " + result.getPresentationInterval());
			System.out.println("list: " + result.getQueryList());
			
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		} catch (Exception e) {
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
				System.out.println(listed[i]);
				File f = new File(file, listed[i]);
				f.delete();
			}
		}
	}

	@Test
	public void aTestOffer() {

		new ResultMessageCatcher();
		Calendar c = Calendar.getInstance();
		PersistMessage msg = new PersistMessage("kungfu panda", c.getTimeInMillis());

		for (int longitude = 0; longitude < 360; longitude += 60) {
			for (int latitude = 0; latitude < 180; latitude += 60) {
				msg.setTemperature(longitude, latitude, 288);
			}
		}

		try {
			Publisher.getInstance().send(msg);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}

		try {

			t.dao.findTemperaturesAt("kungfu panda", c, 8, 5, 8, 5);

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
