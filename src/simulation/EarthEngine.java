package simulation;

import messaging.Message;
import messaging.Publisher;
import messaging.events.ProduceMessage;
import messaging.events.ResultMessage;
import messaging.events.StartMessage;
import common.ComponentBase;
import common.IMonitorCallback;

public class EarthEngine extends ComponentBase {
	
	private final Earth model;
	
	private int precision;
	private int geoAccuracy; // percentage of the cells to store
	private int temporalAccuracy; // percentage of the total number of calculations to save
	
	public EarthEngine(IMonitorCallback monitor) {
		
		model = new Earth(monitor);
		
		Publisher.getInstance().subscribe(ProduceMessage.class, this);
		Publisher.getInstance().subscribe(ResultMessage.class, this);
	}
	
	@Override
	public void performAction(Message msg) {
		
		//System.out.println("EarthEngine. performAction on msg " + msg);
		
		if (msg instanceof StartMessage) {
			
			StartMessage start = (StartMessage) msg;
			start(start.getSimulationName(), start.gs(), start.timeStep(), start.axisTilt(), start.eccentricity());

		} else if (msg instanceof ProduceMessage) {
			System.out.println("EarthEngine got a ProduceMessage");
			generateData();
		} else if (msg instanceof ResultMessage) {
			// TODO either error or IQueryResult containing the Grid to simulation from or display results 
			// We should display the results as textual and graphical. 
			// EarthDisplay has been updated to accept -1 as a "do not display color". When doing the calculation from the query result
			// just need to set -1 if the grid is not in the requested area
			throw new IllegalStateException("Support for ResultMessage has yet to be added. SimulationStatus needs to be updated. Earth needs to be updated");
		} else {
			System.err.printf("WARNING: No processor specified in class %s for message %s\n",
					this.getClass().getName(), msg.getClass().getName());
		}
	}

	public void close() {
		// destructor when done with class
	}
	
	private void generateData() {
		try {
			model.generate();
			Publisher.getInstance().send(new ProduceMessage());
		} catch (InterruptedException e) {
			this.stop();
		}
	}

	private void start(String simulationName, int gs, int timeStep, float axisTilt, float eccentricity) {
		
		model.configure(simulationName, gs, timeStep, axisTilt, eccentricity);
		model.start();
	}
}
