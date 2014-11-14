package db;

import java.util.Calendar;
import java.util.concurrent.Future;

public interface ISimDAO {
	
	public boolean createSimulationNode(String name);
	
	public boolean createTemperatureNode(int temperature);
	
	public boolean createAxisTilt(float axisTilt);
	
	public boolean createOrbitalEccentricityNode(float eccentricity);
	
	public boolean createGridSpacingNode(int gridSpacing);
	
	public boolean createTimeStepNode(int timeStep);
	
	public boolean createPresentationIntervalNode(float presentationInterval);
	
	public boolean createSimulationLengthNode(int simulationLength);
	
	public IQueryResult setSimulationName(String name, int gridSpacing, int timeStep, int simulationLength, float presentationInterval, float axisTilt, float eccentricity) throws Exception;
	
	public Future<IQueryResult> findSimulationByName(String name, int gridSpacing, int timeStep, int simulationLength, float presentationInterval, float axisTilt, float eccentricity);
	
	public Future<IQueryResult> findSimulationByData(int gridSpacing, int timeStep, int simulationLength, float presentationInterval, float axisTilt, float eccentricity);
	
	public Future<IQueryResult> findTemperaturesAt(String name, Calendar datetime, int[] locations);

}
