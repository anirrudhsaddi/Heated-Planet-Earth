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

import org.apache.james.mime4j.field.datetime.DateTime;

import messaging.Publisher;
import messaging.events.PauseMessage;
import messaging.events.ProduceMessage;
import messaging.events.ResumeMessage;
import messaging.events.StartMessage;
import messaging.events.StopMessage;
import simulation.EarthEngine;
import view.EarthDisplayEngine;
import EarthSim.widgets.ControlWidget;
import EarthSim.widgets.QueryWidget;
import EarthSim.widgets.SettingsWidget;
import common.Buffer;
import common.Constants;
import common.Monitor;
import common.ThreadManager;

public class ControlGUI extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6146431536208036768L;

	private ThreadManager		threadManager		= ThreadManager.getManager();


	private  QueryWidget				queryWidget;
	private  ControlWidget				controlWidget;
	private  SettingsWidget				settingsWidget;
	private QueryEngine					queryEngine;

	private final int				precision;
	private final int				geoAccuracy;
	private final int				temporalAccuracy;

	public ControlGUI(int precision, int geoAccuracy, int temporalAccuracy) {

		if (precision < Constants.PRECISION_MIN || precision > Constants.PRECISION_MAX)
			throw new IllegalArgumentException("Invalid precision provided");

		if (geoAccuracy < Constants.GEOACCURACY_MIN || geoAccuracy > Constants.GEOACCURACY_MAX)
			throw new IllegalArgumentException("Invalid geoAccuracy provided");

		if (temporalAccuracy < Constants.TEMPORALACCURACY_MIN || temporalAccuracy > Constants.TEMPORALACCURACY_MAX)
			throw new IllegalArgumentException("Invalid temporalAccuracy provided");

	//	throw new IllegalStateException("The TODOs in here need to be finished, including Demo's params");

		 this.precision = precision;
		 this.geoAccuracy = geoAccuracy;
		 this.temporalAccuracy = temporalAccuracy;

		// START_DATE is epoch UTC (01/01/1970). Add 3 days to make it
		// 01/04/1970
		// Constants.START_DATE.add(Calendar.DAY_OF_YEAR, 3);

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

		lowerRightWindow(); // Set window location to lower right (so we don't
							// hide dialogs)
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

	private JPanel query() {

		JPanel queryPanel = new JPanel();
		queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.PAGE_AXIS));
		queryPanel.setAlignmentY(Component.TOP_ALIGNMENT);

		queryWidget = new QueryWidget();

		queryPanel.add(queryWidget, BorderLayout.CENTER);
		return queryPanel;
	}

	// queryPanel.add()
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

		// TODO open new tab?
		if ("Start".equals(cmd)) {
			try {

				// TODO check for stop and reset?
				// TODO All simulations need to start at Jan 4th (epoch)


				final int gs = Integer.parseInt(settingsWidget.GetInputText("Grid Spacing"));
				final int timeStep = Integer.parseInt(settingsWidget.GetInputText("Simulation Time Step"));
				final float presentationRate = Float.parseFloat(settingsWidget.GetInputText("Presentation Rate"));
				final int simulationLength = Integer.parseInt(settingsWidget.GetInputText("Simulation Length"));
				final float axisTilt = Float.parseFloat(settingsWidget.GetInputText("Axis Tilt"));
				final float eccentricity = Float.parseFloat(settingsWidget.GetInputText("Orbital Eccentricity"));
				
				final String	simName = queryWidget.GetUserInputs("Simulation Name");
				final DateTime	startTime = null;
				final DateTime	endTime = null;
				final double	wLat =  Double.parseDouble(queryWidget.GetUserInputs("West Longitude"));
				final double	eLat =  Double.parseDouble(queryWidget.GetUserInputs("East Longitude"));
				final double	sLat =  Double.parseDouble(queryWidget.GetUserInputs("South Latitude"));
				final double	nLat =  Double.parseDouble(queryWidget.GetUserInputs("North Latitude")); 

				if (gs < Constants.MIN_GRID_SPACING || gs > Constants.MAX_GRID_SPACING)
					throw new IllegalArgumentException("Invalid grid spacing");

				// We'll let the user provide Time Steps in base 2 intervals starting from 1
				if (timeStep < Constants.MIN_TIME_STEP || timeStep > Constants.MAX_TIME_STEP || (simulationLength != 1 && simulationLength % 2 != 0))
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

				// threadManager.add(new SimulationDAO(new SimulationNeo4j()));
				
				

				// TODO set name
				// TODO check name against the DAO
				
				queryEngine = new QueryEngine();
				queryEngine.getQueryValues(simName, axisTilt, eccentricity, startTime, endTime, wLat, eLat, sLat, nLat );

				threadManager.execute(new EarthEngine(new Monitor()));
				threadManager.execute(new EarthDisplayEngine());
				

				Boolean animate = settingsWidget.GetDisplayAnimationStatus();
				
				// TODO There has GOT to be a more elegant way of transporting start values...we want to keep it decoupled, so using the messages was good. But at this point, would
				// using the constructors be better??
				StartMessage msg = new StartMessage(simName, gs, timeStep, presentationRate, simulationLength, axisTilt, eccentricity, this.precision, this.geoAccuracy, this.temporalAccuracy, animate);
				Publisher.getInstance().send(msg);
				Publisher.getInstance().send(new ProduceMessage());

				// do gui stuff to indicate start has occurred.
				controlWidget.disableButtonsBasedOnAction(cmd);
				queryWidget.setFields(false);

			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "Please correct input. All fields need numbers");
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(null, "Please correct input. All fields need numbers");
			}
		} else if ("Pause".equals(cmd)) {

			Publisher.getInstance().send(new PauseMessage());
			controlWidget.disableButtonsBasedOnAction(cmd);

		} else if ("Resume".equals(cmd)) {

			Publisher.getInstance().send(new ResumeMessage());
			controlWidget.disableButtonsBasedOnAction(cmd);

		} else if ("Stop".equals(cmd)) {

			Publisher.getInstance().send(new StopMessage());

			controlWidget.disableButtonsBasedOnAction(cmd);
			queryWidget.setFields(true);
		}
	}
}
