package messaging.events;

import messaging.Message;
import messaging.MessageListener;

public class ConsumeMessage implements Message {

	@Override
	public void process(MessageListener l) {
		l.generate();
	}
}