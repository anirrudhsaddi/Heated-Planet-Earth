package view.widgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import view.util.ColorMap;

import common.Constants;

public class SimulationStatus extends JPanel {

	/**
     * 
     */
	private static final long				serialVersionUID	= 4874764682275993951L;

	private JTextField						sunPosStats, currTimeStatus, gsStatus, timeStepStatus, simulationLength,
			axisTilt, eccentricity;
	private JLabel							lblSunPos, lblCurrTime, lblGs, lblTimeStep, lblSimLength, lblAxisTilt,
			lblEccentricity;
	
	private GridBagLayout grid;
	private GridBagConstraints constraint;

	private static final int				HEIGHT				= 7;
	private static final int				WIDTH				= 2;
	private static final int				HGAP				= 1;
	private static final int				VGAP				= 1;

	private Calendar						currDateTime		= (Calendar) Constants.START_DATE.clone();
	private static final SimpleDateFormat	DATE_FORMAT			= new SimpleDateFormat("dd-MM-yy HH:mm:SS");

	private ColorMap						colorMap;

	public SimulationStatus(String colorMap) {

		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.setPreferredSize(new Dimension(200, 200));
		
		grid = new GridBagLayout();
		constraint = new GridBagConstraints();
		constraint.weightx = 1;
		constraint.weighty = 1;
		constraint.gridx = 0;
		constraint.gridy = 0;
		constraint.anchor = GridBagConstraints.CENTER;
		
		this.setLayout(grid);

		this.colorMap = ColorMap.getMap(colorMap);

		this.addStatusPanel();
		this.addKeyPanel();
	}

	public void init() {

		this.sunPosStats.setText("0");
		this.gsStatus.setText("0");
		this.timeStepStatus.setText("0");
		this.simulationLength.setText("0");
		this.axisTilt.setText("0");
		this.eccentricity.setText("0");

		this.currTimeStatus.setText(DATE_FORMAT.format(currDateTime.getTime()));
	}

	public void update(float sunPosition, Calendar dateTime, int gs, int timeStep, int simulationLength,
			float axisTilt, float eccentricity) {

		this.sunPosStats.setText(String.format("%.1f", sunPosition));
		this.gsStatus.setText(Integer.toString(gs));
		this.timeStepStatus.setText(Integer.toString(timeStep));
		this.simulationLength.setText(Integer.toString(simulationLength));
		this.axisTilt.setText(Float.toString(axisTilt));
		this.eccentricity.setText(Float.toString(eccentricity));

		this.currTimeStatus.setText(DATE_FORMAT.format(dateTime.getTime()));
	}

	private void addKeyPanel() {
		
		JLabel lblOne = new JLabel("0"); 
		lblOne.setSize(new Dimension(10, 10));
		
		JLabel lblTwo = new JLabel("" + (Constants.MAX_TEMP * 0.25)); 
		lblTwo.setSize(new Dimension(10, 10));
		
		JLabel lblThree = new JLabel("" + (Constants.MAX_TEMP * 0.5)); 
		lblThree.setSize(new Dimension(10, 10));
		
		JLabel lblFour = new JLabel("" + (Constants.MAX_TEMP * 0.75)); 
		lblFour.setSize(new Dimension(10, 10));
		
		JLabel lblFive = new JLabel("" + Constants.MAX_TEMP);
		lblFive.setSize(new Dimension(10, 10));
		
		this.add(lblOne);
		this.add(lblTwo);
		this.add(lblThree);
		this.add(lblFour);
		this.add(lblFive);
		
		ThermalScale t = new ThermalScale();
		this.add(t);

	}

