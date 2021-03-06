// GUI.java
package PlanetSim;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
import db.IQueryResult;
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

	private JPanel				queryPanel;

	private String				simulationName;
	private int					gs;
	private int					timeStep;
	private int					simulationLength;
	private float				presentationInterval;
	private float				axisTilt;
	private float				eccentricity;

	private SimulationDAO		simDAO;
	
	SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	

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

		// START_DATE is epoch UTC (01/01/1970)
		
		Constants.START_DATE.set(Calendar.HOUR_OF_DAY, 0);
		Constants.START_DATE.set(Calendar.MINUTE, 0);
		Constants.START_DATE.set(Calendar.SECOND, 0);
		Constants.START_DATE.set(Calendar.MILLISECOND, 0);
		Constants.START_DATE.set(Calendar.MONTH, 0);
		Constants.START_DATE.set(Calendar.DAY_OF_MONTH, 4);
		
		try {
			System.out.println(format1.parse(format1.format(Constants.START_DATE.getTime())));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

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
					threadManager.shutdown();
					dispose();
					System.exit(1);
				}
			}
		});
	}

	private void setupWindow() {

		// setup overall app ui
		setTitle("Heated Planet Diffusion Simulation");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getContentPane().setLayout(new GridLayout());
		setLocationRelativeTo(null);

		setAlwaysOnTop(true);

		getContentPane().add(settingsAndControls());
		
		JScrollPane scrollPane = new JScrollPane(query());
		
		scrollPane.setBounds(0, 0, 800, 450);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		getContentPane().add(scrollPane);

	}

	private JPanel query() {
		
		queryPanel = new JPanel();

		queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.PAGE_AXIS));
		queryPanel.setAlignmentY(Component.TOP_ALIGNMENT);

		queryWidget = new QueryWidget(queryEngine, settingsWidget);

		queryPanel.add(queryWidget, BorderLayout.CENTER);
		queryPanel.setPreferredSize(new Dimension(600, 450));
		
		queryWidget.setFields(true);
		
		return queryPanel;
	}

	private JPanel settingsAndControls() {

		JPanel sncPanel = new JPanel();
		sncPanel.setLayout(new BoxLayout(sncPanel, BoxLayout.PAGE_AXIS));
		sncPanel.setAlignmentY(Component.TOP_ALIGNMENT);

		controlWidget = new ControlWidget(this);
		settingsWidget = new SettingsWidget();
		settingsWidget.setPreferredSize(new Dimension(100, 425));
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
				
				if (simulationName == null || "".equals(simulationName))
					throw new IllegalArgumentException("Invalid Simulation Name");

				if (!simDAO.doesSimulationExist(simulationName)) {
					
					// TODO move to engine
					IQueryResult result = simDAO.setSimulationName(simulationName, gs, timeStep, simulationLength, presentationInterval, axisTilt, eccentricity);
					if (!simulationName.equals(result.getSimulationName().get(0)))
						throw new SQLException("Creating the new Simulation failed");

					Calendar end = (Calendar) Constants.START_DATE.clone();
					end.add(Calendar.MONTH, simulationLength);

					ConfigureMessage msg = new ConfigureMessage();

					configure(msg, ((Calendar) Constants.START_DATE.clone()).getTimeInMillis(), end.getTimeInMillis());

					// Quick off a simulation
					Publisher.getInstance().send(new ProduceMessage());

					// do gui stuff to indicate start has occurred.
					controlWidget.disableButtonsBasedOnAction(cmd);
					queryWidget.updateQList();
					
				} else {
					ShowMessage("Simulation Name already exists in the database");
				}

			} catch (NumberFormatException ex) {
				ShowMessage("Invalid input: " + ex.getMessage());
			} catch (IllegalArgumentException ex) {
				ShowMessage("Invalid input: " + ex.getMessage());
			} catch (SQLException ex) {
				ShowMessage("Query against Simulation name failed: " + ex.getMessage());
			} catch (Exception ex) {
				ShowMessage("Failed to save Simulation Data: " + ex.getMessage());
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
			
		} else if ("Reset".equals(cmd)) {
			
			queryWidget.updateQList();
			controlWidget.disableButtonsBasedOnAction(cmd);

		} else if ("Query".equals(cmd)) {

			try {

				init();

				queryWidget.query(gs, timeStep, simulationLength, presentationInterval, axisTilt, eccentricity);
				controlWidget.disableButtonsBasedOnAction(cmd);

			} catch (NumberFormatException ex) {
				ShowMessage("Invalid input: " + ex.getMessage());
			} catch (IllegalArgumentException ex) {
				ShowMessage("Invalid input: " + ex.getMessage());
			} catch (Exception ex) {
				ShowMessage("Query failed: " + ex.getMessage());
			}

		} else if ("Run".equals(cmd)) {

			try {

				init();
				
				if (simulationName == null || "".equals(simulationName))
					throw new IllegalArgumentException("Invalid Simulation Name");

				final int wLat = Integer.parseInt(queryWidget.GetUserInputs("West Longitude"));
				final int eLat = Integer.parseInt(queryWidget.GetUserInputs("East Longitude"));
				final int sLat = Integer.parseInt(queryWidget.GetUserInputs("South Latitude"));
				final int nLat = Integer.parseInt(queryWidget.GetUserInputs("North Latitude"));

				// Now retrieve the time constraints
				final int startHour = Integer.parseInt(queryWidget.GetComboBox("Start Hour"));
				final int startMinute = Integer.parseInt(queryWidget.GetComboBox("Start Minute"));
				final int endHour = Integer.parseInt(queryWidget.GetComboBox("End Hour")); 
				final int endMinute = Integer.parseInt(queryWidget.GetComboBox("End Minute"));
				
				final boolean minTemp = queryWidget.GetCheckBox("Minimum Temp");
				final boolean maxTemp = queryWidget.GetCheckBox("Maximum Temp");
				final boolean meanTime = queryWidget.GetCheckBox("Mean Time Temp");
				final boolean meanRegion = queryWidget.GetCheckBox("Mean Region Temp");
				final boolean actualValue = queryWidget.GetCheckBox("Actual Values");
				
				ConfigureMessage msg = new ConfigureMessage();

				msg.setLatitude(nLat, sLat, eLat, wLat);
				msg.setStartTime(startHour, startMinute);
				msg.setEndTime(endHour, endMinute);
				msg.setShowMinTemp(minTemp);
				msg.setShowMaxTemp(maxTemp);
				msg.setShowMeanTime(meanTime);
				msg.setShowMeanRegion(meanRegion);
				msg.setShowActualValue(actualValue);
				
				final Calendar selectedStart = queryWidget.getSelectedStartDate();
				final Calendar selectedEnd = queryWidget.getSelectedEndDate();

				 selectedStart.add(Calendar.HOUR, startHour);
				 selectedStart.add(Calendar.MINUTE, startMinute);
				
				 selectedEnd.add(Calendar.HOUR, endHour);
				 selectedEnd.add(Calendar.MINUTE, endMinute);
				
				long startDateTime =  selectedStart.getTimeInMillis();
				long endDateTime = selectedEnd.getTimeInMillis();

				configure(msg, startDateTime, endDateTime);
				
				queryEngine.findTemperaturesAt(simulationName, startDateTime, endDateTime, wLat, eLat, nLat, sLat);
				
			} catch (NumberFormatException ex) {
				ShowMessage("Invalid input: " + ex.getMessage());
			} catch (IllegalArgumentException ex) {
				ShowMessage("Invalid input: " + ex.getMessage());
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

		if (gs < Constants.MIN_GRID_SPACING || gs > Constants.MAX_GRID_SPACING)
			throw new IllegalArgumentException("Invalid grid spacing");

		if (simulationLength < Constants.MIN_SIM_LEN || simulationLength > Constants.MAX_SIM_LEN)
			throw new IllegalArgumentException("Invalid simulation length");

		// We'll let the user provide Time Steps in base 2 intervals
		// starting from 1
		if (timeStep < Constants.MIN_TIME_STEP || timeStep > Constants.MAX_TIME_STEP)
			throw new IllegalArgumentException("Invalid time step");
		
		if (timeStep > 1 && timeStep % 2 != 0)
			throw new IllegalArgumentException("Invalid time step. Time step must be a factor of 2");
		
		int monthsFromMinutes = timeStep >= Constants.MINUTES_IN_A_MONTH ? timeStep / Constants.MINUTES_IN_A_MONTH : 0;
		if (monthsFromMinutes > simulationLength)
			throw new IllegalArgumentException("Invalid time step. Tiem step cannot be greater than simulation length in months");

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

		Calendar startDateTimeCal = Calendar.getInstance();
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