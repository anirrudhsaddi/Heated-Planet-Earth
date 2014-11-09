package common;

import messaging.Message;
import messaging.Publisher;
import messaging.events.ConsumeMessage;
import messaging.events.ProduceMessage;
import messaging.events.StartMessage;

public final class Controller extends ComponentBase implements IController {
	
	private final IBuffer buffer;
	private final Publisher publisher;
	
	// Arbitrary value
	private static final int DEFAULT_BUFFER_SIZE = 50;
	
	public Controller() {
		buffer = Buffer.getBuffer();
		publisher = Publisher.getInstance();
	}

	@Override
	public Boolean call() {
		
		while (!Thread.currentThread().isInterrupted() && !stopped.get()) {
		
			int remaining = buffer.getRemainingCapacity();
			
			// If the buffer is full, send consume and come back again on the next loop
			// if the buffer is not full, send produce
			// if the buffer is not empty, send consume
			if (remaining != 0) 
				publisher.send(new ProduceMessage()); 
				
			if (remaining != buffer.getCapacity())
				publisher.send(new ConsumeMessage()); 
		
		}
		return true;
	}

	@Override
	protected void performAction(Message msg) {
		
		if (msg instanceof StartMessage) {
			this.start();
		} else {
			System.err.printf("WARNING: No processor specified in class %s for message %s\n",
					this.getClass().getName(), msg.getClass().getName());
		}
	}
	
	@Override
	protected void stop() {
		
		super.stop();
		
		// remove subscriptions
		Publisher.unsubscribeAll();
	}
	
	private void start() {
		Buffer.getBuffer().create(DEFAULT_BUFFER_SIZE);
		publisher.send(new ProduceMessage());
	}
}
