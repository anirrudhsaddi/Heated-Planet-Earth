package common;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import messaging.Message;
import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.PauseMessage;
import messaging.events.ResumeMessage;
import messaging.events.StartMessage;
import messaging.events.StopMessage;

public abstract class ComponentBase implements MessageListener, Callable<Boolean> {

	private final ConcurrentLinkedQueue<Message> msgQueue = new ConcurrentLinkedQueue<Message>();
	
	protected AtomicBoolean stopped, paused;
	
	public ComponentBase() {
		
		paused.set(false);
		stopped.set(false);
		
		Publisher publisher = Publisher.getInstance();
		publisher.subscribe(StartMessage.class, this);
		publisher.subscribe(StopMessage.class, this);
		publisher.subscribe(PauseMessage.class, this);
		publisher.subscribe(ResumeMessage.class, this);

	}

	public void onMessage(Message msg) {
		
		// TODO clear queue on stop
		if (msg instanceof StopMessage) {
			this.msgQueue.clear();
			this.stop();
		} else if (msg instanceof StartMessage) {
			if (!this.msgQueue.isEmpty()) this.msgQueue.add(msg);
		} else if (msg instanceof PauseMessage) {
			this.pause();
		} else if (msg instanceof ResumeMessage) {
			this.resume();
		} else {
			// enque message to be processed later
			this.msgQueue.add(msg);
		}
	}

	public void process() {

		if (msgQueue.isEmpty()) return;

		performAction(msgQueue.poll());
	}

	@Override
	public Boolean call() {

		while (!Thread.currentThread().isInterrupted() && !stopped.get()) {
			// TODO try to use wait/notify
			if(!paused.get()) process();
		}
		return true; 
	}

	// Used to pause a component
	protected void pause() {
		this.paused.compareAndSet(false, true);
	}
	
	protected void stop() {
		this.stopped.compareAndSet(false, true);
	}
	
	protected void resume() {
		this.paused.compareAndSet(true, false);
	}
	
	// This method dispatches a message to the appropriate processor
	protected abstract void performAction(Message msg);
}