// GUI.java
package PlanetSim;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Calendar;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import messaging.Publisher;
import messaging.events.PauseMessage;
import messaging.events.ProduceMessage;
import messaging.events.ResumeMessage;
import messaging.events.StartMessage;
import messaging.events.StopMessage;
import simulation.EarthEngine;
import view.EarthDisplayEngine;
import PlanetSim.widgets.ControlWidget;
import PlanetSim.widgets.QueryWidget;
import PlanetSim.widgets.SettingsWidget;
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

	private QueryWidget			queryWidget;
	private ControlWidget		controlWidget;
	private SettingsWidget		settingsWidget;
	private QueryEngine			queryEngine;

	private final int			precision;
	private final int			geoAccuracy;
	private final int			temporalAccuracy;
	private boolean				isquery				= false;
	private JPanel				queryPanel			= new JPanel();
	private int					count				= 1;

	public ControlGUI(int precision, int geoAccuracy, int temporalAccuracy) {

		if (precision < Constants.PRECISION_MIN || precision > Constants.PRECISION_MAX)
			throw new IllegalArgumentException("Invalid precision provided");

		if (geoAccuracy < Constants.GEOACCURACY_MIN || geoAccuracy > Constants.GEOACCURACY_MAX)
			throw new IllegalArgumentException("Invalid geoAccuracy provided");

		if (temporalAccuracy < Constants.TEMPORALACCURACY_MIN || temporalAccuracy > Constants.TEMPORALACCURACY_MAX)
			throw new IllegalArgumentException("Invalid temporalAccuracy provided");

		this.precision = precision;
		this.geoAccuracy = geoAccuracy;
		this.temporalAccuracy = temporalAccuracy;

		// START_DATE is epoch UTC (01/01/1970). Add 3 days to make it
		// 01/04/1970
		Constants.START_DATE.add(Calendar.DAY_OF_YEAR, 3);

		// make widgets
		setupWindow();
		pack();
	}

	private void setupWindow() {

		// setup overall app ui
		setTitle("Heated Planet Diffusion Simulation");

		setSize(800, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getContentPane().setLayout(new GridLayout());
		setLocationRelativeTo(null);

		lowerRightWindow(); // Set window location to lower right (so we don't
							// hide dialogs)
		setAlwaysOnTop(true);

		getContentPane().add(settingsAndControls());
		getContentPane().add(query(isquery));

	}

	private void lowerRightWindow() {

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) (dimension.getWidth() - this.getWidth());
		int y = (int) (dimension.getHeight() - this.getHeight());
		this.setLocation(x, y);

	}

	private JPanel query(boolean isquery) {

		queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.PAGE_AXIS));
		queryPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		queryPanel.setVisible(isquery);

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
				
				final int simulationLength = Integer.parseInt(settingsWidget.GetInputText("Simulation Length"));
				
				if (simulationLength < Constants.MIN_SIM_LEN || simulationLength > Constants.MAX_SIM_LEN)
					throw new IllegalArgumentException("Invalid simulation length");
				
				Calendar end = (Calendar) Constants.START_DATE.clone();
				end.add(Calendar.MONTH, simulationLength);
				init(simulationLength, ((Calendar) Constants.START_DATE.clone()).getTimeInMillis(), end.getTimeInMillis());
				System.out.println("Produce msg"); // Code not reaching here
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

		} else if ("Query".equals(cmd)) {

			System.out.println("Query button clicked");
			isquery = true;
			queryPanel.setVisible(isquery);
			queryWidget.updateQList();
			controlWidget.disableButtonsBasedOnAction(cmd);
			
		} else if ("Run".equals(cmd)) {
			
			final int simulationLength = Integer.parseInt(settingsWidget.GetInputText("Simulation Length"));
			
			if (simulationLength < Constants.MIN_SIM_LEN || simulationLength > Constants.MAX_SIM_LEN)
				throw new IllegalArgumentException("Invalid simulation length");
			
			final double wLat = Double.parseDouble(queryWidget.GetUserInputs("West Longitude"));
			final double eLat = Double.parseDouble(queryWidget.GetUserInputs("East Longitude"));
			final double sLat = Double.parseDouble(queryWidget.GetUserInputs("South Latitude"));
			final double nLat = Double.parseDouble(queryWidget.GetUserInputs("North Latitude"));
			
			// Now retrieve the time constraints
			final int startHour = Integer.parseInt(queryWidget.GetUserInputs("Start Hour"));
			final int startMinute = Integer.parseInt(queryWidget.GetUserInputs("Start Minute"));
			final int endHour = Integer.parseInt(queryWidget.GetUserInputs("End Hour"));
			final int endMinute = Integer.parseInt(queryWidget.GetUserInputs("End Minute"));
			
			final boolean minTemp = queryWidget.GetCheckBox("Minimum Temp");
			final boolean maxTemp = queryWidget.GetCheckBox("Maximum Temp");
			final boolean meanTime = queryWidget.GetCheckBox("Mean Time Temp");
			final boolean meanRegion = queryWidget.GetCheckBox("Mean Region Temp");
			final boolean actualValue = queryWidget.GetCheckBox("Actual Values");
			
			final int gs = Integer.parseInt(settingsWidget.GetInputText("Grid Spacing"));
			
			StartMessage msg = new StartMessage();
			msg.setGridSpacing(gs);
			
			msg.setLatitude(nLat, sLat, eLat, wLat);
			msg.setStartTime(startHour, startMinute);
			msg.setEndTime(endHour, endMinute);
			msg.setShowMinTemp(minTemp);
			msg.setShowMaxTemp(maxTemp);
			msg.setShowMeanTime(meanTime);
			msg.setShowMeanRegion(meanRegion);
			msg.setShowActualValue(actualValue);
			Publisher.getInstance().send(msg);
			
//			final Calendar start = queryWidget.getSelectedStartDate();
//			final Calendar end = queryWidget.getSelectedEndDate();
			
//			start.add(Calendar.HOUR, startHour);
//			start.add(Calendar.MINUTE, startMinute);
//			
//			end.add(Calendar.HOUR, endHour);
//			end.add(Calendar.MINUTE, endMinute);
//			
//			long startDateTime = start.getTimeInMillis();
//			long endDateTime = end.getTimeInMillis();

			try {
				queryEngine = new QueryEngine();
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, "Unable to initialize Database backend. Please close application and try again.");
			}
			
			// TODO get the dates and times from query widget and convert them into calendars, send in millis 
			
			// init(simulationLength, startDateTime, endDateTime);
		}
	}
	
	private void init(int simulationLength, long startDateTime, long endDateTime) {
		
		// TODO check for stop and reset?

		final int gs = Integer.parseInt(settingsWidget.GetInputText("Grid Spacing"));
		final int timeStep = Integer.parseInt(settingsWidget.GetInputText("Simulation Time Step"));
		final float presentationInterval = Float.parseFloat(settingsWidget.GetInputText("Presentation Rate"));
		final float axisTilt = Float.parseFloat(settingsWidget.GetInputText("Axis Tilt"));
		final float eccentricity = Float.parseFloat(settingsWidget.GetInputText("Orbital Eccentricity"));

		// TODO set name
		// TODO check name against the DAO
		// TODO I thought we were going to let the user set the name?
		final String simName = gs + "-" + timeStep + "-" + presentationInterval + "-" + simulationLength + "-" + axisTilt + "-" + count;
		count++;

		if (gs < Constants.MIN_GRID_SPACING || gs > Constants.MAX_GRID_SPACING)
			throw new IllegalArgumentException("Invalid grid spacing");

		// We'll let the user provide Time Steps in base 2 intervals
		// starting from 1
		if (timeStep < Constants.MIN_TIME_STEP || timeStep > Constants.MAX_TIME_STEP || (simulationLength != 1 && simulationLength % 2 != 0))
			throw new IllegalArgumentException("Invalid time step");

		if (presentationInterval < Constants.MIN_PRESENTATION || presentationInterval > Constants.MAX_PRESENTATION)
			throw new IllegalArgumentException("Invalid presentation interval");

		if (axisTilt < Constants.MIN_AXIS_TILT || axisTilt > Constants.MAX_AXIS_TILT)
			throw new IllegalArgumentException("Invalid axisTilt value");

		if (eccentricity < Constants.MIN_ECCENTRICITY || eccentricity > Constants.MAX_ECCENTRICITY)
			throw new IllegalArgumentException("Invalid eccentricity value");

		// Create and reset the buffer
		Buffer.getBuffer().create(Constants.DEFAULT_BUFFFER_SIZE);

		// threadManager.add(new SimulationDAO(new SimulationNeo4j()));

		threadManager.execute(new EarthEngine(new Monitor(endDateTime)));
		threadManager.execute(new EarthDisplayEngine());

		Boolean animate = settingsWidget.GetDisplayAnimationStatus();
		
		Calendar startDateTimeCal = (Calendar) Constants.START_DATE.clone();
		startDateTimeCal.setTimeInMillis(startDateTime);

		StartMessage msg = new StartMessage();
		msg.setSimulationName(simName);
		msg.setGridSpacing(gs);
		msg.setTimeStep(timeStep);
		msg.setPresentationInterval(presentationInterval);
		msg.setSimulationLength(simulationLength);
		msg.setAxisTilt(axisTilt);
		msg.setOrbitalEccentricity(eccentricity);
		msg.setPrecision(this.precision);
		msg.setGeoAccuracy(this.geoAccuracy);
		msg.setTemporalAccuracy(this.temporalAccuracy);
		msg.setAnimated(animate);
		msg.setStartTime(startDateTimeCal);
		
		Publisher.getInstance().send(msg);
	}
}