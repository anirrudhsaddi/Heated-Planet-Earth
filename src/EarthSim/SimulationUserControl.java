package EarthSim;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import messaging.Publisher;
import messaging.events.PauseMessage;
import messaging.events.ResumeMessage;
import messaging.events.StartMessage;
import messaging.events.StopMessage;

public class SimulationUserControl extends JFrame implements ChangeListener{
	private static final long serialVersionUID = 1L;
	private SimulationOptions options;
	private volatile boolean isPaused;
	//private SimulationController controller;
	private final Publisher publisher;
	
	// Swing controls
	private JPanel mainPanel;
	private EarthPanel presentationPanel;
	private JButton startButton, pauseButton, stopButton;
	private JComboBox comboBoxGridSpacing;
	private JTextField txtTimeStepInMinutes;
	private JTextField txtBufferSize;
	private JTextField txtPresentationDisplayRateInSeconds;
	private JCheckBox cbIsSimulationMultithreaded;
	private JCheckBox cbIsPresentationMultithreaded;
	private JComboBox comboBoxInitiativeType;
	private JLabel lblFilePath;
	private JCheckBox chckbxEnableLogging;
	private Timer uiThreadEventTimer;
	private JLabel lblOpacity;
	private JSlider opacitySlider;
	
	
	int delay = 10; // milliseconds

	public SimulationUserControl() {
		// For designer support
		this.publisher = Publisher.getInstance();
		initialize();
	}

	public SimulationUserControl(SimulationOptions options) {
		this.options = options;
		this.publisher = Publisher.getInstance();
		initialize();
		setOptions();
	}

