package messaging.events;

import messaging.Message;
import messaging.MessageListener;

public class StartMessage implements Message {

	@Override
	public void process(MessageListener l) {
		return;
	}
}