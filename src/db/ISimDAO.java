package db;

import java.util.Calendar;
import java.util.concurrent.Future;

public interface ISimDAO {
	
	public Future<IQueryResult> findSimulationByName(String name, int gridSpacing, int timeStep, int simulationLength, float presentatinoInterval, float axisTilt, float eccentricity);
	
	public Future<IQueryResult> findSimulationByData(int gridSpacing, int timeStep, int simulationLength, float presentatinoInterval, float axisTilt, float eccentricity);
	
	public Future<IQueryResult> findTemperaturesAt(String name, Calendar datetime, int[] locations);

}
