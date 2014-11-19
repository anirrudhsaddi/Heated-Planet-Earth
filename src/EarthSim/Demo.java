// Demo.java
package EarthSim;

import javax.swing.SwingUtilities;

public class Demo {

	int precision;
	int geographicPrecision;
	int temporalPrecision;
	
	public static void main(String[] args) {
		Demo demo = new Demo();
		demo.processArgs(args);
		demo.run();
	}

	private Demo() {
		// empty
		precision = 6; //TODO set the default precision
		geographicPrecision = 100;
		temporalPrecision = 100;
	}

	// Note: processArgs ignore args that are not p,g or t as long as you
	// provide a max of 6 input values.
	private void processArgs(String[] args) {
		
		if (args.length > 6)
			usage();

		for (int i = 0; i < args.length; i++) {
			
			String arg = args[i];
			
			// Precision
			if ("-p".equalsIgnoreCase(arg)) {
				
				if (i == -1 || i++ >= args.length) {
					System.out.println("-p needs a value.");
					usage();
				}
				
				this.precision = getPrecisionFromArg(args[i]);
			} 
			
			// geographicPrecision
			else if ("-g".equalsIgnoreCase(arg)) {
				
				if (i == -1 || i++ >= args.length) {
					System.out.println("-g needs a value.");
					usage();
				}
				
				this.geographicPrecision = getPrecisionFromArg(args[i]);
			}

			// temporalPrecision
			else if ("-t".equalsIgnoreCase(arg)) {
		
				if (i == -1 || i++ >= args.length) {
					System.out.println("-t needs a value.");
					usage();
				}
				
				this.temporalPrecision = getPrecisionFromArg(args[i]);
			}
		}
	}
	
	private int getPrecisionFromArg(String arg){
		int precision = 0;
		try {
			precision = Integer.parseInt(arg);
			if(precision <= 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException nfe) {
			System.out.println("Error reading -p value as a number. Please retry.");
			usage();
		}
		return precision;
	}

	private void usage() {
		//System.out.println("Usage: java EarthSim.Demo [-s] [-p] [-r|-t] [-b #]");
		System.out.println("Usage: java PlanetSim.Demo [-p #] [-g #] [-t #]");
		System.exit(-1);
	}

	private void run() {
		debug("Demo started with settings:");
		printSettings();
		createAndShowUI();
		debug("Demo running...");
	}

	private void createAndShowUI() {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ControlGUI ui = new ControlGUI(precision,geographicPrecision,temporalPrecision);
				ui.setVisible(true);
			}
		});
	}
	
	private void printSettings() {
		
		debug("Precision of Data Stored\t\t:" + precision);
		debug("Geograph Precision(sampling rate) \t:" + geographicPrecision + "%");
		debug("Temporal Precision\t\t\t:" + temporalPrecision + "%");
		debug("");
	}

	private void debug(String s) {
		System.out.println(s);
	}
}
