package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import messaging.Publisher;
import messaging.events.StopMessage;
import view.util.ThermalVisualizer;
import view.widgets.EarthImage;
import view.widgets.GridDisplay;
import view.widgets.SimulationStatus;

import common.Constants;
import common.IGrid;

public class EarthDisplay extends JFrame {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -309131746356718870L;

	// core display
	private final JLayeredPane	display;

	// widgets
	private SimulationStatus	simStatus;
	private EarthImage			earthImage;
	private GridDisplay			gridDisplay;

	private int					gs					= 0, timeStep = 0, simulationLength = 0;

	private float				axisTilt			= 0, eccentricity = 0;
	private boolean				animate;

	public EarthDisplay(boolean animate) {

		super("Earth Simulation");

		this.animate = animate;
		EarthDisplay.setDefaultLookAndFeelDecorated(true);

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setResizable(true);

		// Add sim settings
		simStatus = new SimulationStatus(Constants.COLORMAP);
		this.add(simStatus, BorderLayout.SOUTH);

		// Add the display region
		display = new JLayeredPane();
		this.add(display, BorderLayout.CENTER);

		int w, h;
		if (animate) {

			// Add EarthImage
			earthImage = new EarthImage();
			display.add(earthImage, new Integer(Constants.EARTH));

			w = earthImage.getImageWidth();
			h = earthImage.getImageHeight();
		} else {
			w = 600;
			h = 400;
		}

		// Add grid
		gridDisplay = new GridDisplay(new ThermalVisualizer(Constants.COLORMAP, Constants.MIN_TEMP, Constants.MAX_TEMP, Constants.OPACITY), w, h);
		display.add(gridDisplay, new Integer(Constants.GRID));

		this.setPreferredSize(new Dimension(w, h + 200));
		
		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				
				Publisher.getInstance().send(new StopMessage());
				dispose();
			}
		});

	}

	public void display(int gs, int timeStep, int simulationLength, float axisTilt, float eccentricity) {

		this.gs = gs;
		this.timeStep = timeStep;
		this.simulationLength = simulationLength;
		this.axisTilt = axisTilt;
		this.eccentricity = eccentricity;

		this.pack();
		this.setVisible(true);
		this.validate();
	}

	public void close() {
		this.dispose();
	}

	public void update(IGrid grid) {
		
		if (grid != null)
			simStatus.update(grid.getSunPositionDeg(), grid.getDateTime(), this.gs, this.timeStep, this.simulationLength, this.axisTilt, this.eccentricity);
		else
			simStatus.update(0, (Calendar) Constants.START_DATE.clone(), this.gs, this.timeStep, this.simulationLength, this.axisTilt, this.eccentricity);
		
		if (animate)
			gridDisplay.update(grid);
	}
}
