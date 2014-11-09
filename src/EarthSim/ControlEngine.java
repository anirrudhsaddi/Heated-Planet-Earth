package EarthSim;

import messaging.Message;
import messaging.Publisher;
import messaging.events.DisplayMessage;
import messaging.events.NeedDisplayDataMessage;
import messaging.events.ProduceContinuousMessage;
import messaging.events.ProduceMessage;
import simulation.EarthEngine;
import view.EarthDisplayEngine;
import common.Buffer;
import common.ComponentBase;

public class ControlEngine extends ComponentBase {

	// to start message
	public static final int DEFAULT_GRID_SPACING = 15;
	public static final int DEFAULT_TIME_STEP = 1;
	public static final int DEFAULT_SIMULATION_LENGTH = 12;
	public static final float DEFAULT_PRESENTATION_RATE = 0.01f;

	private boolean debugMode = false;

	private EarthEngine model;
	private EarthDisplayEngine view;

	private int bufferSize;
	private int debugCnt = 0;

	// to thread manager
	private Thread modelThread;
	private Thread viewThread;
	private Thread t;

	public ControlEngine() {
	}

	public void start() {
		start(DEFAULT_GRID_SPACING, DEFAULT_TIME_STEP, DEFAULT_PRESENTATION_RATE, DEFAULT_SIMULATION_LENGTH);
	}

	// make message. include values. people who care gets the values out of message
	// break up
	public void start(int gs, int timeStep, float presentationInterval, int simulationLength) {

		if (gs < 1 || gs > Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid grid spacing");

		if (timeStep < 1 || gs > Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid time step");

		if (simulationLength < 12 || simulationLength > 1200)
			throw new IllegalArgumentException("Invalid simulation length");

		if (presentationInterval < 0)
			throw new IllegalArgumentException("Invalid presentation interval");

		Buffer.getBuffer().create(this.bufferSize);

		// Instance model/view
		// who gets this and the subsrcibing?
		model = new EarthEngine(gs, timeStep, simulationLength);
		view = new EarthDisplayEngine(gs, timeStep, presentationInterval, simulationLength);

		Publisher pub = Publisher.getInstance();

		pub.subscribe(NeedDisplayDataMessage.class, model);
		pub.subscribe(ProduceMessage.class, model);

		// subscribe to count presented results for debug purposes
		pub.subscribe(DisplayMessage.class, this);

		// TODO send to thread manager
		// Kick off threads as appropriate
		modelThread = new Thread(model, "model");
		modelThread.start();

		viewThread = new Thread(view, "view");
		viewThread.start();

		// Kick off run loop
		paused = false;
		stopThread = false;
		if (t == null) {
			t = new Thread(this, "controller");
			t.start();
		}

		// start controller
	}

	// make message. move to thread manager
	public void stop() throws InterruptedException {

		// End run loop
		stopThread = true;
		paused = false;

		// TODO move to thread manager
		// Stop the contoller

		t.join();

		// Stop threads
		modelThread.interrupt();
		modelThread.join();

		viewThread.interrupt();
		viewThread.join();

		// destroy model/view
		model.close();
		model = null;
		view.close();
		view = null;

		t = null;
	}

	// make message. move to thread manager
	public void pause() {

		// make GUI updates
		// set variable to skip run loop contents
		paused = true;
		model.pause(paused);
		view.pause(paused);
	}

	// make message. move to thread manager
	public void resume() {

		// make GUI updates
		// set variable to NOT skip run loop contents
		paused = false;
		model.pause(paused);
		view.pause(paused);
	}

	@Override
	public void runAutomaticActions() {

		// not sure that this is for...

		if (debugMode && debugCnt >= 2) {
			stopThread = true;
		}

		// Allow non-threaded components to process event queues
		try {
			model.runAutomaticActions();
			model.processMessageQueue();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			view.runAutomaticActions();
			view.processMessageQueue();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void dispatchMessage(Message msg) {

		if (msg instanceof DisplayMessage) {
			process((DisplayMessage) msg);
		} else {
			System.err
					.printf("WARNING: No processor specified in class %s for message %s\n",
							this.getClass().getName(), msg.getClass().getName());
		}
	}

	public void process(DisplayMessage msg) {
		debugCnt++;
	}
}
