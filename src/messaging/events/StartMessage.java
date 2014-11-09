package messaging.events;

import messaging.Message;

public class StartMessage implements Message {
	
	private final int gs;
	private final int timeStep;
	private final int simulationLength;
	private final float presentationInterval;
	
	public StartMessage(int gs, int timeStep, float presentationInterval, int simulationLength) {
		
		if (gs < 1 || gs > Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid grid spacing");

		if (timeStep < 1 || gs > Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid time step");

		if (simulationLength < 12 || simulationLength > 1200)
			throw new IllegalArgumentException("Invalid simulation length");

		if (presentationInterval < 0)
			throw new IllegalArgumentException("Invalid presentation interval");
		
		this.gs = gs;
		this.timeStep = timeStep;
		this.simulationLength = simulationLength;
		this.presentationInterval = presentationInterval;
		
	}
	
	public int gs() {
		return new Integer(this.gs);
	}
	
	public int timeStep() {
		return new Integer(this.timeStep);
	}
	
	public int simulationLength() {
		return new Integer(this.simulationLength);
	}
	
	public float presentationInterval() {
		return new Float(this.presentationInterval);
	}
}