	private void initialize() {
		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		presentationPanel = new EarthPanel(new java.awt.Dimension(500, 500),
				new java.awt.Dimension(1000, 1000), new java.awt.Dimension(500,
						500));
		presentationPanel.setBounds(12, 13, 1083, 445);
		mainPanel.add(presentationPanel);

		mainPanel.setSize(new java.awt.Dimension(1024, 768));
		getContentPane().add(mainPanel);
		this.setSize(new Dimension(1121, 686));

		startButton = new JButton("Start");
		startButton.setBounds(659, 483, 125, 25);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				startButtonPushed(a);
			}
		});

		pauseButton = new JButton("Pause");
		pauseButton.setBounds(659, 521, 125, 25);
		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				pauseButtonPushed(a);
			}
		});

		stopButton = new JButton("Stop");
		stopButton.setBounds(659, 559, 125, 25);
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				stopButtonPushed(a);
			}
		});

		mainPanel.add(startButton);
		mainPanel.add(pauseButton);
		mainPanel.add(stopButton);

		comboBoxGridSpacing = new JComboBox();
		comboBoxGridSpacing.setBounds(239, 484, 113, 22);
		comboBoxGridSpacing.setModel(new DefaultComboBoxModel(
				GridSpacingIncrement.values()));
		mainPanel.add(comboBoxGridSpacing);

		txtTimeStepInMinutes = new JTextField();
		txtTimeStepInMinutes.setColumns(10);
		txtTimeStepInMinutes.setBounds(239, 522, 113, 22);
		mainPanel.add(txtTimeStepInMinutes);

		JLabel lblGridSpacing = new JLabel("Grid Spacing (degrees):");
		lblGridSpacing.setBounds(87, 487, 140, 16);
		mainPanel.add(lblGridSpacing);

		JLabel label = new JLabel("Simulation Time Step (minutes):");
		label.setBounds(40, 525, 187, 16);
		mainPanel.add(label);

		cbIsSimulationMultithreaded = new JCheckBox("Simulation Multithreaded?");
		cbIsSimulationMultithreaded.setBounds(401, 510, 210, 25);
		mainPanel.add(cbIsSimulationMultithreaded);

		cbIsPresentationMultithreaded = new JCheckBox(
				"Presentation Multithreaded?");
		cbIsPresentationMultithreaded.setBounds(401, 540, 187, 25);
		mainPanel.add(cbIsPresentationMultithreaded);

		JLabel lblInitiativeType = new JLabel("Initiative Type:");
		lblInitiativeType.setBounds(391, 487, 90, 16);
		mainPanel.add(lblInitiativeType);

		comboBoxInitiativeType = new JComboBox();
		comboBoxInitiativeType.setModel(new DefaultComboBoxModel(InitiativeType
				.values()));
		comboBoxInitiativeType.setBounds(480, 484, 131, 22);
		mainPanel.add(comboBoxInitiativeType);

		JLabel label_1 = new JLabel("Buffer Size:");
		label_1.setBounds(160, 563, 67, 16);
		mainPanel.add(label_1);

		txtBufferSize = new JTextField();
		txtBufferSize.setColumns(10);
		txtBufferSize.setBounds(239, 560, 113, 22);
		mainPanel.add(txtBufferSize);

		JLabel label_2 = new JLabel("Presentation Display Rate (seconds):");
		label_2.setBounds(12, 598, 215, 16);
		mainPanel.add(label_2);

		txtPresentationDisplayRateInSeconds = new JTextField();
		txtPresentationDisplayRateInSeconds.setColumns(10);
		txtPresentationDisplayRateInSeconds.setBounds(239, 595, 113, 22);
		mainPanel.add(txtPresentationDisplayRateInSeconds);

		chckbxEnableLogging = new JCheckBox("Enable Logging?");
		chckbxEnableLogging.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				loggingEnabledChecked(a);
			}
		});

		chckbxEnableLogging.setBounds(401, 579, 158, 25);
		mainPanel.add(chckbxEnableLogging);

		lblFilePath = new JLabel();
		lblFilePath.setBounds(401, 609, 664, 27);
		mainPanel.add(lblFilePath);

		this.opacitySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 65);
		//opacitySlider.addChangeListener(this);
		opacitySlider.setMajorTickSpacing(10);
		opacitySlider.setMinorTickSpacing(1);
		opacitySlider.setBounds(659, 610, 158, 27);
		opacitySlider.addChangeListener(this);
		mainPanel.add(opacitySlider);
		
		lblOpacity = new JLabel("Opacity");
		lblOpacity.setBounds(659, 590, 90, 16);
		mainPanel.add(lblOpacity);
		
		// Kill the UI background threads when the window is closed.
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				if (publisher != null/*controller != null*/) {
					publisher.send(new PauseMessage());
					//controller.pause();
				}
			}
		});
		
		pauseButton.setEnabled(false);
		stopButton.setEnabled(false);
	}

	private void setOptions() {
		comboBoxGridSpacing.setSelectedItem(GridSpacingIncrement
				.getEnumVal(options.getGridSpacing()));
		txtTimeStepInMinutes.setText(Integer.toString(options
				.getTimeStepInMinutes()));
		txtBufferSize.setText(Integer.toString(options.getBufferSize()));
		txtPresentationDisplayRateInSeconds.setText(Float.toString(options
				.getPresentationDisplayRateInSeconds()));

		cbIsSimulationMultithreaded.setSelected(options
				.isSimulationMultithreaded());
		cbIsPresentationMultithreaded.setSelected(options
				.isPresentationMultithreaded());

		comboBoxInitiativeType.setSelectedItem(options.getInitiativeType());
	}

	private void getOptions() {
		options.setGridSpacing(((GridSpacingIncrement) comboBoxGridSpacing
				.getSelectedItem()).getNumVal());

		int val = 0;
		try {
			val = Integer.parseInt(txtTimeStepInMinutes.getText());
		} catch (NumberFormatException e) {
			val = 0;
		}
		if (val <= 0 || val > 1440) {
			val = 1;
			txtTimeStepInMinutes.setText("1");
		}
		options.setTimeStepInMinutes(val);

		try {
			val = Integer.parseInt(txtBufferSize.getText());
		} catch (NumberFormatException e) {
			val = 0;
		}
		if (val <= 0) {
			val = 1;
			txtBufferSize.setText("1");
		}
		options.setBufferSize(val);

		float valf = 0;
		try {
			valf = Float.parseFloat(txtPresentationDisplayRateInSeconds
					.getText());
		} catch (NumberFormatException e) {
			valf = 0;
		}
		if (valf <= 0) {
			valf = .5f;
			txtPresentationDisplayRateInSeconds.setText(".5");
		}
		options.setPresentationDisplayRateInSeconds(valf);

		options.setSimulationMultithreaded(cbIsSimulationMultithreaded
				.isSelected());
		options.setPresentationMultithreaded(cbIsPresentationMultithreaded
				.isSelected());

		options.setInitiativeType((InitiativeType) comboBoxInitiativeType
				.getSelectedItem());

		options.setLoggingEnabled(chckbxEnableLogging.isSelected());
		options.setLogFilePath(lblFilePath.getText() + "/");
		options.setOpacity(opacitySlider.getValue() * .01f);
	}

	private void updateInputs(boolean enableThem) {
		comboBoxGridSpacing.setEnabled(enableThem);
		txtTimeStepInMinutes.setEnabled(enableThem);
		txtBufferSize.setEnabled(enableThem);
		txtPresentationDisplayRateInSeconds.setEnabled(enableThem);
		cbIsSimulationMultithreaded.setEnabled(enableThem);
		cbIsPresentationMultithreaded.setEnabled(enableThem);
		comboBoxInitiativeType.setEnabled(enableThem);
		chckbxEnableLogging.setEnabled(enableThem);
	}

	private void startButtonPushed(ActionEvent evt) {
		if (!isPaused) {
			getOptions();
			
			//controller = SimulationFactory.createSimulation(options,
			//		presentationPanel);
			presentationPanel.drawGrid(options.getGridSpacing());

		}
		
		if (options.isPresentationMultithreaded()
				&& options.isSimulationMultithreaded()) {
			// If both are running in their own threads then no need for a silly
			// timer.
			//controller.run(1);
			publisher.send(new StopMessage());
			publisher.send(new StartMessage());
		} else {
			ActionListener taskPerformer = new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					//controller.run(1);
					publisher.send(new StopMessage());
					publisher.send(new StartMessage());
				}
			};
			uiThreadEventTimer = new Timer(delay, taskPerformer);
			uiThreadEventTimer.start();
		}

		startButton.setEnabled(false);
		pauseButton.setEnabled(true);
		stopButton.setEnabled(true);
		updateInputs(false);
	}

	private void pauseButtonPushed(ActionEvent evt) {
		if (uiThreadEventTimer != null) {
			uiThreadEventTimer.stop();
		}

		if (!isPaused) {
			publisher.send(new PauseMessage());
		}
		else{
			publisher.send(new ResumeMessage());
		}
		//controller.pause();

		startButton.setEnabled(true);
		pauseButton.setEnabled(false);
		stopButton.setEnabled(true);
		updateInputs(false);
		isPaused = true;
	}

	private void stopButtonPushed(ActionEvent evt) {
		pauseButtonPushed(null);
		if (uiThreadEventTimer != null) {
			uiThreadEventTimer.stop();
		}
		//controller.pause();
		publisher.send(new StopMessage());	
		
		startButton.setEnabled(true);
		pauseButton.setEnabled(false);
		stopButton.setEnabled(false);
		updateInputs(true);
		isPaused = false;
	}

	private void loggingEnabledChecked(ActionEvent evt) {
		if (chckbxEnableLogging.isSelected()) {
			JFileChooser j = new JFileChooser();
			j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			Integer returnVal = j.showOpenDialog(mainPanel);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				lblFilePath.setText(j.getSelectedFile().getAbsolutePath());
			}
		} else {
			lblFilePath.setText("");
		}
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		getOptions();
		this.presentationPanel.setMapOpacity(options.getOpacity());
	}
}