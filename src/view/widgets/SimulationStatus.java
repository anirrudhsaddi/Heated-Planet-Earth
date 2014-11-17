package view.widgets;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class SimulationStatus extends JPanel {

	/**
     * 
     */
	private static final long serialVersionUID = 4874764682275993951L;

	private JTextField sunPosStats, currTimeStatus, gsStatus, timeStepStatus,
			simulationLength, axisTilt, eccentricity;
	private JLabel lblSunPos, lblCurrTime, lblGs, lblTimeStep, lblSimLength,
			lblAxisTilt, lblEccentricity;

	private static final int HEIGHT = 7;
	private static final int WIDTH = 2;
	private static final int HGAP = 1;
	private static final int VGAP = 1;

	// private final SimpleDateFormat DATE_FORMAT = new
	// SimpleDateFormat("dd-MM-yy HH:mm:SS");

	public SimulationStatus() {

		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.setLayout(new GridLayout(HEIGHT, WIDTH, HGAP, VGAP));

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

	public void init() {
		this.sunPosStats.setText("0");
		this.currTimeStatus.setText("0");
		this.gsStatus.setText("0");
		this.timeStepStatus.setText("0");
		this.simulationLength.setText("0");
		this.axisTilt.setText("0");
		this.eccentricity.setText("0");
	}

	public void update(float sunPosition, long l, int gs, int timeStep,
			int simulationLength, float axisTilt, float eccentricity) {

		this.sunPosStats.setText(String.format("%.1f", sunPosition));
		this.currTimeStatus.setText(Long.toString(l));
		this.gsStatus.setText(Integer.toString(gs));
		this.timeStepStatus.setText(Integer.toString(timeStep));
		this.simulationLength.setText(Integer.toString(simulationLength));
		this.axisTilt.setText(Float.toString(axisTilt));
		this.eccentricity.setText(Float.toString(eccentricity));
	}
}
