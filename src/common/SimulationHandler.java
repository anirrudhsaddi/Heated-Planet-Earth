package common;

import messaging.Message;
import messaging.Publisher;
import messaging.events.ProduceMessage;

public class SimulationHandler implements IHandler {
	
	private final Message msg;
	
	public SimulationHandler(Message msg) {
		this.msg = msg;
	}

	@Override
	public synchronized void trigger() {
		System.out.println("SimulationHandler triggering msg...");
		msg.process(null);
	}
	
	@Override
	public void start() {
		Publisher.getInstance().send(new ProduceMessage());
	}
}