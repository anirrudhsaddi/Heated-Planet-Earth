package common;

import messaging.MessageListener;

public interface IBuffer extends MessageListener {
	
	public void add(IGrid grid) throws InterruptedException;

	public IGrid get() throws InterruptedException;
	
	public int size();
	
	public int getCapacity();
	
	public int getRemainingCapacity();

}