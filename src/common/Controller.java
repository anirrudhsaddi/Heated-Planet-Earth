package common;

import messaging.Publisher;
import messaging.events.ProduceMessage;

public class Controller implements IController {
	
	private final IBuffer buffer;
	private final Publisher publisher;
	
	public Controller() {
		buffer = Buffer.getBuffer();
		publisher = Publisher.getInstance();
	}
	
	public void start() {
		publisher.send(new ProduceMessage());
	}
	
	public void stop() {
		// remove subscriptions
		Publisher.unsubscribeAll();
	}

	// Start this gui threaded
	
	
	
	
	
	// If we're in third party mode and a display was just finished, it's
	// time to request another sim output.
	
	// or when there's room to produce. whats faster?
}
