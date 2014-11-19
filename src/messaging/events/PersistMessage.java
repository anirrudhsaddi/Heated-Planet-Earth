package messaging.events;

import java.util.Map;
import java.util.TreeMap;

import messaging.Message;

public class PersistMessage implements Message {
	
	private final String simulationName;
	private final long dateTime;
	private final int width;
	private final int height;
	
	private final Map<Integer, Double> grid;
	
	public PersistMessage(String simulationName, long dateTime, int width, int height) {
		
		this.simulationName = simulationName;
		this.dateTime = dateTime;
		this.width = width;
		this.height = height;
		
		grid = new TreeMap<Integer, Double>();
	}
	
	public long getDateTime() {
		return this.dateTime;
	}

	public String getSimulationName() {
		return this.simulationName;
	}
	
	public int getGridWidth() {
		return this.width;
	}
	
	public int getGridHeight() {
		return this.height;
	}
	
	public void setTemperature(int x, int y, double temp) {
		if (y > height || x > width || x < 0 || y < 0)
			throw new IllegalArgumentException("index (" + x + ", " + y + ") out of bounds");
		
		grid.put(y * width + x, temp);
	}
	
	public double getTemperature(int x, int y) {
		if (y >= height || x >= width || x < 0 || y < 0)
			throw new IllegalArgumentException("index (" + x + ", " + y + ") out of bounds");
		
		return grid.get(y * width + x);
	}
}
