// GUI.java
package EarthSim;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
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
import messaging.events.ProduceMessage;
import messaging.events.ResumeMessage;
import messaging.events.StartMessage;
import messaging.events.StopMessage;
import simulation.EarthEngine;
import view.EarthDisplayEngine;

import common.Buffer;
import common.Constants;
import common.Monitor;
import common.ThreadManager;


public class ControlGUI extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6146431536208036768L;
	
	private HashMap<String, JTextField> inputs = new HashMap<String, JTextField>();
	private HashMap<String, JButton> buttons = new HashMap<String, JButton>();

	private ThreadManager threadManager = ThreadManager.getManager();
	
	private final int precision;
	private final int geoAccuracy;
	private final int temporalAccuracy;
	
	public ControlGUI(int precision, int geoAccuracy, int temporalAccuracy) {
		
		if (precision < Constants.PRECISION_MIN || precision > Constants.PRECISION_MAX)
			throw new IllegalArgumentException("Invalid precision provided");
			
		if (geoAccuracy < Constants.GEOACCURACY_MIN || geoAccuracy > Constants.GEOACCURACY_MAX)
			throw new IllegalArgumentException("Invalid geoAccuracy provided");
			
		if (temporalAccuracy < Constants.TEMPORALACCURACY_MIN || temporalAccuracy > Constants.TEMPORALACCURACY_MAX)
			throw new IllegalArgumentException("Invalid temporalAccuracy provided");
		
		throw new IllegalStateException("The TODOs in here need to be finished, including Demo's params");
		
		// this.precision = precision;
		// this.geoAccuracy = geoAccuracy;
		// this.temporalAccuracy = temporalAccuracy;
		
		// START_DATE is epoch UTC (01/01/1970). Add 3 days to make it 01/04/1970 
		// Constants.START_DATE.add(Calendar.DAY_OF_YEAR, 3);

		// make widgets
		// setupWindow();
		// pack();
	}

	private void setupWindow() {
		
		// setup overall app ui
		setTitle("Heated Earth Diffusion Simulation");
		
		setSize(300, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);

		lowerRightWindow(); // Set window location to lower right (so we don't hide dialogs)
		setAlwaysOnTop(true);
		
		add(settingsAndControls(), BorderLayout.CENTER);
	}
	
	private void lowerRightWindow() {
		
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) (dimension.getWidth() - this.getWidth());
	    int y = (int) (dimension.getHeight() - this.getHeight());
	    this.setLocation(x, y);
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
		
		settingsPanel.add(inputField("Grid Spacing", Integer.toString(Constants.DEFAULT_GRID_SPACING)));
		settingsPanel.add(inputField("Simulation Time Step",Integer.toString(Constants.DEFAULT_TIME_STEP)));
		settingsPanel.add(inputField("Presentation Rate",Float.toString(Constants.DEFAULT_PRESENTATION)));
		settingsPanel.add(inputField("Simulation Length", Integer.toString(Constants.DEFAULT_SIM_LEN)));
		settingsPanel.add(inputField("Axis Tilt",Float.toString(Constants.DEFAULT_AXIS_TILT)));
		settingsPanel.add(inputField("Orbital Eccentricity",Float.toString(Constants.DEFAULT_ECCENTRICITY)));
		
		return settingsPanel;
	}

	private JPanel runControls() {
		
		JPanel ctrlsPanel = new JPanel(new FlowLayout());
		ctrlsPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

		ctrlsPanel.add(button("Start"));
		ctrlsPanel.add(button("Pause"));
		ctrlsPanel.add(button("Resume"));
		ctrlsPanel.add(button("Stop"));

		buttons.get("Start").setEnabled(true);
		buttons.get("Pause").setEnabled(false);
		buttons.get("Resume").setEnabled(false);
		buttons.get("Stop").setEnabled(false);
		
		return ctrlsPanel;
	}

	private JPanel inputField(String name, String defaultText) {
		
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new FlowLayout());
		inputPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

		JLabel l = new JLabel(name);
		l.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputPanel.add(l);

		JTextField t = new JTextField(defaultText, 10);
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
		buttons.put(name, button);
		return button;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String cmd = e.getActionCommand();
		
		// TODO open new tab
		if ("Start".equals(cmd)) {
			try {
				
				// TODO check for stop and reset?
				// TODO All simulations need to start at Jan 4th (epoch)
				
				final int gs = Integer.parseInt(inputs.get("Grid Spacing").getText());
				final int timeStep = Integer.parseInt(inputs.get("Simulation Time Step").getText());
				final float presentationRate = Float.parseFloat(inputs.get("Presentation Rate").getText());
				final int simulationLength = Integer.parseInt(inputs.get("Simulation Length").getText());
				final float axisTilt = Float.parseFloat(inputs.get("Axis Tilt").getText());
				final float eccentricity = Float.parseFloat(inputs.get("Orbital Eccentricity").getText());
				
				if (gs < Constants.MIN_GRID_SPACING || gs > Constants.MAX_GRID_SPACING)
					throw new IllegalArgumentException("Invalid grid spacing");

				if (timeStep < Constants.MIN_TIME_STEP || timeStep > Constants.MAX_TIME_STEP)
					throw new IllegalArgumentException("Invalid time step");

				if (simulationLength < Constants.MIN_SIM_LEN || simulationLength > Constants.MAX_SIM_LEN)
					throw new IllegalArgumentException("Invalid simulation length");

				if (presentationRate < Constants.MIN_PRESENTATION || presentationRate > Constants.MAX_PRESENTATION)
					throw new IllegalArgumentException("Invalid presentation interval");
				
				if (axisTilt < Constants.MIN_AXIS_TILT || axisTilt > Constants.MAX_AXIS_TILT)
					throw new IllegalArgumentException("Invalid axisTilt value");
				
				if (eccentricity < Constants.MIN_ECCENTRICITY || eccentricity > Constants.MAX_ECCENTRICITY)
					throw new IllegalArgumentException("Invalid eccentricity value");
				
				// Create and reset the buffer
				Buffer.getBuffer().create(Constants.DEFAULT_BUFFFER_SIZE);
				
				//threadManager.add(new SimulationDAO(new SimulationNeo4j()));
				
				// TODO set name
				// TODO check name against the DAO
				String simulationName = "";
				
				threadManager.execute(new EarthEngine(this.precision, this.geoAccuracy, this.temporalAccuracy, new Monitor()));
				threadManager.execute(new EarthDisplayEngine());
				
				Publisher.getInstance().send(new StartMessage(simulationName, gs, timeStep, presentationRate, simulationLength, axisTilt, eccentricity));
				Publisher.getInstance().send(new ProduceMessage());
				
				// do gui stuff to indicate start has occurred.
				buttons.get("Start").setEnabled(false);
				buttons.get("Pause").setEnabled(true);
				buttons.get("Resume").setEnabled(false);
				buttons.get("Stop").setEnabled(true);
				
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "Please correct input. All fields need numbers");
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(null, "Please correct input. All fields need numbers");
			}
		} else if ("Pause".equals(cmd)) {
			
			Publisher.getInstance().send(new PauseMessage());
			
			buttons.get("Pause").setEnabled(false);
			buttons.get("Resume").setEnabled(true);
		} else if ("Resume".equals(cmd)) {
			
			Publisher.getInstance().send(new ResumeMessage());
			
			buttons.get("Pause").setEnabled(true);
			buttons.get("Resume").setEnabled(false);
			
		} else if ("Stop".equals(cmd)) {
			
			Publisher.getInstance().send(new StopMessage());
			
			buttons.get("Start").setEnabled(true);
			buttons.get("Pause").setEnabled(false);
			buttons.get("Resume").setEnabled(false);
			buttons.get("Stop").setEnabled(false);
		}
	}
}
