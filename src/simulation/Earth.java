package simulation;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import messaging.Publisher;
import messaging.events.DeliverMessage;
import messaging.events.DisplayMessage;
import messaging.events.PersistMessage;
import messaging.events.ResultMessage;
import messaging.events.StartMessage;
import simulation.util.GridCell;
import common.Buffer;
import common.Constants;
import common.Grid;
import common.IGrid;
import common.IMonitorCallback;

public final class Earth {

	private static Calendar currentDate, startDate;

	private static final int[] increments = { 6, 9, 10, 12, 15, 18, 20, 30, 36,
			45, 60, 90, 180 };

	private final IMonitorCallback monitor;

	private GridCell prime;

	private String simulationName;

	private float axisTilt;
	private float eccentricity;

	private int currentStep;
	private int width;
	private int height;
	private int sunPositionCell;
	private int currentTimeInSimulation;
	private int timeStep;
	private int gs;

	// persistance variables
	private int precision;
	private int totalDataToSave;
	private int totalGridsToSave;
	private int nth_data;
	private int nth_grids;
	
	// The user query can be thought of as producing a table in which there are
	// rows
	// corresponding to the times at which computations are made and columns
	// corresponding to the grid cells.
	// table DS has integer as time(rows), List<GridCell> is list of grid
	// cells(columns)
	private final Map<Calendar, List<GridCell>>	table = new TreeMap<Calendar, List<GridCell>>();


	public Earth(IMonitorCallback monitor) {

		if (monitor == null)
			throw new IllegalArgumentException("Invalid monitor provided");

		this.monitor = monitor;
	}

	public GridCell getGrid() {
		return prime;
	}

	public void configure(StartMessage start) {

		this.simulationName = start.getSimulationName();
		this.timeStep = start.timeStep();
		this.axisTilt = start.axisTilt();
		this.eccentricity = start.eccentricity();
		this.precision = start.precision();
		this.currentTimeInSimulation = 0;
		this.startDate = start.getStartDate();
		
		currentDate = (Calendar) startDate.clone();

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
		totalGridsToSave = totalGrids * (start.geoAccuracy() / 100);

		// Now calculate the number of 'buckets' (or every 'nth' piece)
		nth_grids = totalGrids / totalGridsToSave;

		// Convert simulationLength into minutes and divide by the timeStep.
		// This will give us the total number of generations we will do.
		// From there, get the number to save by applying the percentage
		int totalGens = (start.simulationLength() * 30 * 1440) / this.timeStep; // simlength*30*1440
		totalDataToSave = totalGens * (start.temporalAccuracy() / 100);
		System.out.println("Sim length: " + start.simulationLength()
				+ "\n Time step:" + this.timeStep);

		// Now calculate the number of 'buckets' (or every 'nth' piece)
		nth_data = totalGens / totalDataToSave;
	}

	public void start() {

		int x = 0, y = 0;

		// do a reset
		sunPositionCell = (width / 2) % width;
		currentStep = 0;

		if (prime != null) {
			prime.setGridProps(x, y, this.getLatitude(y), this.getLongitude(x),
					this.gs, this.axisTilt, this.eccentricity);
			prime.setTemp(Constants.INITIAL_TEMP);
		} else
			prime = new GridCell(Constants.INITIAL_TEMP, x, y,
					this.getLatitude(y), this.getLongitude(x), this.gs,
					this.axisTilt, this.eccentricity);

		prime.setTop(null);

		// South Pole
		GridCell next = null, curr = prime;
		for (x = 1; x < width; x++) {

			this.createRowCell(curr, next, null, x, y);
			curr = curr.getLeft();
		}

		// Stitch the grid row together
		prime.setRight(curr);
		curr.setLeft(prime);

		// Create each grid row, with the exception of the south pole
		GridCell bottom = prime, left = null;
		for (y = 1; y < height - 1; y++) {

			// curr should be changed, but actually have not.
			this.createNextRow(bottom, curr, y);

			curr = bottom.getTop();

			// left should be changed, but actually have not.
			this.createRow(curr, next, bottom.getLeft(), left, y);
			bottom = bottom.getTop();
		}

		this.createNextRow(bottom, curr, y);
		curr = bottom.getTop();

		// North Pole
		this.createRow(curr, next, bottom.getLeft(), left, y);

		// Calculate the average sun temperature
		float totaltemp = 0;
		float totalarea = 0;
		curr = prime;

		for (x = 0; x < height; x++) {
			GridCell rowgrid = curr.getLeft();
			for (y = 0; y < width; y++) {
				totaltemp += rowgrid.calTsun(sunPositionCell,
						currentTimeInSimulation);
				totalarea += rowgrid.getSurfarea();
				rowgrid = rowgrid.getLeft();
			}
			curr = curr.getTop();
		}

		// Set initial average temperature
		GridCell.setAvgSuntemp(totaltemp / (width * height));
		GridCell.setAverageArea(totalarea / (width * height));

	}

