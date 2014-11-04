package EarthSim;

import messaging.ContinuouslyConsumeCommand;
import messaging.ContinuouslyProduceCommand;
import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.ConsumeMessage;
import messaging.events.DisplayMessage;
import messaging.events.PauseMessage;
import messaging.events.ProduceMessage;
import messaging.events.ResumeMessage;
import messaging.events.StartMessage;
import messaging.events.StopMessage;
import messaging.events.UpdatedMessage;
import simulation.Earth;
import view.EarthDisplayEngine;
import common.AbstractEngine;
import common.Buffer;
import common.BufferController;
import common.IEngine;
import common.IHandler;
import common.InitiativeHandler;
import common.SimulationHandler;
import common.State;
import common.ViewHandler;
import concurrent.ProcessManager;

public final class EarthSimEngine extends AbstractEngine {
	
	private static final int DEFAULT_BUFFER_SIZE = 1;
	
	private InitiativeHandler handler;
	private ProcessManager manager;
	private Publisher publisher;
	private MessageListener model, view;
	
	private final boolean viewThreaded, simThreaded;
	 
	private long presentationRate;
	
	public EarthSimEngine(State i, boolean simThreaded, boolean viewThreaded, int b) {
		
		super(true, i, State.MASTER);
		
		if (b <= 0) b = DEFAULT_BUFFER_SIZE;
		if (b >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid buffer size");
		
		this.simThreaded = simThreaded;
		this.viewThreaded = viewThreaded;
		
		Buffer.getBuffer().create(b);
		
		model = new Earth(simThreaded, i);
		view = new EarthDisplayEngine(viewThreaded, i);
		
		manager = ProcessManager.getManager();
		
		IHandler plugin;
		
		if (i == State.SIMULATION) {
			plugin = new SimulationHandler(new ContinuouslyProduceCommand());
		} else if (i == State.PRESENTATION) {
			plugin = new ViewHandler(new ContinuouslyConsumeCommand());
		} else {
			plugin = new BufferController();
		}
		
		handler = new InitiativeHandler(plugin);
		
		publisher = Publisher.getInstance();
		
		publisher.subscribe(UpdatedMessage.class, handler);
		publisher.subscribe(StartMessage.class, handler);
		
		publisher = Publisher.getInstance();
		publisher.subscribe(ProduceMessage.class, model);
		publisher.subscribe(ConsumeMessage.class, view);
		publisher.subscribe(DisplayMessage.class, view);
			
		publisher.subscribe(StartMessage.class, model);
		publisher.subscribe(StartMessage.class, manager);
		publisher.subscribe(StartMessage.class, view);
		
		publisher.subscribe(PauseMessage.class, this);
		publisher.subscribe(ResumeMessage.class, this);
		
		publisher.subscribe(StopMessage.class, manager);
			
		if (simThreaded) {
			manager.add((IEngine) model);
			publisher.subscribe(PauseMessage.class, model);
			publisher.subscribe(ResumeMessage.class, model);
		}
		
		if (viewThreaded) {
			manager.add((IEngine) view);
			publisher.subscribe(PauseMessage.class, view);
			publisher.subscribe(ResumeMessage.class, view);
		}
		
		manager.add(this);
		
		this.presentationRate = 0;
	}
	
	// One way we could improve extensibility is to use a message packet to send values
	// Wrapper around base configure to add additional attribute setting
	public void configure(int gs, int timeStep, long presentationRate) {
		
		if (presentationRate <= 0 || presentationRate >= Long.MAX_VALUE)
			throw new IllegalArgumentException("Invalid Presentation Rate value");
		
		if (presentationRate < 1000)
			this.presentationRate =  presentationRate * 1000;
		else
			this.presentationRate = presentationRate;
		
		this.configure(gs, timeStep);
	}
	
	@Override
	public void configure(int gs, int timeStep) {
		
		if (gs <= 0 || gs >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid Grid Spacing value");
		
		if (timeStep <= 0 || timeStep >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid Time Step value");
		
		((IEngine) model).configure(gs, timeStep); //TODO intermediate element?
		((IEngine) view).configure(gs, timeStep);
	}

	@Override
	public void performAction() {
		
		try {
			Thread.currentThread();
			Thread.sleep(this.presentationRate);
		} catch (InterruptedException e) {
			// do nothing
		}
		
		System.out.println("Sending DispayMessage");
		publisher.send(new DisplayMessage());
	}
	
	@Override
	public void run() {

		while (!Thread.currentThread().isInterrupted() && !this.stopped) {
			this.performAction();
			
			if (!simThreaded && !viewThreaded || initiative == self) { 
				System.out.println("Neither are threaded. Going to send UpdatedMessage!");
				Publisher.getInstance().send(new UpdatedMessage());
			}
		}
	}
}