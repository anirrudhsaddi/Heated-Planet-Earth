// GUI.java
package EarthSim;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import messaging.Publisher;
import messaging.events.PauseMessage;
import messaging.events.ResumeMessage;
import messaging.events.StartMessage;
import messaging.events.StopMessage;
import simulation.EarthEngine;
import view.EarthDisplayEngine;
import common.Buffer;
import common.ThreadManager;
import EarthSim.widgets.ControlWidget;
import EarthSim.widgets.QueryWidget;
import EarthSim.widgets.SettingsWidget;


public class ControlGUI extends JFrame implements ActionListener{


    QueryWidget queryWidget;


    public ControlGUI() {

	// make widgets
	setupWindow();
	pack();
    }

    private void setupWindow() {

	// setup overall app ui
	setTitle("Heated Planet Diffusion Simulation");
	
	setSize(700, 400);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	getContentPane().setLayout(new BorderLayout());
	setLocationRelativeTo(null);

	lowerRightWindow(); // Set window location to lower right (so we don't hide dialogs)
	setAlwaysOnTop(true);

	getContentPane().add(settingsAndControls(), BorderLayout.WEST);
	getContentPane().add(query(), BorderLayout.CENTER);
    }

    private void lowerRightWindow() {
	Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	int x = (int) (dimension.getWidth() - this.getWidth());
	int y = (int) (dimension.getHeight() - this.getHeight());
	this.setLocation(x, y);
    }


	/**
	 * 
	 */
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
	
	//private HashMap<String, JTextField> inputs = new HashMap<String, JTextField>();
	ControlWidget controlWidget;
	SettingsWidget settingsWidget;
	private ThreadManager threadManager = new ThreadManager();

	
    

    private JPanel query(){

	JPanel queryPanel = new JPanel();
	queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.PAGE_AXIS));
	queryPanel.setAlignmentY(Component.TOP_ALIGNMENT);
	
	queryWidget = new QueryWidget();
	
	queryPanel.add(queryWidget,BorderLayout.CENTER);
	return queryPanel;
    }
	//queryPanel.add()
	private JPanel settingsAndControls() {
		
		JPanel sncPanel = new JPanel();
		sncPanel.setLayout(new BoxLayout(sncPanel, BoxLayout.PAGE_AXIS));
		sncPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		
		controlWidget = new ControlWidget(this);
		settingsWidget = new SettingsWidget();
		sncPanel.add(settingsWidget, BorderLayout.WEST);
		sncPanel.add(controlWidget, BorderLayout.WEST);

		return sncPanel;
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
				
				if (settingsWidget.GetDisplayAnimationStatus()){
					threadManager.add(new EarthDisplayEngine());
				}
				
				Publisher.getInstance().send(new StartMessage(gs, timeStep, presentationRate, simulationLength, axisTilt, eccentricity));
				
				//do gui stuff to indicate start has occurred.
				controlWidget.disableButtonsBasedOnAction("Start");
				queryWidget.setFields("Disable");
				
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "Please correct input. All fields need numbers");
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(null, "Please correct input. All fields need numbers");
			}
		} else if ("Pause".equals(cmd)) {
			
			Publisher.getInstance().send(new PauseMessage());
			
			controlWidget.disableButtonsBasedOnAction("Pause");
		} else if ("Resume".equals(cmd)) {
			
			Publisher.getInstance().send(new ResumeMessage());
			
			controlWidget.disableButtonsBasedOnAction("Resume");
			
		} else if ("Stop".equals(cmd)) {
			
			Publisher.getInstance().send(new StopMessage());
			
			controlWidget.disableButtonsBasedOnAction("Stop");
			queryWidget.setFields("Enable");
		}
	}
}