	public void generate() throws InterruptedException {

		this.monitor.notifyCurrentInterval(currentDate.getTimeInMillis());

		// Don't attempt to generate if output queue is full...
		if (Buffer.getBuffer().getRemainingCapacity() == 0)
			return;

		Queue<GridCell> bfs = new LinkedList<GridCell>();
		Queue<GridCell> calcd = new LinkedList<GridCell>();

		currentStep++;

		long time = timeStep * currentStep;
		currentDate.add(Calendar.MINUTE, timeStep);
		if (time % Constants.MINUTES_IN_A_MONTH == 0) {
			currentTimeInSimulation++;
		}

		long rotationalAngle = 360 - ((time % Constants.MAX_SPEED) * 360 / Constants.MAX_SPEED);
		sunPositionCell = (int) (((width * rotationalAngle) / 360 + (width / 2)) % width);

		float sunPositionDeg = rotationalAngle;
		if (sunPositionDeg > 180) {
			sunPositionDeg = sunPositionDeg - 360;
		}

		IGrid grid = new Grid(simulationName, sunPositionCell, sunPositionDeg,
				width, height, time, 0);

		double suntotal = 0;

		suntotal = suntotal + prime.getTSun();
		grid.setTemperature(prime.getX(), prime.getY(),
				prime.calculateTemp(sunPositionCell, currentTimeInSimulation));

		prime.visited(true);
		bfs.add(prime);

		// P3 - Heated Planet
		currentTimeInSimulation = currentStep * 200;

		while (!bfs.isEmpty()) {

			GridCell point = bfs.remove();
			calcd.add(point);

			GridCell child = null;
			Iterator<GridCell> itr = point.getChildren(false);

			while (itr.hasNext()) {

				child = itr.next();
				child.visited(true);
				grid.setTemperature(child.getX(), child.getY(),
						child.calculateTemp(sunPositionCell,
								currentTimeInSimulation));
				bfs.add(child);

				// suntotal += child.calTsun(sunPositionCell,
				// currentTimeInSimulation);
				suntotal += child.getTSun();

				// Set display values here
				grid.setSunLatitudeDeg((float) child
						.getSunLatitudeOnEarth(currentTimeInSimulation));
				grid.setPlanetX(child.getPlanetX(currentTimeInSimulation));
				grid.setPlanetY(child.getPlanetY(currentTimeInSimulation));
			}
		}

		GridCell.setAvgSuntemp(suntotal / (width * height));
		GridCell c = calcd.poll();
		while (c != null) {
			c.visited(false);
			c.swapTemp();
			c = calcd.poll();
		}

		// Buffer.getBuffer().add(grid);
		
		
		Publisher.getInstance().send(new DeliverMessage(grid));

		// Determine if we need to persist this grid and, if so, send
		// Message/payload
		
		// determine persisting based on temporalAccuracy
		if(currentStep % nth_data == 0){
			persistGrid(grid);
		}
	}