	private void addStatusPanel() {

		sunPosStats = new JTextField("0");
		currTimeStatus = new JTextField("0");
		gsStatus = new JTextField("0");
		timeStepStatus = new JTextField("0");
		simulationLength = new JTextField("0");
		axisTilt = new JTextField("0");
		eccentricity = new JTextField("0");

		lblSunPos = new JLabel("Rotational Position:");
		lblCurrTime = new JLabel("Time:");
		lblGs = new JLabel("Grid Spacing:");
		lblTimeStep = new JLabel("Simulation Time Step:");
		lblSimLength = new JLabel("Simulation Length:");
		lblAxisTilt = new JLabel("Axis Tilt:");
		lblEccentricity = new JLabel("Orbital Eccentricity:");

		sunPosStats.setSize(new Dimension(50, 10));
		sunPosStats.setMaximumSize(new Dimension(50, 10));
		sunPosStats.getFont().deriveFont(Font.PLAIN, 10);
		sunPosStats.setEditable(false);

		currTimeStatus.setSize(new Dimension(50, 10));
		currTimeStatus.setMaximumSize(new Dimension(50, 10));
		currTimeStatus.getFont().deriveFont(Font.PLAIN, 10);
		currTimeStatus.setEditable(false);

		gsStatus.setSize(new Dimension(50, 10));
		gsStatus.setMaximumSize(new Dimension(50, 10));
		gsStatus.getFont().deriveFont(Font.PLAIN, 10);
		gsStatus.setEditable(false);

		timeStepStatus.setSize(new Dimension(50, 10));
		timeStepStatus.setMaximumSize(new Dimension(50, 10));
		timeStepStatus.getFont().deriveFont(Font.PLAIN, 10);
		timeStepStatus.setEditable(false);

		simulationLength.setSize(new Dimension(50, 10));
		simulationLength.setMaximumSize(new Dimension(50, 10));
		simulationLength.getFont().deriveFont(Font.PLAIN, 10);
		simulationLength.setEditable(false);

		axisTilt.setSize(new Dimension(50, 10));
		axisTilt.setMaximumSize(new Dimension(50, 10));
		axisTilt.getFont().deriveFont(Font.PLAIN, 10);
		axisTilt.setEditable(false);

		eccentricity.setSize(new Dimension(50, 10));
		eccentricity.setMaximumSize(new Dimension(50, 10));
		eccentricity.getFont().deriveFont(Font.PLAIN, 10);
		eccentricity.setEditable(false);

		lblSunPos.getFont().deriveFont(Font.PLAIN, 8);
		lblCurrTime.getFont().deriveFont(Font.PLAIN, 8);
		lblGs.getFont().deriveFont(Font.PLAIN, 8);
		lblTimeStep.getFont().deriveFont(Font.PLAIN, 8);
		lblSimLength.getFont().deriveFont(Font.PLAIN, 8);
		lblAxisTilt.getFont().deriveFont(Font.PLAIN, 8);
		lblEccentricity.getFont().deriveFont(Font.PLAIN, 8);
		
		this.add(lblSunPos);
		this.add(sunPosStats);
		this.add(lblCurrTime);
		this.add(currTimeStatus);
		this.add(lblGs);
		this.add(gsStatus);
		this.add(lblTimeStep);
		this.add(timeStepStatus);
		this.add(lblSimLength);
		this.add(simulationLength);
		this.add(lblAxisTilt);
		this.add(axisTilt);
		this.add(lblEccentricity);
		this.add(eccentricity);
		
	}

	private class ThermalScale extends Component {

		/**
		 * 
		 */
		private static final long	serialVersionUID	= 5835845197614603392L;

		public void paint(Graphics g) {

			final ArrayList<Color> gradient = new ArrayList<Color>();

			Color c;
			for (double temp = 0; temp <= 1; temp += 0.05) {
				c = colorMap.getColor(temp, 1);
				if (!gradient.contains(c))
					gradient.add(c);
			}

			int size = gradient.size();
			float curr = 0;
			
			Color[] colors = new Color[size];
			float[] fractions = new float[size];
			for (int i = 0; i < size; i++) {
				colors[i] = gradient.get(i);
				fractions[i] = curr;
				curr += 0.05;
			}

			final Rectangle2D r2d = new Rectangle2D.Double(0, 0, 400, 100);

			Graphics2D g2D = (Graphics2D) g;
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			LinearGradientPaint DARK_GRADIENT = new LinearGradientPaint(new Point2D.Double(0, 0), new Point2D.Double(400, 0), fractions, colors);
			g2D.setPaint(DARK_GRADIENT);
			g2D.fill(r2d);
		}
	}
}
