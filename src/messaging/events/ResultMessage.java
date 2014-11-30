package messaging.events;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import messaging.Message;

public class ResultMessage implements Message {

	private int							width	= 10;
	private boolean						needsCalculation;

	private final List<Integer[]>		coords;
	private final Map<Integer, Double>	grid;
	private Map<Long, ResultMessage>	tables;

	private final int					southLatitude;
	private final int					northLatitude;
	private final int					westLongitude;
	private final int					eastLongitude;

	public ResultMessage(int southLatitude, int northLatitude, int westLongitude, int eastLongitude,
			boolean needsCalculation) {

		this.southLatitude = southLatitude;
		this.northLatitude = northLatitude;
		this.westLongitude = westLongitude;
		this.eastLongitude = eastLongitude;
		this.needsCalculation = needsCalculation;

		grid = new TreeMap<Integer, Double>();
		coords = new LinkedList<Integer[]>();
	}
	
	public ResultMessage(ResultMessage msg) {
		
		this.southLatitude = new Integer(msg.southLatitude);
		this.northLatitude = new Integer(msg.northLatitude);
		this.westLongitude = new Integer(msg.westLongitude);
		this.eastLongitude = new Integer(msg.eastLongitude);
		this.needsCalculation = new Boolean(msg.needsCalculation);

		grid = new TreeMap<Integer, Double>(msg.grid);
		coords = new LinkedList<Integer[]>(msg.coords);
	}

	public int getSouthRegionBounds() {
		return this.southLatitude;
	}

	public int getNorthRegionBounds() {
		return this.northLatitude;
	}

	public int getWestRegionBounds() {
		return this.westLongitude;
	}

	public int getEastRegionBounds() {
		return this.eastLongitude;
	}

	public boolean needsCalculation() {
		return this.needsCalculation;
	}

	public Iterator<Integer[]> genCoordinates() {
		return coords.iterator();
	}

	public void setTemperature(int longitude, int latitude, double temp) {

		grid.put(latitude * width + longitude, temp);
		coords.add(new Integer[] { longitude, latitude });
	}

	public double getTemperature(int longitude, int latitude) {

		return grid.get(latitude * width + longitude);
	}

	public boolean containsCoords(int longitude, int latitude) {

		for (Integer[] i : this.coords) {
			if (i[0] == longitude && i[1] == latitude)
				return true;
		}

		return false;
	}

	public void setTables(Hashtable<Long, ResultMessage> tables) {
		this.tables = new Hashtable<Long, ResultMessage> (tables);
	}

	public Map<Long, ResultMessage> getTables() {
		return this.tables;
	}
}
