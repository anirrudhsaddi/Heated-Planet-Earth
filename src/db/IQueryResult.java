package db;

import java.util.List;
import java.util.Map;

public interface IQueryResult {
	
	public boolean isEmpty();
	
	public boolean isErrored();
	
	public Exception getError();
	
	public List<String> getSimulationName();
	
	public List<Integer> getGridSpacing();
	
	public List<Integer> getTimeStep();
	
	public List<Integer> getSimulationLength();
	
	public List<Float> getPresentationInterval();
	
	public List<Float> getAxisTilt();
	
	public List<Float> getOrbitalEccentricity();
	
	public List<List<String>> getQueryList();

}
