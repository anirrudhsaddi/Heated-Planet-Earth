package view.widgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
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

import view.util.ColorGenerator;
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

	private JLabel							colorScale;

	private JPanel							statusPanel, legendPanel;

	private static final int				HEIGHT				= 7;
	private static final int				WIDTH				= 2;
	private static final int				HGAP				= 1;
	private static final int				VGAP				= 1;

	private Calendar						currDateTime		= (Calendar) Constants.START_DATE.clone();
	private static final SimpleDateFormat	DATE_FORMAT			= new SimpleDateFormat("dd-MM-yy HH:mm:SS");

	private ColorMap						colorMap;

	public SimulationStatus(String colorMap) {

		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.setLayout(new GridLayout(1, 2, HGAP, VGAP));

		statusPanel = new JPanel();
		statusPanel.setLayout(new GridLayout(HEIGHT, WIDTH, HGAP, VGAP));

		legendPanel = new JPanel();
		legendPanel.setLayout(new GridLayout(1, 2, HGAP, VGAP));

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
		
		legendPanel.add(new ThermalScale());

		this.add(legendPanel);
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

		sunPosStats.setPreferredSize(new Dimension(10, 10));
		sunPosStats.setMaximumSize(new Dimension(10, 10));
		sunPosStats.getFont().deriveFont(Font.PLAIN, 10);
		sunPosStats.setEditable(false);

		currTimeStatus.setPreferredSize(new Dimension(10, 10));
		currTimeStatus.setMaximumSize(new Dimension(10, 10));
		currTimeStatus.getFont().deriveFont(Font.PLAIN, 10);
		currTimeStatus.setEditable(false);

		gsStatus.setPreferredSize(new Dimension(10, 10));
		gsStatus.setMaximumSize(new Dimension(10, 10));
		gsStatus.getFont().deriveFont(Font.PLAIN, 10);
		gsStatus.setEditable(false);

		timeStepStatus.setPreferredSize(new Dimension(10, 10));
		timeStepStatus.setMaximumSize(new Dimension(10, 10));
		timeStepStatus.getFont().deriveFont(Font.PLAIN, 10);
		timeStepStatus.setEditable(false);

		simulationLength.setPreferredSize(new Dimension(10, 10));
		simulationLength.setMaximumSize(new Dimension(10, 10));
		simulationLength.getFont().deriveFont(Font.PLAIN, 10);
		simulationLength.setEditable(false);

		axisTilt.setPreferredSize(new Dimension(10, 10));
		axisTilt.setMaximumSize(new Dimension(10, 10));
		axisTilt.getFont().deriveFont(Font.PLAIN, 10);
		axisTilt.setEditable(false);

		eccentricity.setPreferredSize(new Dimension(10, 10));
		eccentricity.setMaximumSize(new Dimension(10, 10));
		eccentricity.getFont().deriveFont(Font.PLAIN, 10);
		eccentricity.setEditable(false);

		lblSunPos.getFont().deriveFont(Font.PLAIN, 8);
		lblCurrTime.getFont().deriveFont(Font.PLAIN, 8);
		lblGs.getFont().deriveFont(Font.PLAIN, 8);
		lblTimeStep.getFont().deriveFont(Font.PLAIN, 8);
		lblSimLength.getFont().deriveFont(Font.PLAIN, 8);
		lblAxisTilt.getFont().deriveFont(Font.PLAIN, 8);
		lblEccentricity.getFont().deriveFont(Font.PLAIN, 8);

		statusPanel.add(lblSunPos);
		statusPanel.add(sunPosStats);

		statusPanel.add(lblCurrTime);
		statusPanel.add(currTimeStatus);

		statusPanel.add(lblGs);
		statusPanel.add(gsStatus);

		statusPanel.add(lblTimeStep);
		statusPanel.add(timeStepStatus);

		statusPanel.add(lblSimLength);
		statusPanel.add(simulationLength);

		statusPanel.add(lblAxisTilt);
		statusPanel.add(axisTilt);

		statusPanel.add(lblEccentricity);
		statusPanel.add(eccentricity);

		this.add(statusPanel);
	}

	private class ThermalScale extends Component {

		public void paint(Graphics g) {

			final ArrayList<Color> gradient = new ArrayList<Color>();

			Color c;
			for (double temp = 0; temp < Constants.MAX_TEMP; temp++) {
				c = colorMap.getColor(temp, 1);
				if (!gradient.contains(c))
					gradient.add(c);
			}

			System.out.println(gradient);

			int size = gradient.size();
			float increment = (float) (1.0 / size);
			float curr = 0;
			float[] fractions = new float[size];
			for (int i = 0; i < size; i++) {
				fractions[i] = curr;
				curr += increment;
			}

			System.out.println(fractions);

			final Rectangle2D r2d = new Rectangle2D.Double(0, 0, 100, 50);

			Graphics2D g2D = (Graphics2D) g;
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			LinearGradientPaint DARK_GRADIENT = new LinearGradientPaint(new Point2D.Double(0, 0), new Point2D.Double(100, 0), fractions, (Color[]) gradient.toArray());
			g2D.setPaint(DARK_GRADIENT);
			g2D.fill(r2d);
		}
	}
}
