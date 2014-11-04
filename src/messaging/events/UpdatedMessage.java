package messaging.events;

import messaging.Message;
import messaging.MessageListener;


public class UpdatedMessage implements Message {

	@Override
	public void process(MessageListener l) {
		return;
	}

	// Meant to be a flag event
}
