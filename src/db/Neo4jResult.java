package db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Neo4jResult implements IQueryResult {
	
	private List<String> queryName = new LinkedList<String>();
	private List<Integer> gridSpacing = new LinkedList<Integer>();
	private List<Integer>timeStep = new LinkedList<Integer>();
	private List<Integer> simulationLength = new LinkedList<Integer>();
	private List<Float> presentationInterval = new LinkedList<Float>();
	private List<Float> axisTilt = new LinkedList<Float>();
	private List<Float> eccentricity = new LinkedList<Float>();
	private Exception error;
	
	private boolean populated = false;
	
	public Neo4jResult(String queryName, int gridSpacing, int timeStep, int simulationLength, float presentationInterval, float axisTilt, float eccentricity) {
		
		this.queryName.add(queryName);
		this.gridSpacing.add(gridSpacing);
		this.timeStep.add(timeStep);
		this.simulationLength.add(simulationLength);
		this.presentationInterval.add(presentationInterval);
		this.axisTilt.add(axisTilt);
		this.eccentricity.add(eccentricity);
		
		populated = true;
	}
	
	public Neo4jResult(final ResultSet result) throws SQLException {
		
		// populate from result
		if (!result.isBeforeFirst() || result == null)
			return;
		
		result.next();
		System.out.println(result);
		while(result.next()) {
			
			this.queryName.add(checkColumnExists(result, "simulation") ? result.getString("simulation") : "");
			this.gridSpacing.add(checkColumnExists(result, "gridSpacing") ? result.getInt("gridSpacing") : -1);
			this.timeStep.add(checkColumnExists(result, "timeStep") ? result.getInt("timeStep") : -1);
			this.simulationLength.add(checkColumnExists(result, "simulationLength") ? result.getInt("simulationLength") : -1);
			this.presentationInterval.add(checkColumnExists(result, "presentationInterval") ? result.getFloat("presentationInterval") : -1);
			this.axisTilt.add(checkColumnExists(result, "axislTilt") ? result.getFloat("axislTilt") : -1);
			this.eccentricity.add(checkColumnExists(result, "orbitalEccentricity") ? result.getFloat("orbitalEccentricity") : -1);
		}
		
		populated = true;
	}
	
	private boolean checkColumnExists(ResultSet rs, String label) throws SQLException {
		
		ResultSetMetaData rsmd = rs.getMetaData();
	    int columns = rsmd.getColumnCount();
	    for (int x = 1; x <= columns; x++) {
	    	if (label.equals(rsmd.getColumnName(x)))
	    		return true;
	    }
	    
	    return false;
	}
	
	public Neo4jResult(final Exception result) {
		
		if (result == null)
			throw new IllegalArgumentException("Invalid Exception provided");
		
		this.error = result;
		populated = true;
	}

	@Override
	public List<String> getSimulationName() {
		return queryName;
	}

	@Override
	public List<Integer> getGridSpacing() {
		return gridSpacing;
	}

	@Override
	public List<Integer> getTimeStep() {
		return timeStep;
	}

	@Override
	public List<Integer> getSimulationLength() {
		return simulationLength;
	}

	@Override
	public List<Float> getPresentationInterval() {
		return presentationInterval;
	}

	@Override
	public List<Float> getAxisTilt() {
		return axisTilt;
	}

	@Override
	public List<Float> getOrbitalEccentricity() {
		return eccentricity;
	}

	@Override
	public boolean isErrored() {
		return error != null;
	}

	@Override
	public Exception getError() {
		return error;
	}

	@Override
	public boolean isEmpty() {
		return populated;
	}

}
