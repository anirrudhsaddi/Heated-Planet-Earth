package view.widgets;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import common.Constants;

public class SimulationStatus extends JSplitPane {

	/**
     * 
     */
	private static final long				serialVersionUID	= 4874764682275993951L;
	
	private static final String 			GRADIENT_ICON = "resource/gradient.png";

	private JTextField						sunPosStats, currTimeStatus, gsStatus, timeStepStatus, simulationLength,
			axisTilt, eccentricity;
	private JLabel							lblSunPos, lblCurrTime, lblGs, lblTimeStep, lblSimLength, lblAxisTilt,
			lblEccentricity;

	private JPanel							statusPanel, legendPanel;

	private static final int				HGAP				= 1;
	private static final int				VGAP				= 1;

	private Calendar						currDateTime		= (Calendar) Constants.START_DATE.clone();
	private static final SimpleDateFormat	DATE_FORMAT			= new SimpleDateFormat("dd-MM-yy HH:mm:SS");
	
	private Label lblZero;
	private Label lblOne;
	private Label lblTwo;
	private Label lblThree;
	private Label lblFour;

	public SimulationStatus(String colorMap) {

		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.setPreferredSize(new Dimension(200, 200));

		this.addStatusPanel();
		this.addKeyPanel();

	}

//	public void init() {
//
//		this.sunPosStats.setText("0");
//		this.gsStatus.setText("0");
//		this.timeStepStatus.setText("0");
//		this.simulationLength.setText("0");
//		this.axisTilt.setText("0");
//		this.eccentricity.setText("0");
//
//		this.currTimeStatus.setText(DATE_FORMAT.format(currDateTime.getTime()));
//	}

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

		legendPanel = new JPanel();
		
		GridBagLayout gbl_legendPanel = new GridBagLayout();
		gbl_legendPanel.columnWidths = new int[]{40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 0};
		gbl_legendPanel.rowHeights = new int[]{0, 35, 39, 0, 0, 0};
		gbl_legendPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_legendPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		legendPanel.setLayout(gbl_legendPanel);
		
		lblZero = new Label("0");
		GridBagConstraints gbc_lblZero = new GridBagConstraints();
		gbc_lblZero.insets = new Insets(0, 0, 5, 5);
		gbc_lblZero.gridx = 1;
		gbc_lblZero.gridy = 1;
		legendPanel.add(lblZero, gbc_lblZero);
		
		lblOne = new Label("" + (Constants.MAX_TEMP * 0.25));
		GridBagConstraints gbc_lblOne = new GridBagConstraints();
		gbc_lblOne.insets = new Insets(0, 0, 5, 5);
		gbc_lblOne.gridx = 3;
		gbc_lblOne.gridy = 1;
		legendPanel.add(lblOne, gbc_lblOne);
		
		lblTwo = new Label("" + (Constants.MAX_TEMP * 0.5));
		GridBagConstraints gbc_lblTwo = new GridBagConstraints();
		gbc_lblTwo.insets = new Insets(0, 0, 5, 5);
		gbc_lblTwo.gridx = 5;
		gbc_lblTwo.gridy = 1;
		legendPanel.add(lblTwo, gbc_lblTwo);
		
		lblThree = new Label("" + (Constants.MAX_TEMP * 0.75));
		GridBagConstraints gbc_lblThree = new GridBagConstraints();
		gbc_lblThree.insets = new Insets(0, 0, 5, 5);
		gbc_lblThree.gridx = 7;
		gbc_lblThree.gridy = 1;
		legendPanel.add(lblThree, gbc_lblThree);
		
		lblFour = new Label(Constants.MAX_TEMP + "+");
		GridBagConstraints gbc_lblFour = new GridBagConstraints();
		gbc_lblFour.insets = new Insets(0, 0, 5, 0);
		gbc_lblFour.gridx = 9;
		gbc_lblFour.gridy = 1;
		legendPanel.add(lblFour, gbc_lblFour);
		
		ImageIcon gradientImg = new ImageIcon(GRADIENT_ICON);
		JLabel scalePanel = new JLabel();
		scalePanel.setIcon(gradientImg);
		
		GridBagConstraints gbc_scalePanel = new GridBagConstraints();
		gbc_scalePanel.insets = new Insets(0, 0, 5, 5);
		gbc_scalePanel.gridwidth = 8;
		gbc_scalePanel.gridx = 1;
		gbc_scalePanel.gridy = 2;
		legendPanel.add(scalePanel, gbc_scalePanel);
		
		this.add(legendPanel, JSplitPane.RIGHT);
	}

	private void addStatusPanel() {

		sunPosStats = new JTextField();
		currTimeStatus = new JTextField();
		gsStatus = new JTextField();
		timeStepStatus = new JTextField();
		simulationLength = new JTextField();
		axisTilt = new JTextField();
		eccentricity = new JTextField();

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

		statusPanel = new JPanel();
		statusPanel.setLayout(new GridLayout(7, 2, HGAP, VGAP));

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

		this.add(statusPanel, JSplitPane.LEFT);

	}
}
