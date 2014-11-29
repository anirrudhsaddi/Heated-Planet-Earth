package simulation;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import messaging.Publisher;
import messaging.events.ConfigureMessage;
import messaging.events.DeliverMessage;
import messaging.events.PersistMessage;
import messaging.events.ResultMessage;
import simulation.util.GridCell;

import common.Buffer;
import common.Constants;
import common.Grid;
import common.IGrid;
import common.IMonitorCallback;

public final class Earth {

	private static Calendar						currentDate, startDate;

	private static final int[]					increments	= { 6, 9, 10, 12, 15, 18, 20, 30, 36, 45, 60, 90, 180 };

	private final IMonitorCallback				monitor;

	private GridCell							prime;

	private String								simulationName;

	private float								axisTilt;
	private float								eccentricity;

	private int									currentNumberOfSimulations;
	private int									width;
	private int									height;
	private int									sunPositionCell;
	private int									currentMonthInSimulation;
	private int									timeStep;
	private int									gs;

	// persistance variables
	private int									precision;
	private float								totalDataToSave;
	private float								totalGridsToSave;
	private int									nth_data;
	private int									nth_grids;

	private ResultMessage						seed;

	private double[][]							base;

	public Earth(IMonitorCallback monitor) {

		if (monitor == null)
			throw new IllegalArgumentException("Invalid monitor provided");

		this.monitor = monitor;
	}

	public GridCell getGrid() {
		return prime;
	}

	public void configure(ConfigureMessage start) {

		this.simulationName = start.getSimulationName();
		this.timeStep = start.timeStep();
		this.axisTilt = start.axisTilt();
		this.eccentricity = start.eccentricity();
		this.precision = start.precision();
		this.currentMonthInSimulation = 0;
		this.startDate = start.getStartDate();

		currentDate = (Calendar) startDate.clone();
		System.out.println("CurrentDate: " + currentDate.getTime());

		// The following could be done better - if we have time, we should do so
		int gs = start.gs();
		if (Constants.MAX_DEGREES % gs != 0) {
			for (int i = 0; i < increments.length; i++) {
				if (gs > increments[i])
					this.gs = increments[i];
			}
		} else
			this.gs = gs;

		width = (2 * Constants.MAX_DEGREES / this.gs); // rows
		height = (Constants.MAX_DEGREES / this.gs); // cols

		// Using width, height, determine totalGridsToSave
		int totalGrids = width * height;
		float percent = ((float) start.geoAccuracy() / 100.0f);
		totalGridsToSave = totalGrids * percent;

		// Now calculate the number of 'buckets' (or every 'nth' piece)
		nth_grids = (int) (totalGrids / totalGridsToSave);

		// Convert simulationLength into minutes and divide by the timeStep.
		// This will give us the total number of generations we will do.
		// From there, get the number to save by applying the percentage
		int totalGens = (start.simulationLength() * 30 * 1440) / this.timeStep; // simlength
																				// *
																				// 30
																				// *
																				// 1440
		percent = ((float) start.temporalAccuracy() / 100.0f);
		totalDataToSave = totalGens * percent;

		// Now calculate the number of 'buckets' (or every 'nth' piece)
		nth_data = (int) (totalGens / totalDataToSave);
	}
	
	public GridCell init() {
		return init(false);
	}
	
	public void setSeedMessage(ResultMessage msg) {
		this.seed = msg;
	}

