package messaging.events;

import messaging.Message;
import messaging.MessageListener;

public class DisplayMessage implements Message {

	@Override
	public void process(MessageListener l) {
		return;
	}
	// Meant to be a flag event
}