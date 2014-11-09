package simulation;

import common.ComponentBase;
import messaging.Message;
import messaging.Publisher;
import messaging.events.NeedDisplayDataMessage;
import messaging.events.ProduceContinuousMessage;
import messaging.events.ProduceMessage;

public class EarthEngine extends ComponentBase {
	
	Earth model;
	
	public EarthEngine(int gs, int timeStep, int simulationLength) {
		model = new Earth();
		model.configure(gs, timeStep, simulationLength);
		model.start();
	}
	
	@Override
	public void dispatchMessage(Message msg) {
		if (msg instanceof ProduceContinuousMessage) {
			process((ProduceContinuousMessage) msg);
		} else if (msg instanceof ProduceMessage) {
			process((ProduceMessage) msg);
		} else if (msg instanceof NeedDisplayDataMessage) {
			process((NeedDisplayDataMessage) msg);
		} else {
			System.err.printf("WARNING: No processor specified in class %s for message %s\n",
					this.getClass().getName(), msg.getClass().getName());
		}
	}

	private void process(ProduceContinuousMessage msg) {
		generateData();
		Publisher.getInstance().send(msg); // resend message to self (since continuous)
	}

	private void process(ProduceMessage msg) {
		generateData();
	}

	private void process(NeedDisplayDataMessage msg) {
		generateData();
	}

	public void close() {
		// destructor when done with class
	}
	
	private void generateData() {
		try {
			model.generate();
		} catch (InterruptedException e) {
			stopThread = true;
		}
	}

	@Override
	public void runAutomaticActions() throws Exception {
		// Do nothing
	}
}
