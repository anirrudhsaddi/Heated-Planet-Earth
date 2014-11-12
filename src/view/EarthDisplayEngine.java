package view;

import common.Buffer;
import common.ComponentBase;
import common.IBuffer;
import common.IGrid;
import messaging.Message;
import messaging.Publisher;
import messaging.events.DisplayMessage;
import messaging.events.ConsumeMessage;
import messaging.events.StartMessage;

public class EarthDisplayEngine extends ComponentBase {

	private final EarthDisplay display;

	// Current IGrid this display is updating with
	private IGrid grid;

	// Statistical data - TODO move to different module

	// set to true when initial conditions are overcome
	boolean steadyState = false;

	// 1e-12f;
	private final float STABLE_THRESHOLD = 0f;

	// set true to instrument stats (NOTE: some of these will change execution
	// timing)
	private final boolean STATISTIC_MODE = false;

	// Steady state assumed when when average equator temperature stabilizes
	private float lastEquatorAverage = 0.0f;

	// Profiling fields
	private float statInterval = 1.0f;

	private long lastStatTime = 0;
	private long maxUsedMem = 0;
	private long startWallTime;
	private long presentationCnt = 1;

	public EarthDisplayEngine() {

		super();

		this.display = new EarthDisplay();

		this.grid = null;

		Publisher.getInstance().subscribe(DisplayMessage.class, this);
		Publisher.getInstance().subscribe(ConsumeMessage.class, this);
	}

	@Override
	public synchronized void performAction(Message msg) {

		if (msg instanceof StartMessage) {

			StartMessage start = (StartMessage) msg;
			start(start.gs(), start.timeStep(), start.simulationLength(), start.axisTilt(), start.eccentricity());

		} else if (msg instanceof DisplayMessage) {
			
			if (grid != null) {
				if (STATISTIC_MODE)
					generateStatisicalData(grid);

				System.out.println("EarthDisplayEngine. Updating Display");
				display.update(grid);
				grid = null;
				System.out.println("EarthDisplayEngine. Done updating Display");
			}
			
			try {
				grid = Buffer.getBuffer().get();
			} catch (InterruptedException e) {
				grid = null;
			}

		} else if (msg instanceof ConsumeMessage) {

			System.out.println("EarthDisplayMessage. Got ConsumeMessage");

			if (grid == null) {
				try {
					grid = Buffer.getBuffer().get();
				} catch (InterruptedException e) {
					grid = null;
				}
			}
		} else {
			System.err.printf("WARNING: No processor specified in class %s for message %s\n", this.getClass().getName(), msg.getClass().getName());
		}
	}

	@Override
	public void stop() {

		super.stop();

		// destructor when done with class
		display.close();
	}

	private void start(int gs, int timeStep, int simulationLength, float axisTilt, float eccentricity) {

		display.display(gs, timeStep, simulationLength, axisTilt, eccentricity);
		display.update(grid);
	}

	// TODO move to separate module
	private void generateStatisicalData(IGrid data) {

		if (!steadyState && steadyStateReached(data)) {
			steadyState = true;
			System.out.printf("========STABLE REACHED!========: %d\n",
					data.getCurrentTime());
		}

		long curTime = System.nanoTime();

		// Sample memory usage periodically
		if ((curTime - lastStatTime) * 1e-9 > statInterval) {
			
			float wallTimePerPresentation = (float) (System.nanoTime() - startWallTime) / presentationCnt;
			System.out.printf("walltime/present (msec): %f\n", wallTimePerPresentation / 1e6);
			Runtime runtime = Runtime.getRuntime();
			
			System.gc();
			
			maxUsedMem = Math.max(maxUsedMem, runtime.totalMemory() - runtime.freeMemory());
			System.out.printf("usedMem: %.1f\n", maxUsedMem / 1e6);
			lastStatTime = curTime;

			IBuffer b = Buffer.getBuffer();
			System.out.printf("Buffer fill status: %d/%d\n", b.size() + 1, b.size() + b.getRemainingCapacity());

			startWallTime = System.nanoTime();
			presentationCnt = 0;

		}
		presentationCnt++;
	}

	// TODO move to separate module
	private boolean steadyStateReached(IGrid grid) {

		float equatorAverage = 0.0f;
		int eqIdx = grid.getGridHeight() / 2;
		
		for (int i = 0; i < grid.getGridWidth(); i++) {
			equatorAverage += grid.getTemperature(i, eqIdx);
		}
		
		equatorAverage /= grid.getGridWidth();

		boolean stable = false;

		if (Math.abs(equatorAverage - lastEquatorAverage) <= STABLE_THRESHOLD) {
			stable = true;
		}
		
		lastEquatorAverage = equatorAverage;
		return stable;

	}
}
