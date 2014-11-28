package simulation;

import view.TableDisplay;
import messaging.Message;
import messaging.Publisher;
import messaging.events.DisplayMessage;
import messaging.events.ProduceMessage;
import messaging.events.ResultMessage;
import messaging.events.ConfigureMessage;
import common.ComponentBase;
import common.IMonitorCallback;

public class EarthEngine extends ComponentBase {

	private final Earth	model;

	public EarthEngine(IMonitorCallback monitor) {

		model = new Earth(monitor);

		Publisher.getInstance().subscribe(ProduceMessage.class, this);
		Publisher.getInstance().subscribe(ResultMessage.class, this);
	}

	@Override
	public void performAction(Message msg) {

		if (msg instanceof ConfigureMessage) {
			
			model.configure(((ConfigureMessage) msg));
			model.start();

		} else if (msg instanceof ProduceMessage) {
			generateData();
		} else if (msg instanceof ResultMessage) {
			processQueryResult(((ResultMessage) msg));
		} else {
			System.err.printf("WARNING: No processor specified in class %s for message %s\n",
					this.getClass().getName(), msg.getClass().getName());
		}
	}

	public void close() {
		// destructor when done with class
	}

	private void processQueryResult(ResultMessage msg) {

		// Instantiate and interpolate a grid from the ResultMessage
		// If needsCalculation is True, kick off a simulation (Monitor will stop
		// the simulation at the
		// appropriate time); otherwise send the grid to EarthDisplay to display

		// We should display the results as textual and graphical.
		// Look at the reqs - part of the output for queries is that textual
		// data is required to be displayed
		// for the query (min temp, max temp, mean temp, etc). We need to figure
		// uout how to do this

		// Just need to set -1 if the grid is not in the requested area. We need
		// to just to visually
		// indicate what region they queried

		// We also need to provide a report on the query - including
		// min/max/mean, etc.
		DisplayMessage displayMsg = new DisplayMessage();

		if (msg.needsCalculation() == false) {
			
			// Scenario 1 - no need of calculation, the data from DB is the
			// perfect hit for what the user wants
			// Scenario 2 - calculate - interpolate, the data that the user
			// wants is inbetween
//			model.populateTable(msg);
//			model.interpolateTable(msg);
			
		} else if (msg.needsCalculation() == true) {
			
			// Scenario 3 - calculate - start a new simulation, the data the
			// user wants is beyond the one stored in DB
//			model.simulateFromTable(msg);

		}
		
//		model.setDisplayMsg(displayMsg);
//		
		TableDisplay display = new TableDisplay();
//		display.updateTableData(displayMsg);

	}

	private void generateData() {
		try {
			model.generate();
			Publisher.getInstance().send(new ProduceMessage());
		} catch (InterruptedException e) {
			this.stop();
		}
	}
}
