package db;

import java.sql.ResultSet;

import common.IGrid;

public class Neo4jSimulationDataResult implements IQueryResult {
	
	private String queryName;
	private int gridSpacing, timeStep, simulationLength;
	private float presentationInterval, axisTilt, eccentricity;
	private IGrid grid;
	private Exception error;
	
	public Neo4jSimulationDataResult(final ResultSet result) {
		
		if (result == null)
			throw new IllegalArgumentException("Invalid ResultSet provided");
		
		queryName = result.getString("");
	}
	
	public Neo4jSimulationDataResult(final Exception result) {
		if (result == null)
			throw new IllegalArgumentException("Invalid Exception provided");
		
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

}
