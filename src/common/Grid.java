package common;

import java.util.Map;
import java.util.TreeMap;

public class Grid implements IGrid {

	// Used to transport the temps in the buffer
	private final int sunPosition, time, width, height;

	// We use a TreeMap to not consume a contiguous amount of memory. It's
	// backed by a Red/Black Tree, so we get pretty decent access times
	private final Map<Integer, Float> grid;

	public Grid(int sunPosition, int time, int width, int height) {

		this.sunPosition = sunPosition;
		this.time = time;
		this.width = width;
		this.height = height;

		grid = new TreeMap<Integer, Float>();
	}
	
	public Grid(Grid toCopy) {
		
		this.sunPosition = toCopy.sunPosition;
		this.time = toCopy.time;
		this.width = toCopy.width;
		this.height = toCopy.height;
		
		this.grid = new TreeMap<Integer, Float>(toCopy.grid);
	}

	@Override
	public void setTemperature(int x, int y, float temp) {
		if (y >= height || x >= width || x < 0 || y < 0)
			throw new IllegalArgumentException("index (" + x + ", " + y + ") out of bounds");
		
		grid.put(y * width + x, temp);
	}

	@Override
	public float getTemperature(int x, int y) {
		if (y >= height || x >= width || x < 0 || y < 0)
			throw new IllegalArgumentException("index (" + x + ", " + y + ") out of bounds");
		
		return grid.get(y * width + x);
	}

	@Override
	public int getSunPosition() {
		return this.sunPosition;
	}
	
	@Override
	public int getCurrentTime() {
		return this.time;
	}

	@Override
	public int getGridWidth() {
		return this.width;
	}

	@Override
	public int getGridHeight() {
		return this.height;
	}
}
