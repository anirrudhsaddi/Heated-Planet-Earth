package messaging.events;

import java.util.Calendar;

import messaging.Message;

public class StartMessage implements Message {

	private String simulationName;
	private int gs;
	private int timeStep;
	private int simulationLength;
	private float presentationInterval;
	private float axisTilt;
	private float eccentricity;
	private int precision;
	private int geoAccuracy;
	private int temporalAccuracy;
	private boolean animate;
	private Calendar startDate;

	public void setStartTime(Calendar startDate) {
		this.startDate = startDate;
	}

	public void setAnimated(boolean animate) {
		this.animate = animate;
	}

	public void setTemporalAccuracy(int temporalAccuracy) {
		this.temporalAccuracy = temporalAccuracy;
	}

	public void setGeoAccuracy(int geoAccuracy) {
		this.geoAccuracy = geoAccuracy;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public void setOrbitalEccentricity(float eccentricity) {
		this.eccentricity = eccentricity;
	}

	public void setAxisTilt(float axisTilt) {
		this.axisTilt = axisTilt;
	}

	public void setPresentationInterval(float presentationInterval) {
		this.presentationInterval = presentationInterval;
	}

	public void setSimulationLength(int simulationLengt) {
		this.simulationLength = simulationLengt;
	}

	public void setTimeStep(int timeStep) {
		this.timeStep = timeStep;
	}

	public void setGridSpacing(int gs) {
		this.gs = gs;
	}

	public void setSimulationName(String simulationName) {
		this.simulationName = simulationName;
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

	public boolean animate() {
		return new Boolean(this.animate);
	}

	public int precision() {
		return this.precision;
	}

	public int geoAccuracy() {
		return this.geoAccuracy;
	}

	public int temporalAccuracy() {
		return this.temporalAccuracy;
	}

	public Calendar getStartDate() {
		return this.startDate;
	}
}