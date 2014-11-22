package db;

public interface Neo4jConstants {

	// Define the DB constraints
	public static final String CREATE_NODE_NAME_CONSTRAINT 				= "CREATE CONSTRAINT ON (n: Simulation) ASSERT n.name IS UNIQUE";
	public static final String CREATE_TEMPERATURE_CONSTRAINT 			= "CREATE CONSTRAINT ON (n: Temperature) ASSERT n.value IS UNIQUE";
	public static final String CREATE_AXIST_TILT_CONSTRAINT 			= "CREATE CONSTRAINT ON (n: AxisTilt) ASSERT n.value IS UNIQUE";
	public static final String CREATE_ORBITAL_ECCENTRICITY_CONSTRAINT 	= "CREATE CONSTRAINT ON (n: OrbitalEccentricity) ASSERT n.value IS UNIQUE";
	public static final String CREATE_GRID_SAPCING_CONSTRAINT 			= "CREATE CONSTRAINT ON (n: GridSpacing) ASSERT n.value IS UNIQUE";
	public static final String CREATE_TIME_STEP_CONSTRAINT 				= "CREATE CONSTRAINT ON (n: TimeStep) ASSERT n.value IS UNIQUE";
	public static final String CREATE_PRESENTATION_INTERVAL_CONSTRAINT 	= "CREATE CONSTRAINT ON (n: PresentationInterval) ASSERT n.value IS UNIQUE";
	public static final String CREATE_SIMULATION_LENGTH_CONSTRAINT 		= "CREATE CONSTRAINT ON (n: SimulationLength) ASSERT n.value IS UNIQUE";
	

	public static final String FIND_SIMULATIONS_KEY 				= "find_simulation_names";
	public static final String FIND_TEMPERATURES_KEY 				= "find_temperature_values";
	public static final String FIND_TIME_STEP_KEY 					= "find_time_step_values";
	public static final String FIND_SIMULATION_LENGTH_KEY 			= "find_simulation_length_values";
	public static final String FIND_AXIS_TILT_KEY 					= "find_axis_tilt_values";
	public static final String FIND_ORBITAL_ECCENTRICITY_KEY 		= "find_orbital_eccentricity_values";
	public static final String FIND_GRID_SPACING_KEY 				= "find_grid_spacing_values";
	public static final String FIND_PRESENTATION_INTERVAL_KEY 		= "find_presentation_interval_values";

	public static final String FIND_SIMULATIONS_QUERY 				= "MATCH (a: Simulation) "
			+ "WITH a.name AS simulation "
			+ "RETURN simulation";
	
	public static final String FIND_TEMPERATURES_QUERY 				= "MATCH (a: Temperature) "
			+ "WITH a.value AS temperature "
			+ "RETURN temperature";
	
	public static final String FIND_TIME_STEP_QUERY 				= "MATCH (a: TimeStep) "
			+ "WHERE a.value = {1} "
			+ "WITH a.value AS timeStep "
			+ "RETURN timeStep";
	
	public static final String FIND_SIMULATION_LENGTH_QUERY 		= "MATCH (a: SimulationLength) "
			+ "WHERE a.value = {1} "
			+ "WITH a.value AS simulationLength "
			+ "RETURN simulationLength";
	
	public static final String FIND_AXIS_TILT_QUERY 				= "MATCH (a: AxisTilt) "
			+ "WHERE a.value = {1} "
			+ "WITH a.value AS axisTilt "
			+ "RETURN axisTilt";
	
	public static final String FIND_ORBITAL_ECCENTRICITY_QUERY 		= "MATCH (a: OrbitalEccentricity) "
			+ "WHERE a.value = {1} "
			+ "WITH a.value AS orbitalEccentricity "
			+ "RETURN orbitalEccentricity";
	
	public static final String FIND_GRID_SPACING_QUERY 				= "MATCH (a: GridSpacing) "
			+ "WHERE a.value = {1} "
			+ "WITH a.value AS gridSpacing "
			+ "RETURN gridSpacing";
	
	public static final String FIND_PRESENTATION_INTERVAL_QUERY 	= "MATCH (a: PresentationInterval) "
			+ "WHERE a.value = {1} "
			+ "WITH a.value AS presentationInterval "
			+ "RETURN presentationInterval";

