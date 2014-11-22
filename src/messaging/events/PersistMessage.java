package messaging.events;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import messaging.Message;

public class PersistMessage implements Message {
	
	private final String simulationName;
	private final long dateTime;
	private final int width = 10;
	
	private List<Integer[]> coords;
	private final Map<Integer, Double> grid;
	
	public PersistMessage(String simulationName, long dateTime) {
		
		this.simulationName = simulationName;
		this.dateTime = dateTime;
		
		coords = new LinkedList<Integer[]>();
		grid = new TreeMap<Integer, Double>();
	}
	
	public long getDateTime() {
		return this.dateTime;
	}

	public String getSimulationName() {
		return this.simulationName;
	}
	
	public Iterator<Integer[]> genCoordinates() {
		return coords.iterator();
	}
	
	public void setTemperature(int longitude, int latitude, double temp) {
		
		grid.put(latitude * width + longitude, temp);
		
		coords.add(new Integer[] {longitude, latitude});
	}
	
	public double getTemperature(int longitude, int latitude) {
		
		return grid.get(latitude * width + longitude);
	}
}
