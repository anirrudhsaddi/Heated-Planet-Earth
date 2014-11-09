package common;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import messaging.Message;
import messaging.MessageListener;
import messaging.events.StartMessage;
import messaging.events.StopMessage;

public class ThreadManager extends ThreadPoolExecutor implements IThreadController, MessageListener {
	
	private static final int CORE_POOL_SIZE = 0;
	private static final int MAX_POOL_SIZE = Integer.MAX_VALUE;
	private static final long KEEP_ALIVE = 60L;
	
	private final Queue<Callable<?>> queued;
	private final List<Future<?>> jobs;

	public ThreadManager() {
		
		// Create a Cached Threaded Pool
		super(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		
		queued = new LinkedList<Callable<?>>();
		jobs = new LinkedList<Future<?>>();
	}
	
	public void add(Callable<?> c) {
		if (!queued.contains(c))
			queued.add(c);
	}

	public void start() {
		
		for (Callable<?> c : queued) {
			jobs.add(this.submit(c));
		}
	}
	
	public void stop() {
		this.shutdown();
		
		try {
			this.awaitTermination(5L, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			this.shutdownNow();
		}
	}

	@Override
	public void onMessage(Message msg) {
		
		if (msg instanceof StartMessage)
			this.start();
		if (msg instanceof StopMessage) {
			this.stop();
		}
	}
}
