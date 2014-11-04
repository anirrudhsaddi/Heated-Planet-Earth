package messaging;

import messaging.events.ConsumeMessage;
import messaging.events.ProduceMessage;

public class ContinuouslyProduceCommand implements Message {

	@Override
	public void process(MessageListener l) {
		Publisher.getInstance().send(new ConsumeMessage());
		Publisher.getInstance().send(new ProduceMessage());
	}
}