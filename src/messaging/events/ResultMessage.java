package messaging.events;

import messaging.Message;

public class ResultMessage implements Message {
	
	private Exception error;
	
	public ResultMessage(Exception error) {
		
		if (error == null)
			throw new IllegalArgumentException("Invalid error provided");
		
		this.error = error;
	}

}
