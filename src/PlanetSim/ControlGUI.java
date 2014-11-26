// GUI.java
package PlanetSim;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Calendar;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import messaging.Publisher;
import messaging.events.ConfigureMessage;
import messaging.events.PauseMessage;
import messaging.events.ProduceMessage;
import messaging.events.ResumeMessage;
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
import db.SimulationDAO;
import db.SimulationNeo4j;

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

	private JPanel				queryPanel			= new JPanel();
	private int					count				= 1;

	private String				simulationName;
	private int					gs;
	private int					timeStep;
	private int					simulationLength;
	private float				presentationInterval;
	private float				axisTilt;
	private float				eccentricity;

	private SimulationDAO		simDAO;

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
		Constants.START_DATE.setTimeInMillis(0);
		Constants.START_DATE.add(Calendar.DAY_OF_YEAR, 3);

		try {
			simDAO = new SimulationDAO(new SimulationNeo4j());
			ThreadManager.getManager().execute(simDAO);
			queryEngine = new QueryEngine(simDAO);
		} catch (SQLException e) {
			ShowMessage("Unable to initialize Database backend. Please close application and try again.");
			return;
		}

		// make widgets
		setupWindow();
		pack();

		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				int confirmed = JOptionPane.showConfirmDialog(rootPane, "Are you sure you want to exit the program?",
						"Exit Program Message Box", JOptionPane.YES_NO_OPTION);

				if (confirmed == JOptionPane.YES_OPTION) {
					Publisher.getInstance().send(new StopMessage());
					dispose();
					System.exit(1);
				}
			}
		});
	}

	private void setupWindow() {

		// setup overall app ui
		setTitle("Heated Planet Diffusion Simulation");

		setSize(900, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getContentPane().setLayout(new GridLayout());
		setLocationRelativeTo(null);

		// lowerRightWindow(); // Set window location to lower right (so we
		// don't
		// hide dialogs)
		setAlwaysOnTop(true);

		getContentPane().add(settingsAndControls());
		getContentPane().add(query());

	}

	//
	// private void lowerRightWindow() {
	//
	// Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	// int x = (int) (dimension.getWidth() - this.getWidth());
	// int y = (int) (dimension.getHeight() - this.getHeight());
	// this.setLocation(x, y);
	//
	// }

	private JPanel query() {

		queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.PAGE_AXIS));
		queryPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		// queryPanel.setVisible(isquery);

		queryWidget = new QueryWidget(queryEngine, settingsWidget);

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

		if ("Start".equals(cmd)) {
			try {

				init();

				if (!simDAO.doesSimulationExist(simulationName)) {
					
					simDAO.setSimulationName(simulationName, gs, timeStep, simulationLength, presentationInterval, axisTilt, eccentricity);

					Calendar end = (Calendar) Constants.START_DATE.clone();
					end.add(Calendar.MONTH, simulationLength);

					ConfigureMessage msg = new ConfigureMessage();

					configure(msg, ((Calendar) Constants.START_DATE.clone()).getTimeInMillis(), end.getTimeInMillis());

					// Quick off a simulation
					Publisher.getInstance().send(new ProduceMessage());

					// do gui stuff to indicate start has occurred.
					controlWidget.disableButtonsBasedOnAction(cmd);
					queryWidget.setFields(false);
					queryWidget.updateQList();
				} else {
					ShowMessage("Simulation Name already exists in the database");
				}

			} catch (NumberFormatException nfe) {
				ShowMessage("Please correct input. All fields need numbers");
			} catch (IllegalArgumentException ex) {
				ShowMessage("Please correct input. All fields need numbers");
			} catch (SQLException ex) {
				ShowMessage("Query against Simulation name failed: " + ex);
			} catch (Exception ex) {
				ShowMessage("Failed to save Simulation Data: " + ex);
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

			try {

				init();

				queryWidget.query(gs, timeStep, simulationLength, presentationInterval, axisTilt, eccentricity);
				controlWidget.disableButtonsBasedOnAction(cmd);

			} catch (NumberFormatException nfe) {
				ShowMessage("Please correct input. All fields need numbers");
			} catch (IllegalArgumentException ex) {
				ShowMessage("Please correct input. All fields need numbers");
			} catch (Exception ex) {
				ShowMessage("Query failed: " + ex);
			}

		} else if ("Run".equals(cmd)) {

			try {

				init();

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

				final int gs = Integer.parseInt(settingsWidget.getInputText("Grid Spacing"));

				ConfigureMessage msg = new ConfigureMessage();
				msg.setGridSpacing(gs);

				msg.setLatitude(nLat, sLat, eLat, wLat);
				msg.setStartTime(startHour, startMinute);
				msg.setEndTime(endHour, endMinute);
				msg.setShowMinTemp(minTemp);
				msg.setShowMaxTemp(maxTemp);
				msg.setShowMeanTime(meanTime);
				msg.setShowMeanRegion(meanRegion);
				msg.setShowActualValue(actualValue);

				// final Calendar start = queryWidget.getSelectedStartDate();
				// final Calendar end = queryWidget.getSelectedEndDate();

				// start.add(Calendar.HOUR, startHour);
				// start.add(Calendar.MINUTE, startMinute);
				//
				// end.add(Calendar.HOUR, endHour);
				// end.add(Calendar.MINUTE, endMinute);
				//
				// long startDateTime = start.getTimeInMillis();
				// long endDateTime = end.getTimeInMillis();

				// TODO get the dates and times from query widget and convert
				// them
				// into calendars, send in millis

				// configure(msg, startDateTime, endDateTime);
			} catch (NumberFormatException nfe) {
				ShowMessage("Please correct input. All fields need numbers");
			} catch (IllegalArgumentException ex) {
				ShowMessage("Please correct input. All fields need numbers");
			} catch (Exception ex) {
				ShowMessage("Query run failed: " + ex);
			}

		}
	}

	private void init() {

		simulationName = settingsWidget.getInputText("Simulation Name");
		gs = Integer.parseInt(settingsWidget.getInputText("Grid Spacing"));
		timeStep = Integer.parseInt(settingsWidget.getInputText("Simulation Time Step"));
		simulationLength = Integer.parseInt(settingsWidget.getInputText("Simulation Length"));
		presentationInterval = Float.parseFloat(settingsWidget.getInputText("Presentation Rate"));
		axisTilt = Float.parseFloat(settingsWidget.getInputText("Axis Tilt"));
		eccentricity = Float.parseFloat(settingsWidget.getInputText("Orbital Eccentricity"));

		// simulationName = gs + "_" + timeStep + "_" + presentationInterval +
		// "_" + simulationLength + "_" + axisTilt
		// + "_" + count;
		// count++;

		if (gs < Constants.MIN_GRID_SPACING || gs > Constants.MAX_GRID_SPACING)
			throw new IllegalArgumentException("Invalid grid spacing");

		if (simulationLength < Constants.MIN_SIM_LEN || simulationLength > Constants.MAX_SIM_LEN)
			throw new IllegalArgumentException("Invalid simulation length");

		// We'll let the user provide Time Steps in base 2 intervals
		// starting from 1
		if (timeStep < Constants.MIN_TIME_STEP || timeStep > Constants.MAX_TIME_STEP
				|| (simulationLength != 1 && simulationLength % 2 != 0))
			throw new IllegalArgumentException("Invalid time step");

		if (presentationInterval < Constants.MIN_PRESENTATION || presentationInterval > Constants.MAX_PRESENTATION)
			throw new IllegalArgumentException("Invalid presentation interval");

		if (axisTilt < Constants.MIN_AXIS_TILT || axisTilt > Constants.MAX_AXIS_TILT)
			throw new IllegalArgumentException("Invalid axisTilt value");

		if (eccentricity < Constants.MIN_ECCENTRICITY || eccentricity > Constants.MAX_ECCENTRICITY)
			throw new IllegalArgumentException("Invalid eccentricity value");
	}

	private void configure(ConfigureMessage msg, long startDateTime, long endDateTime) {

		// Create and reset the buffer
		Buffer.getBuffer().create(Constants.DEFAULT_BUFFFER_SIZE);

		threadManager.execute(new EarthEngine(new Monitor(endDateTime)));
		threadManager.execute(new EarthDisplayEngine());

		Boolean animate = settingsWidget.getDisplayAnimationStatus();

		Calendar startDateTimeCal = (Calendar) Constants.START_DATE.clone();
		startDateTimeCal.setTimeInMillis(startDateTime);

		msg.setSimulationName(simulationName);
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

	private void ShowMessage(final String message) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(rootPane, message);
			}
		});
	}
}