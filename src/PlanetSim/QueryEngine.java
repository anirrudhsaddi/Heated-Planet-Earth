package PlanetSim;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JList;

import org.apache.james.mime4j.field.datetime.DateTime;

import db.IDBConnection;
import db.IQueryResult;
import db.SimulationDAO;
import db.SimulationNeo4j;
import PlanetSim.widgets.QueryWidget;

public class QueryEngine {

	private IDBConnection conn;
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

	private final SimulationDAO simDAO;				
	
	private String		simName;
	private float		axialTilt;
	private float		eccentricity;
	private DateTime	startTime;
	private DateTime	endTime;
	private double		wLat;
	private double		eLat;
	private double		sLat;
	private double		nLat;

	public QueryEngine() throws SQLException{
		simDAO = new SimulationDAO(new SimulationNeo4j());
		
	}
	
	public JList<?> getSimulationList(){
		JList simList =  new JList();
		
		
		IQueryResult rs =  null;
		try {
			rs = simDAO.findNamedSimulations();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		simList = (JList) rs.getSimulationName();
		return simList;
		
	}

	public void setConn(IDBConnection conn) {
		this.conn = conn;
	}

	public void setSimName(String simName) {
		this.simName = simName;
	}

	public void setAxialTilt(float axialTilt) {
		this.axialTilt = axialTilt;
	}

	public void setEccentricity(float eccentricity) {
		this.eccentricity = eccentricity;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	public void setwLat(double wLat) {
		this.wLat = wLat;
	}

	public void seteLat(double eLat) {
		this.eLat = eLat;
	}

	public void setsLat(double sLat) {
		this.sLat = sLat;
	}

	public void setnLat(double nLat) {
		this.nLat = nLat;
	}

	// TODO: validate input to make sure that are valid

}
