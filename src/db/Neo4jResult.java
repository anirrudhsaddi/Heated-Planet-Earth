package db;

import java.sql.ResultSet;
import java.sql.SQLException;

import common.IGrid;

public class Neo4jResult implements IQueryResult {
	
	private String queryName;
	private int gridSpacing, timeStep, simulationLength;
	private float presentationInterval, axisTilt, eccentricity;
	private IGrid grid;
	private Exception error;
	
	private boolean populated = false;
	
	public Neo4jResult(String queryName, int gridSpacing, int timeStep, int simulationLength, float presentationInterval, float axisTilt, float eccentricity) {
		
		this.queryName = queryName;
		this.gridSpacing = gridSpacing;
		this.timeStep = timeStep;
		this.simulationLength = simulationLength;
		this.presentationInterval = presentationInterval;
		this.axisTilt = axisTilt;
		this.eccentricity = eccentricity;
		
		populated = true;
	}
	
	public Neo4jResult(final ResultSet result) throws SQLException {
		
		// populate from result. If result has stuff...
		if (!result.isBeforeFirst())
			return;
		
		populated = true;
	}
	
	public Neo4jResult(final Exception result) {
		
		if (result == null)
			throw new IllegalArgumentException("Invalid Exception provided");
		
		this.error = result;
		populated = true;
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
