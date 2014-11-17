package messaging.events;

import messaging.Message;

public class StartMessage implements Message {
	
	private final String simulationName;
	private final int gs;
	private final int timeStep;
	private final int simulationLength;
	private final float presentationInterval;
	private final float axisTilt;
	private final float eccentricity;
	
	public StartMessage(String simulationName, int gs, int timeStep, float presentationInterval, int simulationLength, float axisTilt, float eccentricity) {
		
		this.simulationName			= simulationName;
		this.gs 					= gs;
		this.timeStep 				= timeStep;
		this.simulationLength 		= simulationLength;
		this.presentationInterval 	= presentationInterval;
		this.axisTilt 				= axisTilt;
		this.eccentricity 			= eccentricity;
		
	}
	
	public String getSimulationName() {
		return this.simulationName;
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
	
	public float axisTilt() {
		return new Float(this.axisTilt);
	}
	
	public float eccentricity() {
		return new Float(this.eccentricity);
	}
}