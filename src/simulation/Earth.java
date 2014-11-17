package simulation;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimeZone;

import messaging.Publisher;
import messaging.events.DeliverMessage;
import messaging.events.ProduceMessage;
import simulation.util.GridCell;
import common.Buffer;
import common.Grid;
import common.IGrid;
import common.IModel;
import common.IMonitorCallback;

public final class Earth implements IModel {

	private static final int INITIAL_TEMP 	= 288;
	private static final int MAX_DEGREES 	= 180;
	
	// TODO I believe this has changed?
	// private static final int MAX_SPEED 		= 1440;
	
	private static final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

	private static final int[] increments = { 6, 9, 10, 12, 15, 18, 20, 30, 36, 45, 60, 90, 180 };
	
	private int currentStep;
	private int width;
	private int height;
	private int sunPositionCell;

	private GridCell prime = null;
	
	private float axisTilt;
	private float eccentricity;
	
	private int currentTimeInSimulation;
	private int timeStep;
	private int gs;
	
	private String simulationName;
	
	private final IMonitorCallback monitor;
	
	public Earth(IMonitorCallback monitor) {
		
		if (monitor == null)
			throw new IllegalArgumentException("Invalid monitor provided");
		
		this.monitor = monitor;
	}

	public GridCell getGrid() {
		return prime;
	}

	public void configure(String simulationName, int gs, int timeStep, float axisTilt, float eccentricity) {

		this.simulationName = simulationName;
		this.timeStep = timeStep;
		this.axisTilt = axisTilt;
		this.eccentricity = eccentricity;
		this.currentTimeInSimulation = 0;

		// The following could be done better - if we have time, we should do so
		if (MAX_DEGREES % gs != 0) {
			for (int i = 0; i < increments.length; i++) {
				if (gs > increments[i])
					this.gs = increments[i];
			}
		} else
			this.gs = gs;
	}

	public void start() {

		int x = 0, y = 0;

		width = (2 * MAX_DEGREES / this.gs); // rows
		height = (MAX_DEGREES / this.gs); // cols

		// do a reset
		sunPositionCell = (width / 2) % width;
		currentStep = 0;

		if (prime != null)	
			prime.setTemp(INITIAL_TEMP);
		else
			prime = new GridCell(INITIAL_TEMP, x, y, this.getLatitude(y), this.getLongitude(x), this.gs);

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
				totaltemp += rowgrid.calTsun(sunPositionCell, currentTimeInSimulation);
				totalarea += rowgrid.getSurfarea();
				rowgrid = rowgrid.getLeft();
			}
			curr = curr.getTop();
		}
		
		// Set initial average temperature
		GridCell.setAvgSuntemp(totaltemp / (width * height));
		GridCell.setAverageArea(totalarea / (width * height));
		
		// TODO don't auto-start
		// Publisher.getInstance().send(new ProduceMessage());
	}

	public void generate() throws InterruptedException {
		
		// TODO update currSimulationInterval (one month)
		// TODO make sure timeStep is scaled
		// TODO convert currTimeInSimulation to an actual long representation of the current date since epoch
		// Until then, this will break
		long currentDate = Long.MAX_VALUE;
		this.monitor.notifyCurrentInterval(currentDate, 0);

		// Don't attempt to generate if output queue is full...
		if (Buffer.getBuffer().getRemainingCapacity() == 0) {
			return;
		}

		//System.out.println("generating grid...");
		Queue<GridCell> bfs = new LinkedList<GridCell>();
		Queue<GridCell> calcd = new LinkedList<GridCell>();

		currentStep++;

		long t = timeStep * currentStep;
		int rotationalAngle = 360 - ((t % MAX_SPEED) * 360 / MAX_SPEED);
		sunPositionCell = ( (width * rotationalAngle) / 360 + (width / 2) ) % width;

		float sunPositionDeg = rotationalAngle;
		if (sunPositionDeg > 180) {
			sunPositionDeg = sunPositionDeg - 360;
		}

		IGrid grid = new Grid(simulationName, sunPositionCell, sunPositionDeg, width, height, t, 0);

		float suntotal = 0;
		float calcdTemp = 0;

		calcdTemp = prime.calculateTemp(sunPositionCell, currentTimeInSimulation);
		// suntotal = suntotal + prime.calTsun(sunPositionCell, currentTimeInSimulation);
		suntotal = suntotal + prime.getTSun();
		grid.setTemperature(prime.getX(), prime.getY(), calcdTemp);

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
				calcdTemp = child.calculateTemp(sunPositionCell, currentTimeInSimulation);
				grid.setTemperature(child.getX(), child.getY(), calcdTemp);
				bfs.add(child);
				
				// suntotal += child.calTsun(sunPositionCell, currentTimeInSimulation);
				suntotal += child.getTSun();
				
				//Set display values here
				grid.setSunLatitudeDeg((float) child.getSunLatitudeOnEarth(currentTimeInSimulation));
				grid.setPlanetX(child.getPlanetX(currentTimeInSimulation));
				grid.setPlanetY(child.getPlanetY(currentTimeInSimulation));
			}
		}

		GridCell.setAvgSuntemp(suntotal /  (width * height));
		GridCell c = calcd.poll();
		while (c != null) {
			c.visited(false);
			c.swapTemp();
			c = calcd.poll();
		}

		// Buffer.getBuffer().add(grid);
		Publisher.getInstance().send(new DeliverMessage(grid));
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
			l.setTemp(INITIAL_TEMP);
			l.setGridProps(x, y, this.getLatitude(y), this.getLongitude(x), this.gs);
		} else {
			next = new GridCell(null, bottom, null, curr, INITIAL_TEMP, x, y, this.getLatitude(y), this.getLongitude(x), this.gs);
			curr.setLeft(next);
			if (bottom != null) {
				bottom.setTop(next);
			}
		}
	}

	private void createNextRow(GridCell bottom, GridCell curr, int y) {

		if (bottom.getTop() != null) {
			curr = bottom.getTop();
			curr.setTemp(INITIAL_TEMP);
			curr.setGridProps(0, y, this.getLatitude(y), this.getLongitude(0), this.gs);
		} else {
			curr = new GridCell(null, bottom, null, null, INITIAL_TEMP, 0, y, this.getLatitude(y), this.getLongitude(0), this.gs);
			bottom.setTop(curr);
		}
	}

	private int getLatitude(int y) {
		return (y - (height / 2)) * this.gs;
	}

	private int getLongitude(int x) {
		return x < (width / 2) ? -(x + 1) * this.gs : (360) - (x + 1) * this.gs;
	}
}
