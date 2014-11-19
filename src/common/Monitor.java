package common;

import messaging.Message;
import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.StartMessage;
import messaging.events.StopMessage;

public class Monitor implements IMonitorCallback, MessageListener{
	
	IMonitorCallback callback;
	int currentTimeInSimulation;
	int simulationLength;	// in months
	int timeStep;			// in minutes
	
	Publisher publisher;
	
	public Monitor(){
		
		Publisher publisher = Publisher.getInstance();
		publisher.subscribe(StartMessage.class, this);
	}
	
	public void onMessage(Message msg) {
		
		if (msg instanceof StartMessage) {
			StartMessage startMsg = (StartMessage) msg;
			
			// TODO convert to long representation since epoch of the end date (using Calendar)
			this.simulationLength = startMsg.simulationLength();
			this.timeStep = startMsg.timeStep();
			
			this.currentTimeInSimulation = 0;
		}
	}

	@Override
	public void notifyCurrentInterval(long date, long time) {
		
		
		//Todo: talk to Brandon
	/*	this.currentTimeInSimulation = currSimulationInterval * this.timeStep;
		
		if(this.currentTimeInSimulation >= simulationLength){
			publisher.send(new StopMessage());
			}*/
		
	}
}
