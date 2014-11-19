package messaging.events;

import java.util.Map;
import java.util.TreeMap;

import messaging.Message;

public class ResultMessage implements Message {
	
	private int width = 10;
	private boolean needsCalculation;
	
	private final Map<Integer, Double> grid;
	
	public ResultMessage(boolean needsCalculation) {
		
		this.needsCalculation = needsCalculation;
		
		grid = new TreeMap<Integer, Double>();
	}
	
	public boolean needsCalculation() {
		return this.needsCalculation;
	}
	
	public boolean hasTemperature(int longitude, int latitude) {
		return grid.containsKey(latitude * width + longitude);
	}
	
	public void setTemperature(int longitude, int latitude, double temp) {
		if (longitude < 0 || latitude < 0)
			throw new IllegalArgumentException("index (" +longitude + ", " +latitude+ ") out of bounds");
		
		grid.put(latitude * width + longitude, temp);
	}
	
	public double getTemperature(int longitude, int latitude) {
		if (longitude < 0 || latitude < 0)
			throw new IllegalArgumentException("index (" + longitude + ", " + latitude + ") out of bounds");
		
		return grid.get(latitude * width + longitude);
	}
}
