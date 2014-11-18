// Demo.java
package EarthSim;

import javax.swing.SwingUtilities;

public class Demo {

	public static void main(String[] args) {
		Demo demo = new Demo();
		demo.processArgs(args);
		demo.run();
	}

	private Demo() {
		// empty
	}

	// Note: processArgs ignore args that are not s,p,r,t or b as long as you
	// provide a max of 5 input values.
	private void processArgs(String[] args) {
		
		if (args.length > 5)
			usage();

//		for (int i = 0; i < args.length; i++) {
			
//			String arg = args[i];

//			if ("-s".equalsIgnoreCase(arg)) 
//				ownSimThread = true;
//			
//			else if ("-p".equalsIgnoreCase(arg)) 
//				ownPresThread = true;
//			
//			else if ("-r".equalsIgnoreCase(arg))
//				rset = true;
//			
//			else if ("-t".equalsIgnoreCase(arg))
//				tset = true;
//			
//			else if ("-b".equalsIgnoreCase(arg)) {
//				
//				if (i == -1 || i++ >= args.length) {
//					System.out.println("-b needs a value.");
//					usage();
//				}
//				
//				String bufSizeString = args[i];
//				
//				try {
//					bufferSize = Integer.parseInt(bufSizeString);
//					if(bufferSize <= 0) {
//						throw new NumberFormatException();
//					}
//				} catch (NumberFormatException nfe) {
//					System.out.println("Error reading -b value as a positive integer. Please retry.");
//					usage();
//				}
//				
//			} else
//				usage();
//		}
//
//		if (rset && tset) {
//			System.out.println("Cannot set both -r and -t.");
//			usage();
//		}
	}

	private void usage() {
		System.out.println("Usage: java EarthSim.Demo [-s] [-p] [-r|-t] [-b #]");
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
				ControlGUI ui = new ControlGUI();
				ui.setVisible(true);
			}
		});
	}

	private void printSettings() {
		
//		debug("Simulation on own thread\t:" + ownSimThread);
//		debug("Presentation on own thread\t:" + ownPresThread);
//		debug("Buffer Size\t\t\t:" + bufferSize);
//		debug("");
	}

	private void debug(String s) {
		System.out.println(s);
	}
}
