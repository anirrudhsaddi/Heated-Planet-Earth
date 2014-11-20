package common;

import java.util.Calendar;

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
			
			// TODO get time limit here too from StartMessage (add parameter - set to 0 in normal "Start" button action)
			this.simulationLength = startMsg.simulationLength();
		}
	}

	@Override
	public void notifyCurrentInterval(long dateTime) {
		
		// dateTime will be the millisecond representation of the current date and time from Jan. 4th, 1970 (epoch) - Constants.START_DATE is our start date. If you use 
		// that object, call .clone() on it first
		
		// From here you can generate a Calendar object and use it to check the current length from START_DATE in terms of months. If it's longer than simulationLength,
		// you can also check the current time. Since the startTime passed in from StartMessage will be 0 on normal simulations, and START_MESSAGE starts at midnight,
		// this should also cover the need for checking time with Queries without using special logic.
		
//		if(this.currentTimeInSimulation >= simulationLength){
//			publisher.send(new StopMessage());
//		}
	}
}
