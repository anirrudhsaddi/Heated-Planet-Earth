package PlanetSim.widgets;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ControlWidget extends JPanel {
	
	private static final long			serialVersionUID	= 6146431536208036768L;

	private HashMap<String, JButton>	buttons				= new HashMap<String, JButton>();

	public ControlWidget(ActionListener listener) {

		setAlignmentX(Component.RIGHT_ALIGNMENT);

		add(button("Start", listener));
		add(button("Pause", listener));
		add(button("Resume", listener));
		add(button("Stop", listener));
		add(button("Query", listener));
		add(button("Run", listener));

		buttons.get("Start").setEnabled(true);
		buttons.get("Pause").setEnabled(false);
		buttons.get("Resume").setEnabled(false);
		buttons.get("Stop").setEnabled(false);
		buttons.get("Query").setEnabled(true);
		buttons.get("Run").setEnabled(false);

	}

	private JButton button(String name, ActionListener listener) {

		JButton button = new JButton(name);
		button.setActionCommand(name);
		button.addActionListener(listener);
		buttons.put(name, button);
		return button;
	}

	public void disableButtonsBasedOnAction(String actionName) {
		
		if (actionName == "Start") {
			buttons.get("Start").setEnabled(false);
			buttons.get("Pause").setEnabled(true);
			buttons.get("Resume").setEnabled(false);
			buttons.get("Stop").setEnabled(true);
			buttons.get("Query").setEnabled(false);
			buttons.get("Run").setEnabled(false);
		} else if (actionName == "Pause") {
			buttons.get("Pause").setEnabled(false);
			buttons.get("Resume").setEnabled(true);
		} else if (actionName == "Resume") {
			buttons.get("Pause").setEnabled(true);
			buttons.get("Resume").setEnabled(false);
		} else if (actionName == "Stop") {
			buttons.get("Start").setEnabled(true);
			buttons.get("Pause").setEnabled(false);
			buttons.get("Resume").setEnabled(false);
			buttons.get("Stop").setEnabled(false);
			buttons.get("Query").setEnabled(true);
			buttons.get("Run").setEnabled(false);
		} else if (actionName == "Query") {
			buttons.get("Start").setEnabled(false);
			buttons.get("Pause").setEnabled(false);
			buttons.get("Resume").setEnabled(false);
			buttons.get("Stop").setEnabled(false);
			buttons.get("Run").setEnabled(true);
		} else if (actionName == "Run") {
			buttons.get("Start").setEnabled(false);
			buttons.get("Pause").setEnabled(true);
			buttons.get("Resume").setEnabled(false);
			buttons.get("Stop").setEnabled(true);
			buttons.get("Query").setEnabled(false);
			buttons.get("Run").setEnabled(false);
		}
	}
}