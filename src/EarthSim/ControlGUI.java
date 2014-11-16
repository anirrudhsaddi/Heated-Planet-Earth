// GUI.java
package EarthSim;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import EarthSim.widgets.ControlWidget;
import EarthSim.widgets.QueryWidget;


public class ControlGUI extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 6146431536208036768L;

    //private HashMap<String, JTextField> inputs = new HashMap<String, JTextField>();
    ControlWidget controlWidget;
    QueryWidget queryWidget;


    public ControlGUI() {

	// make widgets
	setupWindow();
	pack();
    }

    private void setupWindow() {

	// setup overall app ui
	setTitle("Heated Planet Diffusion Simulation");

	setSize(300, 400);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	setLayout(new BorderLayout());
	setLocationRelativeTo(null);

	lowerRightWindow(); // Set window location to lower right (so we don't hide dialogs)
	setAlwaysOnTop(true);

	add(settingsAndControls(), BorderLayout.WEST);
	add(query(), BorderLayout.CENTER);
    }

    private void lowerRightWindow() {
	Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	int x = (int) (dimension.getWidth() - this.getWidth());
	int y = (int) (dimension.getHeight() - this.getHeight());
	this.setLocation(x, y);
    }

    private JPanel settingsAndControls() {

	JPanel sncPanel = new JPanel();
	sncPanel.setLayout(new BoxLayout(sncPanel, BoxLayout.PAGE_AXIS));
	sncPanel.setAlignmentY(Component.TOP_ALIGNMENT);

	controlWidget = new ControlWidget();

	sncPanel.add(controlWidget.getSettingsWidget(), BorderLayout.WEST);
	sncPanel.add(controlWidget, BorderLayout.WEST);


	return sncPanel;
    }

    private JPanel query(){

	JPanel queryPanel = new JPanel();
	queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.PAGE_AXIS));
	queryPanel.setAlignmentY(Component.TOP_ALIGNMENT);
	
	queryWidget = new QueryWidget();
	
	queryPanel.add(queryWidget,BorderLayout.CENTER);
	//queryPanel.add()

	return queryPanel;
    }
}
