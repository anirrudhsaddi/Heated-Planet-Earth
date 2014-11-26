package PlanetSim;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JList;

import org.apache.james.mime4j.field.datetime.DateTime;

import PlanetSim.widgets.QueryWidget;
import db.IDBConnection;
import db.IQueryResult;
import db.SimulationDAO;

public class QueryEngine {

	private IDBConnection		conn;
	// TODO Don't forget - we have to send a start message before calling
	// findTemperatesAt!

	/*
	 * Behavior: 1. Provide a method to retrieve all available simulation names
	 * from the db.
	 * 
	 * 2. IF we generate a guaranteed unique name, then we don't have to check
	 * simulation name on every normal simulation start (it still might be a
	 * good idea). // I am auto generating the simulation name now(Anirrudh).
	 * 
	 * 3. Check to see if a simulation exists based on physical data on a normal
	 * simulation run. If not, let the user know that they need to run a query.
	 * If it doesn't, call setSimulationName. //We are saving every simulation
	 * 
	 * 4. For a query, if the uses selects a simulation, call the db to get the
	 * physical data. If they query with physical data, get the name from the
	 * db. If an exception occurs or the result is empty, error out.
	 * 
	 * 4. When a user hits query, pass in the simulation name and the requested
	 * location/dates, along with the physical data, make sure that the
	 * StartMessage gets called, then pass it on to the db. If any errors occur,
	 * error out
	 */

	private final SimulationDAO	simDAO;

	public QueryEngine(SimulationDAO simDAO) throws SQLException {
		
		this.simDAO = simDAO;
	}
	
	public void setSimulationName(String simulationName, int gridSpacing, int timeStep, int simulationLength, float presentationInterval, float axisTilt, float orbitalEccentricity) throws Exception {
		
		IQueryResult result = simDAO.setSimulationName(simulationName, gridSpacing, timeStep, simulationLength, presentationInterval, axisTilt, orbitalEccentricity);
		
		if (result.isEmpty())
			throw new SQLException("No data was returned when setting the new Simulation: query failed");
		if (result.isErrored())
			throw result.getError();
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JList getSimulationList() throws SQLException {
		
		JList simList = new JList();
		
		IQueryResult rs = null;
		rs = simDAO.findNamedSimulations();
		
		java.util.List<String> names= rs.getSimulationName();
		simList.setListData(names.toArray());
		System.out.println("SimList size: " + simList.getModel().getSize() );
		for(int i=0;i<simList.getModel().getSize();){
			System.out.println("Element at "+ i +". " + simList.getModel().getElementAt(i));
			i++;
		}
		return simList;

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JList getSimulationsByData(int gridSpacing, int timeStep, int simulationLength, float presentationInterval, float axisTilt, float orbitalEccentricity) throws Exception {
		
		IQueryResult result = simDAO.findSimulationByData(gridSpacing, timeStep, simulationLength, presentationInterval, axisTilt, orbitalEccentricity).get();
		
		if (result.isEmpty())
			return new JList();
		if (result.isErrored())
			throw result.getError();
		
		return new JList(result.getSimulationName().toArray());
	}

	public Hashtable<String, String> getSimulationPhysicalParameters(String simulationName) throws Exception {
		
		IQueryResult result = simDAO.findSimulationByName(simulationName).get(); 
		
		if (result.isEmpty())
			return new Hashtable<String, String>();
		if (result.isErrored())
			throw result.getError();
		// HAS_PRESENTATION|:HAS_TIME|:HAS_GRID|:HAS_ECCENTRICITY|:HAS_AXIS|:HAS_LENGTH
		
		List<String> queryResult = result.getQueryList().get(0);
		Hashtable<String, String> ret = new Hashtable<String, String>();
		ret.put("Grid Spacing", String.valueOf(queryResult.get(2)));
		ret.put("Simulation Time Step", String.valueOf(queryResult.get(1)));
		ret.put("Simulation Length", String.valueOf(queryResult.get(5)));
		ret.put("Presentation Rate", String.valueOf(queryResult.get(0)));
		ret.put("Axis Tilt", String.valueOf(queryResult.get(4)));
		ret.put("Orbital Eccentricity", String.valueOf(queryResult.get(3)));
		return ret;
		
	}
	
	public void findTemperaturesAt(String name, long startDateTime, long endDateTime, int westLongitude,
			int eastLongitude, int northLatitude, int southLatitude) throws Exception {
		
		simDAO.findTemperaturesAt(name, startDateTime, endDateTime, westLongitude, eastLongitude, northLatitude, southLatitude);
	}
}
