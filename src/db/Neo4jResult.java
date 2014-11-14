package db;

import java.sql.ResultSet;

import common.IGrid;

public class Neo4jResult implements IQueryResult {
	
	private String queryName;
	private int gridSpacing, timeStep, simulationLength;
	private float presentationInterval, axisTilt, eccentricity;
	private IGrid grid;
	private Exception error;
	private boolean populated;
	
	public Neo4jResult(final ResultSet result) {
		
		populated = false;
		
		// populate from result. If result has stuff...
		
		populated = true;
		
	}
	
	public Neo4jResult(final Exception result) {
		
		if (result == null)
			throw new IllegalArgumentException("Invalid Exception provided");
		
		populated = false;
		
		this.error = result;
	}

	@Override
	public String getQueryName() {
		return queryName;
	}

	@Override
	public int getGridSpacing() {
		return gridSpacing;
	}

	@Override
	public int getTimeStep() {
		return timeStep;
	}

	@Override
	public int getSimulationLength() {
		return simulationLength;
	}

	@Override
	public float getPresentationInterval() {
		return presentationInterval;
	}

	@Override
	public float getAxisTilt() {
		return axisTilt;
	}

	@Override
	public float getEccentricity() {
		return eccentricity;
	}

	@Override
	public IGrid getGrid() {
		return grid;
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
