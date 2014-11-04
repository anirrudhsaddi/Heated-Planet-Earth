package common;

import messaging.Message;
import messaging.MessageListener;
import messaging.events.StartMessage;
import messaging.events.UpdatedMessage;

public class InitiativeHandler implements MessageListener {
	
	private final IHandler handler;
	public InitiativeHandler(IHandler handler) {
		this.handler = handler;
	}

	@Override
	public void onMessage(Message msg) {
		
		System.out.println("In " + this + ". Recevied msg " + msg);
		
		if (msg instanceof StartMessage) this.handler.start();
		if (msg instanceof UpdatedMessage) this.generate();
	}

	@Override
	public void generate() {
		this.handler.trigger();
	}

	@Override
	public void performAction() {
		return;
	}

	@Override
	public void processQueue() {
		return;
	}
}
