package EarthSim;

public class CommandLineHelper {
	public static SimulationOptions GetOptions(String[] args){
		SimulationOptions options = new SimulationOptions();

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			
			// Buffer size
			if (arg.equalsIgnoreCase("-b")) {
				int input = getSwitchValue(args, i + 1);
				if (input <= 0) {
					throw new IllegalArgumentException(
							"-b must be a positive integer value greater than zero.");
				}
				options.setBufferSize(input);
			}
			
			// Simulation in own thread
			if (arg.equalsIgnoreCase("-s")) {
				options.setSimulationMultithreaded(true);
			}
			
			// Presentation in own thread
			if (arg.equalsIgnoreCase("-p")) {
				options.setPresentationMultithreaded(true);
			}
			
			// Simulation initiative
			if (arg.equalsIgnoreCase("-t")) {
				options.setInitiativeType(InitiativeType.Simulation);
			}
			
			// Presentation initiative
			if (arg.equalsIgnoreCase("-r")) {
				options.setInitiativeType(InitiativeType.Presentation);
			}
		}
		
		return options;
	}
	
	private static int getSwitchValue(String[] args, int expectedPosition) {
		if (expectedPosition < args.length) {
			try {
				return Integer.parseInt(args[expectedPosition]);
			} catch (Exception e) {
				return -1;
			}
		}
		return -1;
	}
}
