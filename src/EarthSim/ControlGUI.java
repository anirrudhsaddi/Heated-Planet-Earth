// GUI.java
package EarthSim;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import common.Buffer;
import common.Controller;
import common.ThreadManager;
import simulation.EarthEngine;
import view.EarthDisplayEngine;
import messaging.Publisher;
import messaging.events.PauseMessage;
import messaging.events.ResumeMessage;
import messaging.events.StartMessage;
import messaging.events.StopMessage;


public class ControlGUI extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6146431536208036768L;
	
	private static final int DEFAULT_BUFFFER_SIZE	= 10;
	
	private static final int MIN_GRID_SPACING 		= 1;
	private static final int DEFAULT_GRID_SPACING  	= 15;
	private static final int MAX_GRID_SPACING 		= 180;
	
	private static final int MIN_TIME_STEP 			= 1;
	private static final int DEFAULT_TIME_STEP		= 1440;
	private static final int MAX_TIME_STEP			= 525600;
	
	private static final int MIN_SIM_LEN			= 1;
	private static final int DEFAULT_SIM_LEN		= 12;
	private static final int MAX_SIM_LEN			= 1200;
	
	private static final float MIN_PRESENTATION		= 1f;
	private static final float DEFAULT_PRESENTATION	= 1f;
	private static final float MAX_PRESENTATION		= Float.MAX_VALUE;
	
	private final static float MIN_AXIS_TILT 		= -180.0f;
	private final static float DEFAULT_AXIS_TILT	= 23.44f;
	private final static float MAX_AXIS_TILT 		= 180f;
	
	private static final float MIN_ECCENTRICITY 	= 0f;
	private static final float DEFAULT_ECCENTRICITY	= 0.0167f;
	private static final float MAX_ECCENTRICITY 	= 1.0f;
	
	private static final Calendar START_DATE		= Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	
	private HashMap<String, JTextField> inputs = new HashMap<String, JTextField>();
	private HashMap<String, JButton> buttons = new HashMap<String, JButton>();

	private ThreadManager threadManager = ThreadManager.getManager();
	
	public ControlGUI() {
		
		// START_DATE is epoch UTC (01/01/1970). Add 3 days to make it 01/04/1970 
		START_DATE.add(Calendar.DAY_OF_YEAR, 3);

		// make widgets
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
		
		settingsPanel.add(inputField("Grid Spacing", Integer.toString(DEFAULT_GRID_SPACING)));
		settingsPanel.add(inputField("Simulation Time Step",Integer.toString(DEFAULT_TIME_STEP)));
		settingsPanel.add(inputField("Presentation Rate",Float.toString(DEFAULT_PRESENTATION)));
		settingsPanel.add(inputField("Simulation Length", Integer.toString(DEFAULT_SIM_LEN)));
		settingsPanel.add(inputField("Axis Tilt",Float.toString(DEFAULT_AXIS_TILT)));
		settingsPanel.add(inputField("Orbital Eccentricity",Float.toString(DEFAULT_ECCENTRICITY)));
		
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
				
				if (gs < MIN_GRID_SPACING || gs > MAX_GRID_SPACING)
					throw new IllegalArgumentException("Invalid grid spacing");

				if (timeStep < MIN_TIME_STEP || timeStep > MAX_TIME_STEP)
					throw new IllegalArgumentException("Invalid time step");

				if (simulationLength < MIN_SIM_LEN || simulationLength > MAX_SIM_LEN)
					throw new IllegalArgumentException("Invalid simulation length");

				if (presentationRate < MIN_PRESENTATION || presentationRate > MAX_PRESENTATION)
					throw new IllegalArgumentException("Invalid presentation interval");
				
				if (axisTilt < MIN_AXIS_TILT || axisTilt > MAX_AXIS_TILT)
					throw new IllegalArgumentException("Invalid axisTilt value");
				
				if (eccentricity < MIN_ECCENTRICITY || eccentricity > MAX_ECCENTRICITY)
					throw new IllegalArgumentException("Invalid eccentricity value");
				
				// TODO clear rather than create?
				// Create the buffer
				Buffer.getBuffer().create(DEFAULT_BUFFFER_SIZE);
				
				// TODO set name?
				//threadManager.add(new SimulationDAO(new SimulationNeo4j()));
				threadManager.execute(new ControlEngine());
				threadManager.execute(new EarthEngine());
				threadManager.execute(new EarthDisplayEngine());
				
				Publisher.getInstance().send(new StartMessage(gs, timeStep, presentationRate, simulationLength, axisTilt, eccentricity));
				
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