	// Define the node creation statements
	public static final String CREATE_SIMULATION_KEY 				= "create_simulation_node";
	public static final String CREATE_TEMP_KEY 						= "create_temperature_node";
	public static final String CREATE_AXIS_TILT_KEY 				= "create_axis_node";
	public static final String CREATE_ORBITAL_ECCENTRICITY_KEY 				= "create_eccentricity_node";
	public static final String CREATE_GRID_SPACING_KEY				= "create_grid_spacing_node";
	public static final String CREATE_TIME_STEP_KEY 				= "create_time_step_node";
	public static final String CREATE_PRESENTATION_INTERVAL_KEY 	= "create_presentation_interval_node";
	public static final String CREATE_SIMULATION_LENGTH_KEY 		= "create_simulation_length_node";

	public static final String CREATE_SIMULATION_NODE 				= "MERGE (n: Simulation { name : {1} }) "
			+ "WITH n.name AS simulation "
			+ "RETURN simulation";
	public static final String CREATE_TEMP_NODE 					= "MERGE (n: Temperature { value : {1} }) "
			+ "WITH n.value AS temperature "
			+ "RETURN temperature";
	public static final String CREATE_AXIS_TILT_NODE 				= "MERGE (n: AxisTilt { value : {1} }) "
			+ "WITH n.value AS axisTilt "
			+ "RETURN axisTilt";
	public static final String CREATE_ORBITAL_ECCENTRICITY_NODE 			= "MERGE (n: OrbitalEccentricity { value : {1} }) "
			+ "WITH n.value AS orbitalEccentricity "
			+ "RETURN orbitalEccentricity";
	public static final String CREATE_GRID_SPACING_NODE 			= "MERGE (n: GridSpacing { value : {1} }) "
			+ "WITH n.value AS gridSpacing "
			+ "RETURN gridSpacing";
	public static final String CREATE_TIME_STEP_NODE 				= "MERGE (n: TimeStep { value : {1} }) "
			+ "WITH n.value AS timeStep "
			+ "RETURN timeStep";
	public static final String CREATE_PRESENTATION_INTERVAL_NODE 	= "MERGE (n: PresentationInterval { value : {1} }) "
			+ "WITH n.value AS presentationInterval " 
			+ "RETURN presentationInterval";
	public static final String CREATE_SIMULATION_LENGTH_NODE 		= "MERGE (n: SimulationLength { value : {1} }) "
			+ "WITH n.value AS simulationLength "
			+ "RETURN simulationLength";

	// Define the relationships creation statements
	public static final String CREATE_TEMP_REL_KEY 					= "create_temp_rel";
	public static final String CREATE_AXIS_REL_KEY 					= "create_axis_rel";
	public static final String CREATE_ECCENTRICITY_REL_KEY 			= "create_eccentricity_rel";
	public static final String CREATE_GRID_REL_KEY 					= "create_grid_rel";
	public static final String CREATE_TIME_REL_KEY 					= "create_time_rel";
	public static final String CREATE_PRESENTATION_REL_KEY 			= "create_presentation_rel";
	public static final String CREATE_LENGTH_REL_KEY 				= "create_length_rel";

	public static final String CREATE_TEMP_REL = "MATCH (a: Simulation), (b: Temperature) "
			+ "WHERE a.name = {1} AND b.value = {5} "
			+ "MERGE (a)-[r: HAS_TEMP { latitude: {2}, longitude: {3}, datetime: {4} }]->(b) "
			+ "WITH a.name AS simulation, r.latitude AS latitude, r.longitude AS longitude, r.datetime AS dateTime, b.value AS temperature "
			+ "RETURN simulation, latitude, longitude, dateTime, temperature";

	public static final String CREATE_AXIS_REL = "MATCH (a: Simulation), (b: AxisTilt) "
			+ "WHERE a.name = {1} AND b.value = {2} "
			+ "MERGE (a)-[r: HAS_AXIS]->(b) "
			+ "WITH a.name AS simulation, b.value AS axisTilt "
			+ "RETURN simulation, axisTilt";

