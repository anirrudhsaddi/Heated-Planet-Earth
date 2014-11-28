package messaging.events;

import messaging.Message;
import common.IGrid;

public class DeliverMessage implements Message {
	
	private final IGrid grid;
	
	public DeliverMessage(IGrid grid) {
		if (grid == null)
			throw new IllegalArgumentException("Inlivad IGrid provided");
		
		this.grid = grid;
	}
	
	public IGrid getGrid() {
		return this.grid;
	}
}
