package common;

import java.util.Calendar;

import messaging.Message;
import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.StartMessage;
import messaging.events.StopMessage;

public class Monitor implements IMonitorCallback, MessageListener{
	
	IMonitorCallback callback;
	Calendar endDate;
	Publisher publisher;
	Calendar currentTime;
	
	public Monitor(){
		
		Publisher publisher = Publisher.getInstance();
		publisher.subscribe(StartMessage.class, this);
	}
	
	public void onMessage(Message msg) {
		
		if (msg instanceof StartMessage) {
			StartMessage startMsg = (StartMessage) msg;
			
			// TODO convert to long representation since epoch of the end date (using Calendar)
			this.currentTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			this.currentTime.add(Calendar.DAY_OF_YEAR, 3);
			
			this.endDate = startMsg.endDate();
		}
	}

	@Override
	public void notifyCurrentInterval(long date, long time) {
		
		this.currentTime.setTimeInMillis(date + time);

		if(this.currentTime.after(this.endDate)){
			publisher.send(new StopMessage());
		}
	}
}
