package db;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.Future;

public interface ISimulationDAO {
	
	public boolean createSimulationNode(String name) throws SQLException;
	
	public boolean createTemperatureRelationship(String name, int temperature) throws SQLException;
	
	public boolean createAxisTiltRelationship(String name, float axisTilt) throws SQLException;
	
	public boolean createOrbitalEccentricityRelationship(String name, float eccentricity) throws SQLException;
	
	public boolean createGridSpacingRelationship(String name, int gridSpacing) throws SQLException;
	
	public boolean createTimeStepRelationship(String name, int timeStep) throws SQLException;
	
	public boolean createPresentationIntervalRelationship(String name, float presentationInterval) throws SQLException;
	
	public boolean createSimulationLengthRelationship(String name, int simulationLength) throws SQLException;
	
	// Only user for Starting a simulation
	public IQueryResult setSimulationName(String name, int gridSpacing, int timeStep, int simulationLength, float presentationInterval, float axisTilt, float eccentricity) throws Exception;
	
	public Future<IQueryResult> findSimulationByName(String name) throws SQLException;
	
	public Future<IQueryResult> findSimulationByData(int gridSpacing, int timeStep, int simulationLength, float presentationInterval, float axisTilt, float eccentricity) throws SQLException;
	
	public void findTemperaturesAt(String name, Calendar startDateTime, int westLongitude, int eastLongitude, int northLatitude, int southLatitude) throws SQLException;

}
