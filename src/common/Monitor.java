package common;

import java.util.Calendar;

import messaging.Publisher;
import messaging.events.StopMessage;

public class Monitor implements IMonitorCallback {
	
	private Calendar currentTimeInSimulation;
	private Calendar endDate = (Calendar) Constants.START_DATE.clone();
	
	public Monitor(int simulationLength) {
		endDate.add(Calendar.MONTH, simulationLength);
	}
	
	public Monitor(int simulationLength, int endTime){
		endDate.add(Calendar.MONTH, simulationLength);
		endDate.add(Calendar.MILLISECOND, endTime);
	}

	@Override
	public void notifyCurrentInterval(long dateTime) {
		
		// dateTime will be the millisecond representation of the current date and time from Jan. 4th, 1970 (epoch) - Constants.START_DATE is our start date. If you use 
		// that object, call .clone() on it first
		
		// From here you can generate a Calendar object and use it to check the current length from START_DATE in terms of months. If it's longer than simulationLength,
		// you can also check the current time. Since the startTime passed in from StartMessage will be 0 on normal simulations, and START_MESSAGE starts at midnight,
		// this should also cover the need for checking time with Queries without using special logic.
		
		this.currentTimeInSimulation = (Calendar) Constants.START_DATE.clone();
		this.currentTimeInSimulation.setTimeInMillis(dateTime);

		// Test
//		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy ");//HH:mm:ss
//		String strdate = sdf.format(this.currentTimeInSimulation.getTime());
//		System.out.println("Current Time " + strdate);
//		strdate = sdf.format(endDate.getTime());
//		System.out.println("End Time " + strdate + "\n");
		//end test
		
		if (this.currentTimeInSimulation.after(endDate)){
			Publisher.getInstance().send(new StopMessage());
		}
	}
}