	public GridCell init(boolean interpolate) {

		int longitude, latitude;
		int x = 0, y = 0;
		double temperature;

		// do a reset
		sunPositionCell = (width / 2) % width;
		currentNumberOfSimulations = 0;
		
		longitude = this.getLongitude(x);
		latitude = this.getLatitude(y);

		if (prime != null && !interpolate) {
			prime.setGridProps(x, y, latitude, longitude, this.gs, this.axisTilt, this.eccentricity);
			prime.setTemp(Constants.INITIAL_TEMP);
		} else {
			
			if (interpolate) {
				
				if (this.seed.containsCoords(longitude, latitude))
					temperature = this.seed.getTemperature(longitude, latitude);
				else
					temperature = this.interpolateGrid(x, y);
			} else 
				temperature = Constants.INITIAL_TEMP;
			
			prime = new GridCell(temperature, x, y, latitude, longitude, this.gs, this.axisTilt, this.eccentricity);
		}
			
		prime.setTop(null);

		// South Pole
		GridCell next = null, curr = prime;
		for (x = 1; x < width; x++) {
			
			this.createRowCell(curr, next, null, x, y, interpolate);
			curr = curr.getLeft();
		}

		// Stitch the grid row together
		prime.setRight(curr);
		curr.setLeft(prime);

		// Create each grid row, with the exception of the south pole
		GridCell bottom = prime, left = null;
		for (y = 1; y < height - 1; y++) {

			// curr should be changed, but actually have not.
			this.createNextRow(bottom, curr, y, interpolate);

			curr = bottom.getTop();

			// left should be changed, but actually have not.
			this.createRow(curr, next, bottom.getLeft(), left, y, interpolate);
			bottom = bottom.getTop();
		}

		this.createNextRow(bottom, curr, y, interpolate);
		curr = bottom.getTop();

		// North Pole
		this.createRow(curr, next, bottom.getLeft(), left, y, interpolate);

		// Calculate the average sun temperature
		float totaltemp = 0;
		float totalarea = 0;
		curr = prime;

		for (x = 0; x < height; x++) {

			GridCell rowgrid = curr.getLeft();
			for (y = 0; y < width; y++) {
				
				totaltemp += rowgrid.calTsun(sunPositionCell, currentMonthInSimulation);
				totalarea += rowgrid.getSurfarea();
				rowgrid = rowgrid.getLeft();
			}
			
			curr = curr.getTop();
		}

		// Set initial average temperature
		GridCell.setAvgSuntemp(totaltemp / (width * height));
		GridCell.setAverageArea(totalarea / (width * height));
		
		return prime;
	}

	public void generate() throws InterruptedException {

		this.monitor.notifyCurrentInterval(currentDate.getTimeInMillis());

		// Don't attempt to generate if output queue is full...
		if (Buffer.getBuffer().getRemainingCapacity() == 0)
			return;

		Queue<GridCell> bfs = new LinkedList<GridCell>();
		Queue<GridCell> calcd = new LinkedList<GridCell>();

		long totlaMinutesInSimulation = timeStep * currentNumberOfSimulations;
		long rotationalAngle = 360 - ((totlaMinutesInSimulation % Constants.MAX_SPEED) * 360 / Constants.MAX_SPEED);
		sunPositionCell = (int) (((width * rotationalAngle) / 360 + (width / 2)) % width);

		float sunPositionDeg = rotationalAngle;
		if (sunPositionDeg > 180) {
			sunPositionDeg = sunPositionDeg - 360;
		}

		IGrid grid = new Grid(simulationName, sunPositionCell, sunPositionDeg, width, height, currentDate);

		double suntotal = 0;

		suntotal = suntotal + prime.getTSun();
		grid.setTemperature(prime.getX(), prime.getY(), prime.calculateTemp(sunPositionCell, currentMonthInSimulation));

		prime.visited(true);
		bfs.add(prime);

		// P3 - Heated Planet
		// currentTimeInSimulation = currentStep * 200;

		while (!bfs.isEmpty()) {

			GridCell point = bfs.remove();
			calcd.add(point);

			GridCell child = null;
			Iterator<GridCell> itr = point.getChildren(false);

			while (itr.hasNext()) {

				child = itr.next();
				child.visited(true);
				grid.setTemperature(child.getX(), child.getY(),
						child.calculateTemp(sunPositionCell, currentMonthInSimulation));
				bfs.add(child);

				suntotal += child.getTSun();

				// Set display values here
				grid.setSunLatitudeDeg((float) child.getSunLatitudeOnEarth(currentMonthInSimulation));
				grid.setPlanetX(child.getPlanetX(currentMonthInSimulation));
				grid.setPlanetY(child.getPlanetY(currentMonthInSimulation));
			}
		}

		GridCell.setAvgSuntemp(suntotal / (width * height));
		GridCell c = calcd.poll();
		while (c != null) {
			c.visited(false);
			c.swapTemp();
			c = calcd.poll();
		}

		Publisher.getInstance().send(new DeliverMessage(grid));

		// Determine if we need to persist this grid and, if so, send
		// Message/payload

		// determine persisting based on temporalAccuracy
		if (currentNumberOfSimulations % nth_data == 0)
			persistGrid(grid);
		
		currentDate.add(Calendar.MINUTE, timeStep);
		currentMonthInSimulation = currentDate.get(Calendar.MONTH);
		
		currentNumberOfSimulations++;
	}