	private void persistGrid(IGrid grid) {

		// Determine if to store
		BigDecimal valueToStore;
		PersistMessage msg = new PersistMessage(simulationName,
				currentDate.getTimeInMillis());

		// TODO this requires (longitude, latitude) coords, not (x, y)
		int latitude, longitude;
		for (int x = 0; x < width; x++) {
			longitude = this.getLongitude(x);
			for (int y = 0; y < height; y++) {		
				
				// determine persisting based on geoAccuracy
				if((x+y) % nth_grids == 0){
					latitude = this.getLatitude(y);
					valueToStore = new BigDecimal(grid.getTemperature(x, y));
					msg.setTemperature(
							longitude,
							latitude,
							valueToStore.setScale(this.precision,
									BigDecimal.ROUND_HALF_UP).doubleValue());
				
				}
			}
		}

		Publisher.getInstance().send(msg);
	}

	private void createRow(GridCell curr, GridCell next, GridCell bottom,
			GridCell left, int y) {

		for (int x = 1; x < width; x++) {

			this.createRowCell(curr, next, bottom, x, y);
			bottom = bottom.getLeft();
			curr = curr.getLeft();
		}

		left = bottom.getTop(); // This should be the first cell we created

		// Stitch the grid row together
		curr.setLeft(left);
		left.setRight(curr);
	}

	private void createRowCell(GridCell curr, GridCell next, GridCell bottom,
			int x, int y) {

		if (curr.getLeft() != null) {
			GridCell l = curr.getLeft();
			l.setTemp(Constants.INITIAL_TEMP);
			l.setGridProps(x, y, this.getLatitude(y), this.getLongitude(x),
					this.gs, this.axisTilt, this.eccentricity);
		} else {
			next = new GridCell(null, bottom, null, curr,
					Constants.INITIAL_TEMP, x, y, this.getLatitude(y),
					this.getLongitude(x), this.gs, this.axisTilt,
					this.eccentricity);
			curr.setLeft(next);
			if (bottom != null) {
				bottom.setTop(next);
			}
		}
	}

	private void createNextRow(GridCell bottom, GridCell curr, int y) {

		if (bottom.getTop() != null) {
			curr = bottom.getTop();
			curr.setTemp(Constants.INITIAL_TEMP);
			curr.setGridProps(0, y, this.getLatitude(y), this.getLongitude(0),
					this.gs, this.axisTilt, this.eccentricity);
		} else {
			curr = new GridCell(null, bottom, null, null,
					Constants.INITIAL_TEMP, 0, y, this.getLatitude(y),
					this.getLongitude(0), this.gs, this.axisTilt,
					this.eccentricity);
			bottom.setTop(curr);
		}
	}

	private int getLatitude(int y) {
		return (y - (height / 2)) * this.gs;
	}

	private int getLongitude(int x) {
		return x < (width / 2) ? -(x + 1) * this.gs : (360) - (x + 1) * this.gs;
	}
	
	//TABLE DS related code
	public void populateTable(ResultMessage msg) {
		Calendar time = new GregorianCalendar(0, 0, 0, 0, 0, 0);
		List<GridCell> gridCells = new LinkedList<GridCell>();
		for (int i = msg.getNorthRegionBounds(); i <= msg.getSouthRegionBounds(); i++) {
			for (int j = msg.getEastRegionBounds(); j <= msg.getWestRegionBounds(); j++) {
				// check if this exists
				Integer[] checkInts = new Integer[] { i, j };
				if (msg.containsCoords(checkInts)) {
					GridCell thisCell = new GridCell(msg.getTemperature(i, j), 0, 0, i, j, 0, 0, 0);
					gridCells.add(thisCell);
				}
			}
		}
		this.table.put(time, gridCells);
	}

