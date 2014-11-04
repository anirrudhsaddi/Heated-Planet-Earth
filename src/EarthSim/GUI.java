// GUI.java
package EarthSim;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import messaging.Publisher;
import messaging.events.PauseMessage;
import messaging.events.ResumeMessage;
import messaging.events.StartMessage;
import messaging.events.StopMessage;

import common.State;

public class GUI extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6146431536208036768L;
	
	private EarthSimEngine engine;
	private final Publisher publisher;
	
	private HashMap<String, JTextField> inputs = new HashMap<String, JTextField>();

	public GUI(boolean ownSimThread, boolean ownPresThread, State initiative, long bufferSize) {
		
		// BW - still need to think on the best way to do this. But that's only when
		// we have the papers done...
		this.engine = new EarthSimEngine(initiative, ownSimThread, ownPresThread, (int) bufferSize);
		this.publisher = Publisher.getInstance();

		setupWindow();
		pack();
	}

	private void setupWindow() {
		
		// setup overall app ui
		setTitle("Heated Earth Diffusion Simulation");
		
		setSize(300, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
		
		add(settingsAndControls(), BorderLayout.CENTER);
	}

	private JPanel settingsAndControls() {
		
		JPanel sncPanel = new JPanel();
		sncPanel.setLayout(new BoxLayout(sncPanel, BoxLayout.PAGE_AXIS));
		sncPanel.setAlignmentY(Component.TOP_ALIGNMENT);

		sncPanel.add(settings(), BorderLayout.WEST);
		sncPanel.add(runControls(), BorderLayout.WEST);

		return sncPanel;
	}

	private JPanel settings() {
		
		JPanel settingsPanel = new JPanel();
		settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
		settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));
		settingsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		
		settingsPanel.add(inputField("Grid Spacing"));
		settingsPanel.add(inputField("Simulation Time Step"));
		settingsPanel.add(inputField("Presentation Rate"));

		return settingsPanel;
	}

	private JPanel runControls() {
		
		JPanel ctrlsPanel = new JPanel(new FlowLayout());
		ctrlsPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

		ctrlsPanel.add(button("Start"));
		ctrlsPanel.add(button("Pause"));
		ctrlsPanel.add(button("Resume"));
		ctrlsPanel.add(button("Stop"));

		return ctrlsPanel;
	}

	private JPanel inputField(String name) {
		
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new FlowLayout());
		inputPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

		JLabel l = new JLabel(name);
		l.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputPanel.add(l);

		JTextField t = new JTextField("", 10);
		t.setAlignmentX(Component.RIGHT_ALIGNMENT);
		l.setLabelFor(t);
		inputPanel.add(t);

		inputs.put(name, t);
		return inputPanel;
	}

	private JButton button(String name) {
		
		JButton button = new JButton(name);
		button.setActionCommand(name);
		button.addActionListener(this);
		return button;
	}

	public void actionPerformed(ActionEvent e) {
		
		String cmd = e.getActionCommand();
		
		if ("Start".equals(cmd)) {
			if (configureEngine())
				this.start();
		}
		
		else if ("Pause".equals(cmd))
			this.pause();
		
		else if ("Resume".equals(cmd))
			this.resume();
		
		else if ("Stop".equals(cmd)) 
			this.stop();
	}
	
	public void start() {
		publisher.send(new StopMessage());
		publisher.send(new StartMessage());
	}
	
	public void stop() {
		publisher.send(new StopMessage());	
	}
	
	public void pause() {
		publisher.send(new PauseMessage());
	}
	
	public void resume() {
		publisher.send(new ResumeMessage());
	}

	private boolean configureEngine() {
		
		try {
			
			int gs = Integer.parseInt(inputs.get("Grid Spacing").getText());
			int timeStep = Integer.parseInt(inputs.get("Simulation Time Step").getText());
			long presentationRate = Long.parseLong(inputs.get("Presentation Rate").getText());

			engine.configure(gs, timeStep, presentationRate);
			return true;

		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null,
					"Please correct input. All fields need numbers");
		}
		
		return false;
	}
}
