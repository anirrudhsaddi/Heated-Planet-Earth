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
	
	private final List<Integer[]> coords;
	private final Map<Integer, Double> grid;
	
	public PersistMessage(String simulationName, long dateTime) {
		
		this.simulationName = simulationName;
		this.dateTime = new Long(dateTime);
		
		coords = new LinkedList<Integer[]>();
		grid = new TreeMap<Integer, Double>();
	}
	
	public PersistMessage(PersistMessage msg) {
		
		this.simulationName = new String(msg.simulationName);
		this.dateTime = new Long(msg.dateTime);
		this.coords = new LinkedList<Integer[]>(msg.coords);
		this.grid = new TreeMap<Integer, Double>(msg.grid);
	}
	
	public long getDateTime() {
		return new Long(this.dateTime);
	}

	public String getSimulationName() {
		return this.simulationName;
	}
	
	public Iterator<Integer[]> genCoordinates() {
		return coords.iterator();
	}
	
	public void setTemperature(int longitude, int latitude, double temp) {
		
		int l = new Integer(longitude);
		int t = new Integer(latitude);
		
		grid.put(t * width + l, new Double(temp));
		
		coords.add(new Integer[] {l, t});
	}
	
	public double getTemperature(int longitude, int latitude) {
		
		return new Double(grid.get(latitude * width + longitude));
	}
}
