package view;

import messaging.Message;
import messaging.Publisher;
import messaging.events.DisplayMessage;
import messaging.events.UpdatedMessage;
import common.AbstractEngine;
import common.Buffer;
import common.IGrid;
import common.State;

public class EarthDisplayEngine extends AbstractEngine {
	
	private final EarthDisplay earthDisplay;
	private IGrid grid = null;
	
	private int gs, timeStep;
	
	public EarthDisplayEngine(final boolean isThreaded, State initiative) {
		super(isThreaded, initiative, State.PRESENTATION);
		earthDisplay = new EarthDisplay();
	}
	
	@Override
	public void onMessage(Message msg) {

		if (msg instanceof DisplayMessage) {
			
			if (!IS_THREADED && initiative == self)
				Publisher.getInstance().send(new UpdatedMessage());
			
			if (grid != null) {
				System.out.println("Going to update display");
				earthDisplay.update(grid);
				grid = null;
			}
			
		} else
			super.onMessage(msg);
	}

	@Override
	public void generate() {
		
		if (grid == null) {
			grid = Buffer.getBuffer().get();
			System.out.println("Retrieved Grid " + grid + " from buffer");
		}
	}

	@Override
	public void configure(int gs, int timeStep) {
		
		if (gs <= 0 || gs >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid grid spacing value");
		
		this.gs = gs;
		this.timeStep = timeStep;
	}

	@Override
	public void start() {
		grid = null;
		earthDisplay.display(this.gs, this.timeStep);
		earthDisplay.update(grid);
	}
}