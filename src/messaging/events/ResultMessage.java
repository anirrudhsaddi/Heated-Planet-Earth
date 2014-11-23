package messaging.events;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.Constants;

import simulation.util.GridCell;
import messaging.Message;

public class ResultMessage implements Message {

	private int									width	= 10;
	private boolean								needsCalculation;

	private final List<Integer[]>				coords;
	private final Map<Integer, Double>			grid;
	private Map<Calendar, List<GridCell>>		table;

	
	private final int							southLatitude;
	private final int							northLatitude;
	private final int							westLongitude;
	private final int							eastLongitude;

	public ResultMessage(int southLatitude, int northLatitude, int westLongitude, int eastLongitude, boolean needsCalculation) {

		this.southLatitude = southLatitude;
		this.northLatitude = northLatitude;
		this.westLongitude = westLongitude;
		this.eastLongitude = eastLongitude;
		this.needsCalculation = needsCalculation;

		grid = new TreeMap<Integer, Double>();
		coords = new LinkedList<Integer[]>();
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

	public boolean containsCoords(Integer[] checkInts) {
		return coords.contains(checkInts);
	}
	
	public void setTemperature(int longitude, int latitude, double temp, long dateTime) {
		
		// Man this method is horribly inefficent...everytime we call this, a puppy dies :(. 
		// time permitting, let's make this better
		Calendar c = ((Calendar) Constants.START_DATE.clone());
		c.setTimeInMillis(dateTime);
		
		List<GridCell> list;
		if (this.table.containsKey(c)) {
			list = this.table.get(c);
			list.add(new GridCell(temp, 0, 0, latitude, longitude, 0, 0, 0));
		} else {
			list = new LinkedList<GridCell>();
			list.add(new GridCell(temp, 0, 0, latitude, longitude, 0, 0, 0));
		}
		
		this.table.put(c, list);
	}
	
	public Map<Calendar, List<GridCell>> getGridCells() {
		return this.table;
	}
}
