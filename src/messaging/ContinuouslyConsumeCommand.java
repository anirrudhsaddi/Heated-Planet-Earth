package messaging;

import messaging.events.ConsumeMessage;
import messaging.events.ProduceMessage;

public class ContinuouslyConsumeCommand implements Message {

	@Override
	public void process(MessageListener l) {
		Publisher.getInstance().send(new ProduceMessage());
		Publisher.getInstance().send(new ConsumeMessage());
	}
}