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

	public static final String FIND_SIMULATIONS_QUERY 				= "MATCH (a: Simulation) "
			+ "WITH a.name AS simulation "
			+ "RETURN simulation";

	// Define the node creation statements
	public static final String CREATE_SIMULATION_KEY 				= "create_simulation_node";
	public static final String CREATE_TEMP_KEY 						= "create_temperature_node";
	public static final String CREATE_AXIS_TILT_KEY 				= "create_axis_node";
	public static final String CREATE_ECCENTRICITY_KEY 				= "create_eccentricity_node";
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
	public static final String CREATE_ECCENTRICITY_NODE 			= "MERGE (n: OrbitalEccentricity { value : {1} }) "
			+ "WITH n.value AS orbitalEccentricity "
			+ "RETURN orbitalEccentricity";
	public static final String CREATE_GRID_SPACING_NODE 			= "MERGE (n: GridSpacing { value : {1} }) "
			+ "WITH n.value AS gridSpacing "
			+ "RETURN gridSpacing";
	public static final String CREATE_TIME_STEP_NODE 				= "MERGE (n: TimeStep { value : {1} }) "
			+ "WITH n.value AS timeStep "
			+ "RETURN timeStep";
	public static final String CREATE_PRESENTATION_INTERVAL_NODE 	= "MERGE (n: PresentationInterval { value : {1} }) "
			+ "WITH n.value AS presentationInterval" 
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

	public static final String CREATE_TEMP_REL = "MATCH (a: Simulation) WHERE a.name = {1} "
			+ "CREATE UNIQUE (a)-[r: HAS_TEMP { latitude: {2}, longitude: {3}, datetime: {4} }]->(b: Temperate {value: {5} }) "
			+ "WITH a.name AS simulation, r.latitude AS latitude, r.longitude AS longitude, r.datetime AS datetime, b AS temperature.value "
			+ "RETURN simulation, latitude, longitude, datetime, temperature";

	public static final String CREATE_AXIS_REL = "MATCH (a: Simulation) WHERE a.name = {1} "
			+ "CREATE UNIQUE (a)-[r: HAS_AXIS]->(b: AxisTilt {value: {2} }) "
			+ "WITH a.name AS simulation, b.value AS axisTilt "
			+ "RETURN simulation, axisTilt";

	public static final String CREATE_ECCENTRICITY_REL = "MATCH (a: Simulation) WHERE a.name = {1} "
			+ "CREATE UNIQUE (a)-[r: HAS_ECCENTRICITY]->(b: OrbitalEccentricity {value: {2} }) "
			+ "WITH a.name AS simulation, b.value AS orbitalEccentricity "
			+ "RETURN simulation, orbitalEccentricity";

	public static final String CREATE_GRID_REL = "MATCH (a: Simulation) WHERE a.name = {1} "
			+ "CREATE UNIQUE (a)-[r: HAS_GRID]->(b: GridSpacing {value: {2} }) "
			+ "WITH a AS simulation, r AS relation, b AS gridSpacing "
			+ "RETURN simulation, relationship, gridSpacing";

	public static final String CREATE_TIME_REL = "MATCH (a: Simulation) WHERE a.name = {1} "
			+ "CREATE UNIQUE (a)-[r: HAS_TIME]->(b: TimeStep {value: {2} }) "
			+ "WITH a.name AS simulation, b.value as timeStep "
			+ "RETURN simulation, timeStep";

	public static final String CREATE_PRESENTATIONAL_REL = "MATCH (a: Simulation) WHERE a.name = {1} "
			+ "CREATE UNIQUE (a)-[r: HAS_PRESENTATION]->(b: PresentationInterval { value: {2} }) "
			+ "WITH a.name AS simulation, b.value AS presentationInterval "
			+ "RETURN simulation, presentationInterval";

	public static final String CREATE_LENGTH_REL = "MATCH (a: Simulation) WHERE a.name = {1} "
			+ "CREATE UNIQUE (a)-[r: HAS_LENGTH]->(b: SimulationLength { value: {2} }) "
			+ "WITH a.name AS simulation, b.value AS simulationLength "
			+ "RETURN simulation, simulationLength";

	// Define the Query Statements
	public static final String MATCH_NODE_BY_NAME_KEY 		= "match_node_name";
	public static final String MATCH_NODE_BY_DATA_KEY 		= "match_node_values";
	public static final String GET_GRID_BY_DATE_TIME_KEY 	= "match_area_by_date";
	public static final String GET_DATE_TIME_KEY 			= "match_closest_datetime";

	public static final String MATCH_NODE_BY_NAME_QUERY = "MATCH (n:Simulation)-[ "
			+ ":HAS_PRESENTATION|:HAS_TIME|:HAS_GRID|:HAS_ECCENTRICITY|:HAS_AXIS "
			+ "]->(o) "
			+ "WHERE n.name = {1} "
			+ "RETURN { name: n.name, result: o }";

	public static final String MATCH_NODE_BY_DATA_QUERY = "MATCH (n:Simulation)-[ "
			+ ":HAS_PRESENTATION|:HAS_TIME|:HAS_GRID|:HAS_ECCENTRICITY:|HAS_AXIS "
			+ "]->(o) "
			+ "RETURN { name: n.name, "
			+ "	result: filter(x IN o.values WHERE x = {1} OR x = {2} OR x = {3} OR x = {4} OR x = {5} )"
			+ "}";

	public static final String GET_GRID_BY_DATE_TIME_QUERY = "MATCH (n:Simulation)-[ r:HAS_TEMP ]->(t:Temperature) "
			+ "WHERE n.name = {1} AND r.datetime = {2} "
			+ "WITH r.latitude AS latitude, r.longitude AS longitude, t.value AS temperature "
			+ "RETURN latitude, longitude, temperature";

	public static final String GET_DATE_TIME_QUERY = "MATCH (n:Simulation)-[r:HAS_TEMPERATURE]-(t:Temperature) "
			+ "WHERE n.name = {1} AND r.datetime <= {2} "
			+ "WITH max(r.datetime) as datetime" 
			+ "RETURN datetime";

}
