package common;

import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

public class Grid implements IGrid {

	// Used to transport the temps in the buffer
	private final int sunPosition,  width, height;
	private final float sunPositionDeg;
	private final Calendar currDateTime;
	private final String simulationName;
	
	private float planetX, planetY;
	private float sunLatitudeDeg = 0;

	// We use a TreeMap to not consume a contiguous amount of memory. It's
	// backed by a Red/Black Tree, so we get pretty decent access times
	private final Map<Integer, Double> grid;

	public Grid(String simulationName, int sunPosition, float sunPositionDeg, int width, int height, Calendar currDateTime) {

		this.simulationName = simulationName;
		this.sunPosition = sunPosition;
		this.sunPositionDeg = sunPositionDeg;
		this.currDateTime = currDateTime;
		this.width = width;
		this.height = height;

		grid = new TreeMap<Integer, Double>();
	}
	
	public Grid(Grid toCopy) {
		
		this.simulationName = toCopy.simulationName;
		this.sunPosition = toCopy.sunPosition;
		this.sunPositionDeg = toCopy.sunPositionDeg;
		this.width = toCopy.width;
		this.height = toCopy.height;
		this.planetX = toCopy.planetX;
		this.planetY = toCopy.planetY;
		this.sunLatitudeDeg = toCopy.sunLatitudeDeg;
		this.currDateTime = toCopy.currDateTime;
		
		this.grid = new TreeMap<Integer, Double>(toCopy.grid);
	}

	@Override
	public void setTemperature(int x, int y, double temp) {
		if (y > height || x > width || x < 0 || y < 0)
			throw new IllegalArgumentException("index (" + x + ", " + y + ") out of bounds");
		
		grid.put(y * width + x, temp);
	}

	@Override
	public double getTemperature(int x, int y) {
		if (y >= height || x >= width || x < 0 || y < 0)
			throw new IllegalArgumentException("index (" + x + ", " + y + ") out of bounds");
		
		return grid.get(y * width + x);
	}

	@Override
	public float getSunPositionDeg() {
		return this.sunPositionDeg;
	}
	
	public float getSunLatitudeDeg() {
		return this.sunLatitudeDeg;
	}
	
	public void setSunLatitudeDeg(float lat) {
		this.sunLatitudeDeg = (float) lat;
	}

	@Override
	public int getGridWidth() {
		return this.width;
	}

	@Override
	public int getGridHeight() {
		return this.height;
	}

	@Override
	public void setPlanetX(float x) {
		this.planetX = x;
	}

	@Override
	public void setPlanetY(float y) {
		this.planetY = y;
	}

	@Override
	public float getPlanetX() {
		return this.planetX;
	}

	@Override
	public float getPlanetY() {
		return this.planetY;
	}
	
	@Override
	public Calendar getDateTime() {
		return this.currDateTime;
	}
	
	@Override
	public String getSimulationName() {
		return this.simulationName;
	}
}
