package messaging.events;

import common.IGrid;

import messaging.Message;

public class DisplayMessage implements Message {
	
	private final IGrid grid;
	
	public DisplayMessage(IGrid grid) {
		
		if (grid == null)
			throw new IllegalArgumentException("Invliad IGrid provided");
		
		this.grid = grid;
	}
	
	public IGrid getGrid() {
		return this.grid;
	}
}