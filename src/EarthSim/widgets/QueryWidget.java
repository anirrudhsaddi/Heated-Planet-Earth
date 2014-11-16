package EarthSim.widgets;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class QueryWidget extends JPanel implements ActionListener{

    private static final long serialVersionUID = 1L;

    private HashMap<String, JTextField> inputs = new HashMap<String, JTextField>();
    private ArrayList<String> simulationList = new ArrayList<String>();
    GridLayout mainlayout = new GridLayout(3,0);

    public QueryWidget() {
	setBorder(BorderFactory.createTitledBorder("Query"));
	setLayout(new GridLayout());
	setAlignmentY(Component.RIGHT_ALIGNMENT);

	JScrollPane listScrollPane = new JScrollPane(list("Simulation List"));
	add(listScrollPane, BorderLayout.WEST);
	add(inputPanel(), BorderLayout.EAST);
	
	
    }

    private JList<?> list(String string) {
	// TODO Auto-generated method stub
	for(int i =0;i<15;i++){
	    simulationList.add(""+ i);
	}
	String[] array = new String[simulationList.size()];
	for(int i = 0; i < array.length; i++) {
	    array[i] = simulationList.get(i);
	}
	JList slBox = new JList(array);
	return slBox;
    }

    private JPanel inputPanel(){

	JPanel inputsPanel = new JPanel();

	inputsPanel.setLayout(new BoxLayout(inputsPanel, BoxLayout.Y_AXIS));
	inputsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
	
	add(inputField("Start time", ""));
	add(inputField("End time",""));
	add(inputField("Top longitude",""));
	add(inputField("Btm longitude", ""));
	add(inputField("West latitude",""));
	add(inputField("East latitude",""));

	return inputsPanel;
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
	t.setEnabled(false);
	inputPanel.add(t);

//	inputs.put(name, t);
	return inputPanel;
    }



    @Override
    public void actionPerformed(ActionEvent arg0) {
	// TODO Auto-generated method stub

    }
}