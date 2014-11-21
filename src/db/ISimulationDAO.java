package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public interface ISimulationDAO {
	
	public ResultSet findNamedSimulations() throws SQLException;
	
	public boolean createOrMatchSimulationNode(String name) throws SQLException;
	
	public boolean createOrMatchTemperatureRelationship(String name, int latitude, int longitude, float datetime, int temperature) throws SQLException;
	
	public boolean createOrMatchAxisTiltRelationship(String name, float axisTilt) throws SQLException;
	
	public boolean createOrMatchOrbitalEccentricityRelationship(String name, float eccentricity) throws SQLException;
	
	public boolean createOrMatchGridSpacingRelationship(String name, int gridSpacing) throws SQLException;
	
	public boolean createOrMatchTimeStepRelationship(String name, int timeStep) throws SQLException;
	
	public boolean createOrMatchPresentationIntervalRelationship(String name, float presentationInterval) throws SQLException;
	
	public boolean createOrMatchSimulationLengthRelationship(String name, int simulationLength) throws SQLException;
	
	// Only user for Starting a simulation
	public IQueryResult setSimulationName(String name, int gridSpacing, int timeStep, int simulationLength, float presentationInterval, float axisTilt, float eccentricity) throws Exception;
	
	public Future<IQueryResult> findSimulationByName(String name) throws SQLException;
	
	public Future<IQueryResult> findSimulationByData(int gridSpacing, int timeStep, int simulationLength, float presentationInterval, float axisTilt, float eccentricity) throws SQLException;
	
	public void findTemperaturesAt(String name, Calendar startDateTime, int westLongitude, int eastLongitude, int northLatitude, int southLatitude) throws SQLException, InterruptedException, ExecutionException;

}
