package tests;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

import common.Constants;
import common.Monitor;

public class TestMonitor {

	@Test
	public void testMonitor() {
		
		Monitor monitor = new Monitor(12);
		
		int timeStep = 2160;	// in minutes
		
		Calendar currentDate = (Calendar) Constants.START_DATE.clone();

		//43829.1 minutes in a month
		float EstimatedCycles = (float) (12 * 43829.1)/timeStep;
		EstimatedCycles++;
		
		System.out.println("estimated cycles " + EstimatedCycles);
		
		
		int monitorResult = 0;
		int counter = 0;
		while(monitorResult != -1){
			System.out.println("Still Working: cycle " + counter + " Monitor Result:" + monitorResult);
			monitor.notifyCurrentInterval(currentDate.getTimeInMillis());
			currentDate.add(Calendar.MINUTE, timeStep);
			assertTrue (counter <= EstimatedCycles);
			counter++;
		}
		System.out.println("Finished!");
	}

}
