package simulation;

import messaging.Message;
import messaging.Publisher;
import messaging.events.ProduceMessage;
import messaging.events.StartMessage;
import common.ComponentBase;

public class EarthEngine extends ComponentBase {
	
	Earth model;
	
	public EarthEngine() {
		
		model = new Earth();
		
		Publisher.getInstance().subscribe(ProduceMessage.class, this);
	}
	
	@Override
	public void performAction(Message msg) {
		
		//System.out.println("EarthEngine. performAction on msg " + msg);
		
		if (msg instanceof StartMessage) {
			
			StartMessage start = (StartMessage) msg;
			start(start.gs(), start.timeStep(), start.simulationLength(), start.axisTilt(), start.eccentricity());

		} else if (msg instanceof ProduceMessage) {
			System.out.println("EarthEngine got a ProduceMessage");
			generateData();
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

	private void start(int gs, int timeStep, int simulationLength, float axisTilt, float eccentricity) {
		
		model.configure(gs, timeStep, simulationLength, axisTilt, eccentricity);
		model.start();
	}
}
