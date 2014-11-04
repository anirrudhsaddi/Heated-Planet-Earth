package common;

import messaging.Message;
import messaging.Publisher;
import messaging.events.UpdatedMessage;

public class ViewHandler implements IHandler {

	private final Message msg;
	
	public ViewHandler(Message msg) {
		this.msg = msg;
	}

	@Override
	public synchronized void trigger() {
		System.out.println("ViewHandler triggering msg...");
		msg.process(null);
	}
	
	@Override
	public void start() {
		return;
	}
}
