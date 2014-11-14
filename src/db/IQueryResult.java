package db;

import common.IGrid;

public interface IQueryResult {
	
	public boolean isEmpty();
	
	public boolean isErrored();
	
	public Exception getError();
	
	public String getQueryName();
	
	public int getGridSpacing();
	
	public int getTimeStep();
	
	public int getSimulationLength();
	
	public float getPresentationInterval();
	
	public float getAxisTilt();
	
	public float getEccentricity();
	
	public IGrid getGrid();

}
