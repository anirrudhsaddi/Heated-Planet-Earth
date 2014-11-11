package common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import messaging.Publisher;
import messaging.events.ConsumeMessage;
import messaging.events.ProduceMessage;

public class Buffer implements IBuffer {
	
	private BlockingQueue<IGrid> buffer;
	
	private static int size;
	private static Buffer instance = null;
	
	public static Buffer getBuffer() {
		if (instance == null) instance = new Buffer();
		
		return instance;
	}
	
	public void create(int size) {
		
		if (size < 1 || size > Integer.MAX_VALUE) 
			throw new IllegalArgumentException("Invalid size");

		Buffer.size = size;
		buffer = new LinkedBlockingQueue<IGrid>(size);
	}
	
	private Buffer() {
		// do nothing
	}
	
	@Override
	public void add(IGrid grid) throws InterruptedException {
		
		if (grid == null)
			throw new IllegalArgumentException("IGrid is null");
		
		System.out.println("Buffer. Attempting to add IGrid");
		
		// Something that would be smart would to double buffer up and if we can't add now,
		// we attempt to add upon a get trigger
		buffer.offer(grid, 10, TimeUnit.SECONDS);
		
		System.out.println("Buffer. Done adding IGrid");
		
		if (buffer.size() != 0) {
			System.out.println("Buffer. Sending ConsumeMessage");
			Publisher.getInstance().send(new ConsumeMessage());
		}
		
		System.out.println("Buffer. Sending ProduceMessage");
		Publisher.getInstance().send(new ProduceMessage());
	}

	@Override
	public IGrid get() throws InterruptedException {
		if (buffer.isEmpty()) return null;
		System.out.println("Buffer. Attempting to return IGrid");
		return buffer.poll(1, TimeUnit.SECONDS);
	}
	
	@Override
	public int size() {
		return buffer.size();
	}

	@Override
	public int getCapacity() {
		return Buffer.size;
	}

	@Override
	public int getRemainingCapacity() {
		return buffer.remainingCapacity();
	}
}