package EarthSim;

import messaging.Message;
import messaging.Publisher;
import messaging.events.DisplayMessage;
import messaging.events.StartMessage;
import common.ComponentBase;

public class ControlEngine extends ComponentBase {
	
	private long lastDisplayTime = 0;
	private float presentationInterval;

	public ControlEngine() {
		super();
	}
	
	// override run
	// fire off display each interval
	@Override
	public void process() {
		
		long curTime = System.nanoTime();
		if ((curTime - lastDisplayTime) * 1e-9 >= presentationInterval) {
			Publisher.getInstance().send(new DisplayMessage());
			lastDisplayTime = curTime;
		}
	}

	@Override
	protected void performAction(Message msg) {
		
		if (msg instanceof StartMessage) {
			
			StartMessage start = (StartMessage) msg;
			this.start(start.presentationInterval());

		} else {
			System.err.printf("WARNING: No processor specified in class %s for message %s\n",
					this.getClass().getName(), msg.getClass().getName());
		}
	}
	
	private void start(float presentationInterval) {

		if (presentationInterval < 0 || presentationInterval > Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid presentationInterval value");
		
		this.presentationInterval = presentationInterval;
	}
}
