package db;

public interface Neo4jConstants {

	// Define the DB constraints
	public static final String CREATE_NODE_NAME_CONSTRAINT 			= "CREATE CONSTRAINT ON (n: Simulation) ASSERT n.name IS UNIQUE";

	public static final String FIND_SIMULATIONS_KEY 				= "find_simulation_names";

	public static final String FIND_SIMULATIONS_QUERY 				= "MATCH (a: Simulation) RETURN a";

	// Define the node creation statements
	public static final String CREATE_SIMULATION_KEY 				= "create_simulation_node";
	public static final String CREATE_TEMP_KEY 						= "create_temperature_node";
	public static final String CREATE_AXIS_TILT_KEY 				= "create_axis_node";
	public static final String CREATE_ECCENTRICITY_KEY 				= "create_eccentricity_node";
	public static final String CREATE_GRID_SPACING_KEY				= "create_grid_spacing_node";
	public static final String CREATE_TIME_STEP_KEY 				= "create_time_step_node";
	public static final String CREATE_PRESENTATION_INTERVAL_KEY 	= "create_presentation_interval_node";
	public static final String CREATE_SIMULATION_LENGTH_KEY 		= "create_simulation_length_node";

	public static final String CREATE_SIMULATION_NODE 				= "CREATE UNIQUE (n: Simulation { name : \"?\" }) 		RETURN n";
	public static final String CREATE_TEMP_NODE 					= "CREATE UNIQUE (n: Temperature { value : ? }) 		RETURN n";
	public static final String CREATE_AXIS_TILT_NODE 				= "CREATE UNIQUE (n: AxislTilt { value : ? }) 		RETURN n";
	public static final String CREATE_ECCENTRICITY_NODE 			= "CREATE UNIQUE (n: OrbitalEccentricity { value : ? }) 	RETURN n";
	public static final String CREATE_GRID_SPACING_NODE 			= "CREATE UNIQUE (n: GridSpacing { value : ? }) 		RETURN n";
	public static final String CREATE_TIME_STEP_NODE 				= "CREATE UNIQUE (n: TimeStep { value : ? }) 		RETURN n";
	public static final String CREATE_PRESENTATION_INTERVAL_NODE 	= "CREATE UNIQUE (n: PresentationInterval { value : ? }) 	RETURN n";
	public static final String CREATE_SIMULATION_LENGTH_NODE 		= "CREATE UNIQUE (n: SimulationLength { value : ? }) 	RETURN n";

	// Define the relationships creation statements
	public static final String CREATE_TEMP_REL_KEY 					= "create_temp_rel";
	public static final String CREATE_AXIS_REL_KEY 					= "create_axis_rel";
	public static final String CREATE_ECCENTRICITY_REL_KEY 			= "create_eccentricity_rel";
	public static final String CREATE_GRID_REL_KEY 					= "create_grid_rel";
	public static final String CREATE_TIME_REL_KEY 					= "create_time_rel";
	public static final String CREATE_PRESENTATIONAL_REL_KEY 		= "create_presentational_rel";
	public static final String CREATE_LENGTH_REL_KEY 				= "create_length_rel";

	public static final String CREATE_TEMP_REL = "MATCH (a: Simulation) WHERE a.name = \"?\" "
			+ "CREATE UNIQUE (a)-[r: HAS_TEMP { latitude: ?, longitude: ?, datetime: ? }]->(b: Temperate {value: ?}) "
			+ "RETURN a,r,b";

	public static final String CREATE_AXIS_REL = "MATCH (a: Simulation) WHERE a.name = \"?\" "
			+ "CREATE UNIQUE (a)-[r: HAS_AXIS]->(b: AxisTilt {value: ?}) "
			+ "RETURN a,r,b";

	public static final String CREATE_ECCENTRICITY_REL = "MATCH (a: Simulation) WHERE a.name = \"?\" "
			+ "CREATE UNIQUE (a)-[r: HAS_ECCENTRICITY]->(b: OrbitalEccentricity {value: ?}) "
			+ "RETURN a,r,b";

	public static final String CREATE_GRID_REL = "MATCH (a: Simulation) WHERE a.name = \"?\" "
			+ "CREATE UNIQUE (a)-[r: HAS_GRID]->(b: GridSpacing {value: ?}) "
			+ "RETURN a,r,b";

	public static final String CREATE_TIME_REL = "MATCH (a: Simulation) WHERE a.name = \"?\" "
			+ "CREATE UNIQUE (a)-[r: HAS_TIME]->(b: TimeStep {value: ?}) "
			+ "RETURN a,r,b";

	public static final String CREATE_PRESENTATIONAL_REL = "MATCH (a: Simulation) WHERE a.name = \"?\" "
			+ "CREATE UNIQUE (a)-[r: HAS_PRESENTATION]->(b: PresentationInterval { value: ?}) "
			+ "RETURN a,r,b";

	public static final String CREATE_LENGTH_REL = "MATCH (a: Simulation) WHERE a.name = \"?\" "
			+ "CREATE UNIQUE (a)-[r: HAS_LENGTH]->(b: SimulationLength { value: ?}) "
			+ "RETURN a,r,b";

	// Define the Query Statements
	public static final String MATCH_NODE_BY_NAME_KEY 		= "match_node_name";
	public static final String MATCH_NODE_BY_DATA_KEY 		= "match_node_values";
	public static final String GET_GRID_BY_DATE_TIME_KEY 	= "match_area_by_date";
	public static final String GET_DATE_TIME_KEY 			= "match_closest_datetime";

	public static final String MATCH_NODE_BY_NAME_QUERY = "MATCH (n:Simulation)-[ "
			+ ":HAS_PRESENTATION|:HAS_TIME|:HAS_GRID|:HAS_ECCENTRICITY|:HAS_AXIS "
			+ "]->(o) "
			+ "WHERE n.name = \"?\" "
			+ "RETURN { name: n.name, result: o }";

	public static final String MATCH_NODE_BY_DATA_QUERY = "MATCH (n:Simulation)-[ "
			+ ":HAS_PRESENTATION|:HAS_TIME|:HAS_GRID|:HAS_ECCENTRICITY:|HAS_AXIS "
			+ "]->(o) "
			+ "RETURN { name: n.name, "
			+ "	result: filter(x IN o.values WHERE x = ? OR x = ? OR x = ? OR x = ? OR x = ? )"
			+ "}";

	public static final String GET_GRID_BY_DATE_TIME_QUERY = "MATCH (n:Simulation)-[ r:HAS_TEMP ]->(t:Temperature) "
			+ "WHERE n.name = \"?\" AND r.datetime = ? "
			+ "WITH r.latitude as latitude, r.longitude as longitude, t.value as temperature "
			+ "RETURN temperature, latitude, longitude ";

	public static final String GET_DATE_TIME_QUERY = "MATCH (n:Simulation)-[r:HAS_TEMPERATURE]-(t:Temperature) "
			+ "WHERE n.name = \"?\" AND r.datetime <= ? "
			+ "WITH max(r.datetime) as datetime" + "RETURN datetime";

}
