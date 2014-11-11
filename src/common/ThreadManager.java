package common;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import messaging.Message;
import messaging.MessageListener;
import messaging.events.StopMessage;

public class ThreadManager extends ThreadPoolExecutor implements IThreadController, MessageListener {
	
	private static final int CORE_POOL_SIZE = 0;
	private static final int MAX_POOL_SIZE = Integer.MAX_VALUE;
	private static final long KEEP_ALIVE = 60L;

	public ThreadManager() {
		
		// Create a Cached Threaded Pool
		super(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
	public void add(Runnable r) {
		this.execute(r);
	}
	
	public void stop() {
		this.shutdown();
	}

	@Override
	public void onMessage(Message msg) {
		
		if (msg instanceof StopMessage) {
			this.stop();
		}
	}
}
