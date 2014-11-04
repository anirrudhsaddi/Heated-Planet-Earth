package simulation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import simulation.util.EarthCell;
import simulation.util.GridCell;
import common.AbstractEngine;
import common.Buffer;
import common.Grid;
import common.IGrid;
import common.State;

public final class Earth extends AbstractEngine {

	public static final double CIRCUMFERENCE = 4.003014 * Math.pow(10, 7);
	public static final double SURFACE_AREA = 5.10072 * Math.pow(10, 14);

	public static final int MAX_TEMP = 550; // shot in the dark here...
	public static final int INITIAL_TEMP = 288;
	public static final int MIN_TEMP = 0;

	private static final int DEFAULT_DEGREES = 15;
	private static final int DEFAULT_SPEED = 1; // minutes
	private static final int MAX_DEGREES = 180;
	private static final int MAX_SPEED = 1440;

	private static final int[] increments = { 6, 9, 10, 12, 15, 18, 20, 30, 36, 45, 60, 90, 180 };

	private int currentStep;
	private int width;
	private int height;
	private int sunPosition;

	private static GridCell prime = null;
	private int timeStep = DEFAULT_SPEED;
	private int gs = DEFAULT_DEGREES;
	
	private STATE state;
	
	public Earth(boolean simThreaded, State initiative) {
		super(simThreaded, initiative, State.SIMULATION);
		
		state = STATE.READY;
	}

	public GridCell getGrid() {
		return prime;
	}

	@Override
	public void configure(int gs, int timeStep) {

		if (gs <= 0 || gs > MAX_DEGREES)
			throw new IllegalArgumentException("Invalid grid spacing");

		if (timeStep <= 0 || timeStep > MAX_SPEED)
			throw new IllegalArgumentException("Invalid speed setting");

		this.timeStep = timeStep;

		// The following could be done better - if we have time, we should do so
		if (MAX_DEGREES % gs != 0) {
			for (int i : increments) {
				if (i < gs) this.gs = i;
				else break;
			}
		} else
			this.gs = gs;
		
		state = STATE.CONFIGURED;
	}

	@Override
	public void start() {
		
		System.out.println("\n\n" + this + " IS STARTING!!\n\n");
		
		state = STATE.STARTING;

		int x = 0, y = 0;

		width = (2 * MAX_DEGREES / this.gs); // rows
		height = (MAX_DEGREES / this.gs); // cols

		// do a reset
		sunPosition = (width / 2) % width;
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
		for (y = 1; y < height; y++) {

			// curr should be changed, but actually have not.
			this.createNextRow(bottom, curr, y); 
			
			curr = bottom.getTop();
			
			// left should be changed, but actually have not.
			this.createRow(curr, next, bottom.getLeft(), left, y);
			bottom = bottom.getTop();
		}
		
		// Calculate the average sun temperature
		float totaltemp = 0;
		float totalarea = 0;
		curr = prime;
				
		for (x = 0; x < height; x++) {
			GridCell rowgrid = curr.getLeft();
			for (y = 0; y < width; y++) {
				totaltemp += rowgrid.calTsun(sunPosition);
				totalarea += rowgrid.getSurfarea();
				rowgrid = rowgrid.getLeft();
			}
			curr = curr.getTop();
		}
		
		// Set initial average temperature
		GridCell.setAvgSuntemp(totaltemp / (width * height));
		GridCell.setAverageArea(totalarea / (width * height));
		
		state = STATE.STARTED;
		
		System.out.println("\n\n" + this + " IS DONE STARTING!!\n\n");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void generate() {
		
		System.out.println(this + " State: " + state + " Generating Grid...");
		
		if (IS_THREADED)
			while (state == STATE.READY || state == STATE.STARTING || state == STATE.CONFIGURED) { /* wait */ }
		else
			if (state == STATE.READY || state == STATE.CONFIGURED || state == STATE.STARTING) return;

		//System.out.println("generating grid...");
		Queue<EarthCell> bfs = new LinkedList<EarthCell>();
		Queue<EarthCell> calcd = new LinkedList<EarthCell>();

		currentStep++;

		int t = timeStep * currentStep;
		int rotationalAngle = (t % MAX_SPEED) * 360 / MAX_SPEED;
		sunPosition = ( (width * rotationalAngle) / 360 + (width / 2) ) % width;

		IGrid grid = new Grid(sunPosition, t, width, height);

		float suntotal = 0;
		float calcdTemp = 0;
		
		calcdTemp = prime.calculateTemp(sunPosition);
		suntotal = suntotal + prime.calTsun(sunPosition);;
		grid.setTemperature(prime.getX(), prime.getY(), calcdTemp);
		
		prime.visited(true);
		bfs.add(prime);

		while (!bfs.isEmpty()) {

			EarthCell point = bfs.remove();
			calcd.add(point);

			EarthCell child = null;
			Iterator<EarthCell> itr = point.getChildren(false);
			
			while (itr.hasNext()) {
				
				child = itr.next();
				child.visited(true);
				calcdTemp = child.calculateTemp(sunPosition);
				grid.setTemperature(child.getX(), child.getY(), calcdTemp);
				bfs.add(child);
				suntotal += child.calTsun(sunPosition);
			}
		}

		GridCell.setAvgSuntemp(suntotal /  (width * height));
		EarthCell c = calcd.poll();
		while (c != null) {
			c.visited(false);
			c.swapTemp();
			c = calcd.poll();
		}

		while(!this.stopped) {
			try {
				Buffer.getBuffer().add(new Grid((Grid) grid));
				System.out.println("Submitted Grid to buffer");
				break;
			} catch (InterruptedException e) {
				System.err.println("Unable to add to buffer: " + e);
			}
		}
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
	
	private enum STATE {
		READY, CONFIGURED, STARTING, STARTED;
	}
}
