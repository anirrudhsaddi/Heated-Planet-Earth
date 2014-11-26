package PlanetSim.widgets;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SettingsWidget extends JPanel {
	
	private static final long			serialVersionUID		= 6146431536208036768L;
	private static final int			DEFAULT_GRID_SPACING	= 15;
	private static final int			DEFAULT_TIME_STEP		= 1440;
	private static final int			DEFAULT_SIM_LEN			= 12;
	private static final float			DEFAULT_PRESENTATION	= 1f;
	private final static float			DEFAULT_AXIS_TILT		= 23.44f;
	private static final float			DEFAULT_ECCENTRICITY	= 0.0167f;

	private HashMap<String, JTextField>	inputs					= new HashMap<String, JTextField>();
	private JCheckBox					chckbxDisplayAnimation;

	public SettingsWidget() {
		
		setBorder(BorderFactory.createTitledBorder("Settings"));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setAlignmentY(Component.TOP_ALIGNMENT);

		add(inputField("Simulation Name", ""));
		add(inputField("Grid Spacing", Integer.toString(DEFAULT_GRID_SPACING)));
		add(inputField("Simulation Time Step", Integer.toString(DEFAULT_TIME_STEP)));
		add(inputField("Presentation Rate", Float.toString(DEFAULT_PRESENTATION)));
		add(inputField("Simulation Length", Integer.toString(DEFAULT_SIM_LEN)));
		add(inputField("Axis Tilt", Float.toString(DEFAULT_AXIS_TILT)));
		add(inputField("Orbital Eccentricity", Float.toString(DEFAULT_ECCENTRICITY)));
		add(checkBox());
	}

	private JPanel inputField(String name, String defaultText) {

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new FlowLayout());
		inputPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

		JLabel l = new JLabel(name);
		l.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputPanel.add(l);

		JTextField t = new JTextField(defaultText, 10);
		t.setAlignmentX(Component.RIGHT_ALIGNMENT);
		l.setLabelFor(t);
		inputPanel.add(t);

		inputs.put(name, t);
		return inputPanel;
	}

	private JPanel checkBox() {

		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new FlowLayout());
		checkBoxPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

		chckbxDisplayAnimation = new JCheckBox("DisplayAnimation");
		chckbxDisplayAnimation.setSelected(true);
		chckbxDisplayAnimation.setBounds(5, 5, 100, 15);
		checkBoxPanel.add(chckbxDisplayAnimation);

		return checkBoxPanel;
	}

	public Boolean getDisplayAnimationStatus() {
		return chckbxDisplayAnimation.isSelected();
	}

	public String getInputText(String InputName) {
		return inputs.get(InputName).getText();
	}
	
	public void setInputText(String InputName, String value) {
		inputs.get(InputName).setText(value);
	}
}