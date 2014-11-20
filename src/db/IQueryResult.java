package db;

import java.util.Iterator;

public interface IQueryResult {
	
	public boolean isEmpty();
	
	public boolean isErrored();
	
	public Exception getError();
	
	public Iterator<String> getQueryName();
	
	public Iterator<Integer> getGridSpacing();
	
	public Iterator<Integer> getTimeStep();
	
	public Iterator<Integer> getSimulationLength();
	
	public Iterator<Float> getPresentationInterval();
	
	public Iterator<Float> getAxisTilt();
	
	public Iterator<Float> getEccentricity();

}
