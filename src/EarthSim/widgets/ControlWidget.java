package EarthSim.widgets;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import messaging.Publisher;
import messaging.events.PauseMessage;
import messaging.events.ResumeMessage;
import messaging.events.StartMessage;
import messaging.events.StopMessage;
import simulation.EarthEngine;
import view.EarthDisplayEngine;
import EarthSim.ControlEngine;
import common.Buffer;
import common.ThreadManager;

public class ControlWidget extends JPanel implements ActionListener{
private static final long serialVersionUID = 6146431536208036768L;
	
	private static final int DEFAULT_BUFFFER_SIZE	= 10;
	
	private static final int MIN_GRID_SPACING 		= 1;
	private static final int MAX_GRID_SPACING 		= 180;
	
	private static final int MIN_TIME_STEP 			= 1;
	private static final int MAX_TIME_STEP			= 525600;
	
	private static final int MIN_SIM_LEN			= 1;
	private static final int MAX_SIM_LEN			= 1200;
	
	private static final float MIN_PRESENTATION		= 1f;
	private static final float MAX_PRESENTATION		= Float.MAX_VALUE;
	
	private final static float MIN_AXIS_TILT 		= -180.0f;
	private final static float MAX_AXIS_TILT 		= 180f;
	
	private static final float MIN_ECCENTRICITY 	= 0f;
	private static final float MAX_ECCENTRICITY 	= 1.0f;
	
	private HashMap<String, JButton> buttons = new HashMap<String, JButton>();
	private ThreadManager threadManager = new ThreadManager();
	private SettingsWidget settingsWidget;
	
	public ControlWidget() {
		//this = new JPanel(new FlowLayout());
		setAlignmentX(Component.RIGHT_ALIGNMENT);

		add(button("Start"));
		add(button("Pause"));
		add(button("Resume"));
		add(button("Stop"));

		buttons.get("Start").setEnabled(true);
		buttons.get("Pause").setEnabled(false);
		buttons.get("Resume").setEnabled(false);
		buttons.get("Stop").setEnabled(false);
		
		settingsWidget = new SettingsWidget();
	}
	
	private JButton button(String name) {
		
		JButton button = new JButton(name);
		button.setActionCommand(name);
		button.addActionListener(this);
		buttons.put(name, button);
		return button;
	}
	
	public SettingsWidget getSettingsWidget(){
		return settingsWidget;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String cmd = e.getActionCommand();
		
		// TODO open new tab
		if ("Start".equals(cmd)) {
			try {
				final int gs = Integer.parseInt(settingsWidget.GetInputText("Grid Spacing"));
				final int timeStep = Integer.parseInt(settingsWidget.GetInputText("Simulation Time Step"));
				final float presentationRate = Float.parseFloat(settingsWidget.GetInputText("Presentation Rate"));
				final int simulationLength = Integer.parseInt(settingsWidget.GetInputText("Simulation Length"));
				final float axisTilt = Float.parseFloat(settingsWidget.GetInputText("Axis Tilt"));
				final float eccentricity = Float.parseFloat(settingsWidget.GetInputText("Orbital Eccentricity"));
				
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
				
				// Create the buffer
				Buffer.getBuffer().create(DEFAULT_BUFFFER_SIZE);
				
				//threadManager.add(new Controller());
				threadManager.add(new ControlEngine());
				threadManager.add(new EarthEngine());
				threadManager.add(new EarthDisplayEngine());
				
				Publisher.getInstance().send(new StartMessage(gs, timeStep, presentationRate, simulationLength, axisTilt, eccentricity));
				
				//do gui stuff to indicate start has occurred.
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
