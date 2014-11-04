package common;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import messaging.Message;
import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.PauseMessage;
import messaging.events.ResumeMessage;
import messaging.events.StartMessage;
import messaging.events.StopMessage;
import messaging.events.UpdatedMessage;

public abstract class AbstractEngine implements IEngine, MessageListener {
	
	protected static ReentrantLock pauseLock = new ReentrantLock();
	protected static Condition unpaused = pauseLock.newCondition();

	protected final ConcurrentLinkedQueue<Message> MSG_QUEUE;
	protected final boolean IS_THREADED;
	
	protected boolean stopped = false;
	
	protected final State initiative;
	protected final State self;
	
	public AbstractEngine(final boolean isThreaded, State initiative, State self) {
		this.IS_THREADED = isThreaded;
		this.initiative = initiative;
		this.self = self;
		MSG_QUEUE = new ConcurrentLinkedQueue<Message>();
	}

	public AbstractEngine(State initiative, State self) {
		this(false, initiative, self);
	}

	public void onMessage(Message msg) {
		
		System.out.println("In " + this + ". Recevied msg " + msg);
		
		if (msg instanceof StopMessage) this.stop();
		if (msg instanceof StartMessage) this.start();
		if (msg instanceof PauseMessage) this.pause();
		if (msg instanceof ResumeMessage) this.resume();

		// If threaded, enque message to be processed later
		if (this.IS_THREADED)
			MSG_QUEUE.add(msg);
		else {
			msg.process(this);
		}
	}

	// TODO guard against starvation?
	public void performAction() {

		Message msg;
		if (MSG_QUEUE.isEmpty()) return;
		if ((msg = MSG_QUEUE.poll()) != null) {
			System.out.println(this + "going to process " + msg);
			msg.process(this);
		}
	}

	public void run() {

		while (!Thread.currentThread().isInterrupted() && !this.stopped) {
			// Just loop
			System.out.println( this + " going to perform action");
			this.performAction();
			
			if (initiative == self) {
				System.out.println( this + " done performing action. going to send updated message");
				Publisher.getInstance().send(new UpdatedMessage());
			}
		}
	}

	public void processQueue() {

		ConcurrentLinkedQueue<Message> curr = new ConcurrentLinkedQueue<Message>(MSG_QUEUE);
		while (!curr.isEmpty()) {
			this.performAction();
		}
	}

	// This method dispatches a message to the appropriate processor
	public <T extends Message> void dispatchMessage(T msg) {
		Publisher.getInstance().send(msg);
	}
	
	@Override
	public void generate() {
		return;
	}

	public void stop() {
		Thread.currentThread().interrupt();
		this.stopped = true;
	}
	
	public void pause() {
		pauseLock.lock();
	}
	
	public void resume() {
		pauseLock.lock();
		try {
			unpaused.signalAll();
		} finally {
			pauseLock.unlock();
		}
	}

	@Override
	public void start() {
		return;
	}
}
