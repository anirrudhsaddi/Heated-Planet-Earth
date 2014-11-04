package EarthSim;

import java.sql.Date;
import EarthSim.InitiativeType;

public class SimulationOptions {
	private int gridSpacing = 15;
	private int timeStepInMinutes = 1;
	private String logFilePath = "c:\\";
	private boolean loggingEnabled = false;
	private boolean isSimulationMultithreaded;
	private boolean isPresentationMultithreaded;
	private InitiativeType initiativeType = InitiativeType.Mediator;
	private int bufferSize = 1;
	private float presentationDisplayRateInSeconds = .5f;
	private float opacity = .65f;

	// unchanging defaults
	private final float initialSunPosition = 180.0f;
	private final float initialCellTempInKelvin = 288f;
	private final Date initialDate = new Date(946659600000l); // 12/31/1999 at 12:00
	
		
	protected int getGridLength() {
		return 360/gridSpacing;
	}
	protected int getGridHeight() {
		return 180/gridSpacing;
	}
	protected int getGridSpacing() {
		return gridSpacing;
	}
	protected void setGridSpacing(int gridSpacing) {
		this.gridSpacing = gridSpacing;
	}
	protected int getTimeStepInMinutes() {
		return timeStepInMinutes;
	}
	protected void setTimeStepInMinutes(int timeStepInMinutes) {
		this.timeStepInMinutes = timeStepInMinutes;
	}
	protected boolean isSimulationMultithreaded() {
		return isSimulationMultithreaded;
	}
	protected void setSimulationMultithreaded(boolean isSimulationMultithreaded) {
		this.isSimulationMultithreaded = isSimulationMultithreaded;
	}
	protected boolean isPresentationMultithreaded() {
		return isPresentationMultithreaded;
	}
	protected void setPresentationMultithreaded(boolean isPresentationMultithreaded) {
		this.isPresentationMultithreaded = isPresentationMultithreaded;
	}
	protected InitiativeType getInitiativeType() {
		return initiativeType;
	}
	protected void setInitiativeType(InitiativeType initiativeType) {
		this.initiativeType = initiativeType;
	}
	protected int getBufferSize() {
		return bufferSize;
	}
	protected void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	protected float getPresentationDisplayRateInSeconds() {
		return presentationDisplayRateInSeconds;
	}
	protected void setPresentationDisplayRateInSeconds(
			float presentationDisplayRateInSeconds) {
		this.presentationDisplayRateInSeconds = presentationDisplayRateInSeconds;
	}
	protected float getInitialCellTemperatureInKelvin() {
		return this.initialCellTempInKelvin;
	}
	protected float getInitialSunPosition() {
		return this.initialSunPosition;
	}
	protected Date getInitialDateTime() {
		return this.initialDate;
	}
	public String getLogFilePath() {
		return logFilePath;
	}
	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}
	public boolean isLoggingEnabled() {
		return loggingEnabled;
	}
	public void setLoggingEnabled(boolean loggingEnabled) {
		this.loggingEnabled = loggingEnabled;
	}
	public float getOpacity() {
		return opacity;
	}
	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}
}