	public static final String CREATE_ECCENTRICITY_REL = "MATCH (a: Simulation), (b: OrbitalEccentricity) "
			+ "WHERE a.name = {1} AND b.value = {2} "
			+ "MERGE (a)-[r: HAS_ECCENTRICITY]->(b) "
			+ "WITH a.name AS simulation, b.value AS orbitalEccentricity "
			+ "RETURN simulation, orbitalEccentricity";

	public static final String CREATE_GRID_REL = "MATCH (a: Simulation), (b: GridSpacing) "
			+ "WHERE a.name = {1} AND b.value = {2} "
			+ "MERGE (a)-[r: HAS_GRID]->(b) "
			+ "WITH a.name AS simulation, b.value AS gridSpacing "
			+ "RETURN simulation, gridSpacing";

	public static final String CREATE_TIME_REL = "MATCH (a: Simulation), (b: TimeStep) "
			+ "WHERE a.name = {1} AND b.value = {2} "
			+ "MERGE (a)-[r: HAS_TIME]->(b) "
			+ "WITH a.name AS simulation, b.value as timeStep "
			+ "RETURN simulation, timeStep";

	public static final String CREATE_PRESENTATIONAL_REL = "MATCH (a: Simulation), (b: PresentationInterval)"
			+ "WHERE a.name = {1} AND b.value = {2} "
			+ "MERGE (a)-[r: HAS_PRESENTATION]->(b) "
			+ "WITH a.name AS simulation, b.value AS presentationInterval "
			+ "RETURN simulation, presentationInterval";

	public static final String CREATE_LENGTH_REL = "MATCH (a: Simulation), (b: SimulationLength) "
			+ "WHERE a.name = {1} AND b.value = {2} "
			+ "MERGE (a)-[r: HAS_LENGTH]->(b) "
			+ "WITH a.name AS simulation, b.value AS simulationLength "
			+ "RETURN simulation, simulationLength";

	// Define the Query Statements
	public static final String MATCH_NODE_BY_NAME_KEY 		= "match_node_name";
	public static final String MATCH_NODE_BY_DATA_KEY 		= "match_node_values";
	public static final String GET_GRID_BY_DATE_TIME_KEY 	= "match_area_by_date";
	public static final String GET_DATE_TIME_KEY 			= "match_closest_datetime";

	public static final String MATCH_NODE_BY_NAME_QUERY = "MATCH (n:Simulation)-[:HAS_PRESENTATION|:HAS_TIME|:HAS_GRID|:HAS_ECCENTRICITY|:HAS_AXIS|:HAS_LENGTH]->(o) "
			+ "WHERE n.name = {1} "
			+ "WITH n.name as simulation, COLLECT( o.value) AS results "
			+ "RETURN simulation, results";

	public static final String MATCH_NODE_BY_DATA_QUERY = "MATCH (n:Simulation)-[:HAS_GRID]->(a), (n:Simulation)-[:HAS_TIME]->(b), "
			+ "(n:Simulation)-[:HAS_LENGTH]->(c), (n:Simulation)-[:HAS_PRESENTATION]->(d), (n:Simulation)-[:HAS_AXIS]->(e), (n:Simulation)-[:HAS_ECCENTRICITY]->(f) "
			+ "WHERE a.value = {1} AND b.value = {2} AND c.value = {3} AND d.value = {4} AND e.value = {5} AND f.value = {6} "
			+ "WITH n.name AS simulation, a.value AS gridSpacing, b.value AS timeStep, c.value AS simulationLength, d.value AS presentationInterval, e.value AS axisTilt, f.value AS orbitalEccentricity "
			+ "RETURN simulation, gridSpacing, timeStep, simulationLength, presentationInterval, axisTilt, orbitalEccentricity";

	public static final String GET_GRID_BY_DATE_TIME_QUERY = "MATCH (n:Simulation)-[r:HAS_TEMP]->(t:Temperature) "
			+ "WHERE n.name = {1} AND r.datetime = {2} "
			+ "WITH r.latitude AS latitude, r.longitude AS longitude, t.value AS temperature "
			+ "RETURN latitude, longitude, temperature";

	public static final String GET_DATE_TIME_QUERY = "MATCH (n:Simulation)-[r:HAS_TEMP]->(t:Temperature) "
			+ "WHERE n.name = {1} AND r.datetime <= {2} "
			+ "WITH max(r.datetime) as dateTime " 
			+ "RETURN dateTime";

}
