package EarthSim;

import java.util.Date;

public class SimulationStepResult implements Comparable<SimulationStepResult>{
	private int iteration;
	private long simulationIdleTimeInMs;
	private long presentationIdleTimeInMs;
	private long simulationStepCalculationTimeInMs;
	private long presentationStepCalculationTimeInMs;
	private float[][] cellTemperatures;
	private long memoryUsage;
	private float sunRotationalPosition;
	private Date calculationDate;
	private int bufferDepth;
	private boolean SkippedDueToRefreshRate;
	
	public SimulationStepResult(){
		calculationDate = new Date();
	}
	
	protected long getSimulationIdleTimeInMs() {
		return simulationIdleTimeInMs;
	}
	protected void setSimulationIdleTimeInMs(long simulationIdleTimeInMs) {
		this.simulationIdleTimeInMs = simulationIdleTimeInMs;
	}
	protected long getSimulationStepCalculationTimeInMs() {
		return simulationStepCalculationTimeInMs;
	}
	protected void setSimulationStepCalculationTimeInMs(long simulationStepCalculationTimeInMs) {
		this.simulationStepCalculationTimeInMs = simulationStepCalculationTimeInMs;
	}
	protected float[][] getCellTemperatures() {
		return cellTemperatures;
	}
	protected void setCellTemperatures(float[][] cellTemperatures) {
		this.cellTemperatures = cellTemperatures;
	}
	protected long getMemoryUsage() {
		return memoryUsage;
	}
	protected void setMemoryUsage(long memoryUsage) {
		this.memoryUsage = memoryUsage;
	}
	protected float getSunRotationalPosition() {
		return sunRotationalPosition;
	}
	protected void setSunRotationalPosition(float sunRotationalPosition) {
		this.sunRotationalPosition = sunRotationalPosition;
	}
	protected int getBufferDepth() {
		return bufferDepth;
	}
	protected void setBufferDepth(int bufferDepth) {
		this.bufferDepth = bufferDepth;
	}
	public Date getCalculationDate() {
		return calculationDate;
	}
	public void setCalculationDate(Date calculationDate) {
		this.calculationDate = calculationDate;
	}
	public int getIteration() {
		return iteration;
	}
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

	public long getPresentationIdleTimeInMs() {
		return presentationIdleTimeInMs;
	}

	public void setPresentationIdleTimeInMs(long presentationIdleTimeInMs) {
		this.presentationIdleTimeInMs = presentationIdleTimeInMs;
	}

	public long getPresentationStepCalculationTimeInMs() {
		return presentationStepCalculationTimeInMs;
	}

	public void setPresentationStepCalculationTimeInMs(
			long presentationStepCalculationTimeInMs) {
		this.presentationStepCalculationTimeInMs = presentationStepCalculationTimeInMs;
	}
	
	public boolean isSkippedDueToRefreshRate() {
		return SkippedDueToRefreshRate;
	}

	public void setSkippedDueToRefreshRate(boolean skippedDueToRefreshRate) {
		SkippedDueToRefreshRate = skippedDueToRefreshRate;
	}
	
	@Override
	public int compareTo(SimulationStepResult o) {
		return Double.compare(iteration, o.getIteration());
	}

	public double getTemperature(int x, int y) {
		try{
			return this.cellTemperatures[y][x];
		}catch(ArrayIndexOutOfBoundsException e){
			return 0;
		}
	}
}