	public void interpolateTable(ResultMessage msg) {
		
		 // TODO: result message should give output in a range of time
		Calendar time = new GregorianCalendar(0, 0, 0, 0, 0, 0);

		List<GridCell> gridCells = new LinkedList<GridCell>();
		GridCell thisCell = null;
		
		for (int i = msg.getNorthRegionBounds(); i <= msg.getSouthRegionBounds(); i++) {
			for (int j = msg.getEastRegionBounds(); j <= msg.getWestRegionBounds(); j++) {
				
				Integer[] checkInts = new Integer[] { i, j };
				if (msg.containsCoords(checkInts)) {
					
					thisCell = new GridCell(msg.getTemperature(i, j), i, j, i, j, 0, 0, 0);
					gridCells.add(thisCell);
					
				} else if (thisCell != null) {
					
					double tempNearestCell = thisCell.getTemp();
					int k = thisCell.getX(), l = thisCell.getY();
					int dy = l - j, dx = k - i;
					double mytemp = tempNearestCell * (dy / dx);
					
					// SplineInterpolator splineInterp = new
					// SplineInterpolator();
					thisCell = new GridCell(mytemp, i, j, i, j, 0, 0, 0);
					gridCells.add(thisCell);
				}
			}
		}
		this.table.put(time, gridCells);
	}
	
	//In the situation when end date can not be found in DB, we have to run a new simmulation
	public void simulateFromTable(ResultMessage msg) {
		
	}
	
	public void setDisplayMsg(DisplayMessage msg) {
		msg.setTable(table);
		msg.setMaxTemp(this.getMax());
		msg.setMinTemp(this.getMin());
		msg.setMeanTempOverRegion(this.getMeanTempOverRegion());
		msg.setMeanTempOverTime(this.getMeanTempOverTime());
	}

	// Minimum temperature in the region, when and where it occurred;
	// that is, the smallest temperature in the entire table and the time and
	// location where it occurred
	public GridCell getMin() {
		
		GridCell result = new GridCell(Constants.MAX_TEMP, 0, 0, 0, 0, 0, 0, 0);
		for (Map.Entry<Calendar, List<GridCell>> entry : this.table.entrySet()) {
			Calendar time = entry.getKey();
			List<GridCell> gridCells = entry.getValue();
			for (GridCell mycell : gridCells) {
				if (mycell.getTemp() < result.getTemp()) {
					result = mycell;
					result.timeOfResult = time;
				}
			}
		}
		return result;
	}

	// Maximum temperature in the region, when and where it occurred;
	// that is, the largest temperature in the table and the time and location
	// where it occurred
	public GridCell getMax() {
		
		GridCell result = new GridCell(Constants.MIN_TEMP, 0, 0, 0, 0, 0, 0, 0);
		for (Map.Entry<Calendar, List<GridCell>> entry : this.table.entrySet()) {
			
			Calendar time = entry.getKey();
			List<GridCell> gridCells = entry.getValue();
			for (GridCell mycell : gridCells) {
				if (mycell.getTemp() > result.getTemp()) {
					result = mycell;
					result.timeOfResult = time;
				}
			}
		}
		
		return result;
	}

	// Mean temperature over the region for the requested times;
	// that is, for each row in the table, what was its mean temperature across
	// all of the columns. (The denominator is the number of columns.)
	public List<Double> getMeanTempOverRegion() {
		
		List<Double> meanTemps = new LinkedList<Double>();
		for (Map.Entry<Calendar, List<GridCell>> entry : this.table.entrySet()) {
			
			// Integer time = entry.getKey();
			List<GridCell> gridCells = entry.getValue();
			double temperatures = 0;
			for (GridCell mycell : gridCells) {
				temperatures += mycell.getTemp();
			}
			temperatures /= gridCells.size();
			meanTemps.add(temperatures);
		}
		
		return meanTemps;
	}

	// Mean temperature over the times for the requested region;
	// that is, for each column in the table, what was its mean temperature down
	// all rows. (The denominator is the number of rows.
	public List<Double> getMeanTempOverTime() {
		
		List<Double> meanTemps = new LinkedList<Double>();
		for (Map.Entry<Calendar, List<GridCell>> entry : this.table.entrySet()) {
			
			// Integer time = entry.getKey();
			List<GridCell> gridCells = entry.getValue();
			int i = 0;
			for (GridCell mycell : gridCells) {
				meanTemps.set(i++, meanTemps.get(i) + mycell.getTemp());
			}
		}
		
		int i = 0;
		for (Map.Entry<Calendar, List<GridCell>> entry : this.table.entrySet()) {
			meanTemps.set(i++, meanTemps.get(i) / this.table.size());
		}
		
		return meanTemps;
	}
}
