package db;

import java.sql.ResultSet;
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
		
		// populate from result. If result has stuff...
		if (!result.isBeforeFirst())
			return;
		
		result.first();
		while(result.next()) {
			
			this.queryName.add(result.getString("o.name"));
			
			this.gridSpacing.add(result.getInt("o.GridSpacing.value"));
			this.timeStep.add(result.getInt("o.TimeStep.value"));
			this.simulationLength.add(result.getInt("o.SimulationLength.value"));
			
			this.presentationInterval.add(result.getFloat("o.PresentationInterval.value"));
			this.axisTilt.add(result.getFloat("o.AxislTilt.value"));
			this.eccentricity.add(result.getFloat("o.OrbitalEccentricity.value"));
		}
		
		populated = true;
	}
	
	public Neo4jResult(final Exception result) {
		
		if (result == null)
			throw new IllegalArgumentException("Invalid Exception provided");
		
		this.error = result;
		populated = true;
	}

	@Override
	public Iterator<String> getQueryName() {
		return queryName.iterator();
	}

	@Override
	public Iterator<Integer> getGridSpacing() {
		return gridSpacing.iterator();
	}

	@Override
	public Iterator<Integer> getTimeStep() {
		return timeStep.iterator();
	}

	@Override
	public Iterator<Integer> getSimulationLength() {
		return simulationLength.iterator();
	}

	@Override
	public Iterator<Float> getPresentationInterval() {
		return presentationInterval.iterator();
	}

	@Override
	public Iterator<Float> getAxisTilt() {
		return axisTilt.iterator();
	}

	@Override
	public Iterator<Float> getEccentricity() {
		return eccentricity.iterator();
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
