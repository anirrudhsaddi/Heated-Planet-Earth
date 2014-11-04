package messaging.events;

import messaging.Message;
import messaging.MessageListener;

public class ProduceMessage implements Message {

	@Override
	public void process(MessageListener l) {
		System.out.println(this + ". Telling " + l + " to generate");
		l.generate();
	}
}