	private void persistGrid(IGrid grid) {

		// Determine if to store
		BigDecimal valueToStore;
		PersistMessage msg = new PersistMessage(simulationName, currentDate.getTimeInMillis());

		int latitude, longitude;
		for (int x = 0; x < width; x++) {

			longitude = this.getLongitude(x);
			for (int y = 0; y < height; y++) {

				// determine persisting based on geoAccuracy
				if ((x + y) % nth_grids == 0) {
					latitude = this.getLatitude(y);
					valueToStore = new BigDecimal(grid.getTemperature(x, y));
					msg.setTemperature(longitude, latitude, valueToStore.setScale(this.precision, BigDecimal.ROUND_HALF_UP).doubleValue());
				}
			}
		}

		Publisher.getInstance().send(msg);
	}

	private void createRow(GridCell curr, GridCell next, GridCell bottom, GridCell left, int y, boolean interpolate) {

		for (int x = 1; x < width; x++) {

			this.createRowCell(curr, next, bottom, x, y, interpolate);
			bottom = bottom.getLeft();
			curr = curr.getLeft();
		}

		left = bottom.getTop(); // This should be the first cell we created

		// Stitch the grid row together
		curr.setLeft(left);
		left.setRight(curr);
	}

	private void createRowCell(GridCell curr, GridCell next, GridCell bottom, int x, int y, boolean interpolate) {

		if (curr.getLeft() != null && !interpolate) {
			GridCell l = curr.getLeft();
			l.setTemp(Constants.INITIAL_TEMP);
			l.setGridProps(x, y, this.getLatitude(y), this.getLongitude(x), this.gs, this.axisTilt, this.eccentricity);
		} else {
			
			int longitude = this.getLongitude(x);
			int latitude = this.getLatitude(y);
			double temperature;
			
			if (interpolate) {
				
				if (this.seed.containsCoords(longitude, latitude)) {
					temperature = this.seed.getTemperature(longitude, latitude);
				} else {
					temperature = this.interpolateGrid(x, y);
				}
			} else {
				temperature = Constants.INITIAL_TEMP;
			}
			
			next = new GridCell(null, bottom, null, curr, temperature, x, y, latitude, longitude, this.gs, this.axisTilt, this.eccentricity);
			curr.setLeft(next);
			if (bottom != null) {
				bottom.setTop(next);
			}
		}
	}

	private void createNextRow(GridCell bottom, GridCell curr, int y, boolean interpolate) {

		if (bottom.getTop() != null && !interpolate) {
			curr = bottom.getTop();
			curr.setTemp(Constants.INITIAL_TEMP);
			curr.setGridProps(0, y, this.getLatitude(y), this.getLongitude(0), this.gs, this.axisTilt, this.eccentricity);
		} else {
			
			int longitude = this.getLongitude(0);
			int latitude = this.getLatitude(y);
			double temperature;
			
			if (interpolate) {
				
				if (this.seed.containsCoords(longitude, latitude)) {
					temperature = this.seed.getTemperature(longitude, latitude);
				} else {
					temperature = this.interpolateGrid(0, y);
				}
			} else {
				temperature = Constants.INITIAL_TEMP;
			}
			
			curr = new GridCell(null, bottom, null, null, temperature, 0, y, latitude, longitude, this.gs, this.axisTilt, this.eccentricity);
			bottom.setTop(curr);
		}
	}

	private int getLatitude(int y) {
		return (y - (height / 2)) * this.gs;
	}

	private int getLongitude(int x) {
		return x < (width / 2) ? -(x + 1) * this.gs : (360) - (x + 1) * this.gs;
	}

	private double interpolateGrid(int x, int y) {

		if (base == null)
			return Constants.INITIAL_TEMP;

		int x1 = x - 1, x2 = x + 1, y1 = y - 1, y2 = y + 1;
		double Q11, Q21, Q12, Q22;

		try {
			Q11 = base[x1][y1];
		} catch (NullPointerException e) {
			Q11 = Constants.INITIAL_TEMP;
		}

		try {
			Q12 = base[x1][y2];
		} catch (NullPointerException e) {
			Q12 = Constants.INITIAL_TEMP;
		}

		try {
			Q21 = base[x2][y1];
		} catch (NullPointerException e) {
			Q21 = Constants.INITIAL_TEMP;
		}

		try {
			Q22 = base[x2][y2];
		} catch (NullPointerException e) {
			Q22 = Constants.INITIAL_TEMP;
		}
		
		double fQ11 = Q11 * (x2 - x) * (y2 - y);
		double fQ21 = Q21 * (x - x1) * (y2 -y);
		double fQ12 = Q12 * (x2 - x) * (y - y1);
		double fQ22 = Q22 * (x -x1) * (y -y1);
		
		return (1 / ((x2 - x1) * (y2 - y1))) + fQ11 + fQ21 + fQ12 + fQ22;
	}
}
