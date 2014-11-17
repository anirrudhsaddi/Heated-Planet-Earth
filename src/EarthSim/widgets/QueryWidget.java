package EarthSim.widgets;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.JCheckBox;

public class QueryWidget extends JPanel implements ActionListener{

    private static final long serialVersionUID = 1L;

    private HashMap<String, JTextField> inputs = new HashMap<String, JTextField>();
    private ArrayList<String> simulationList = new ArrayList<String>();
    GridLayout mainlayout = new GridLayout(3,0);
    private JTextField textFieldEndTime, textFieldStartTime;
    private JTextField textFieldNorthLongitude;
    private JTextField textFieldSouthLongitude;
    private JTextField textFieldWestLatitude;
    private JTextField textFieldEastLatitude;
    private JList slBox;

    public QueryWidget() {
	setBorder(BorderFactory.createTitledBorder("Query"));
	setLayout(new GridLayout());
	setAlignmentY(Component.RIGHT_ALIGNMENT);

	JScrollPane listScrollPane = new JScrollPane(list("Simulation List"));
	listScrollPane.setPreferredSize(new Dimension(50,this.getHeight())); 
	add(listScrollPane, BorderLayout.EAST);
	add(inputFields());
		
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
	slBox = new JList(array);
	return slBox;
    }

  
    private JPanel inputFields() {

	JPanel inputPanel = new JPanel();
	inputPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
	inputPanel.setLayout(null);

	JLabel lblStartTime = new JLabel("Start Time");
	lblStartTime.setBounds(10, 10, 130, 15);
	inputPanel.add(lblStartTime);

	textFieldStartTime = new JTextField();
	textFieldStartTime.setBounds(145, 10, 114, 19);
	textFieldStartTime.setEnabled(false);
	textFieldStartTime.setColumns(10);
	inputPanel.add(textFieldStartTime);
	
	JLabel lblEndTime = new JLabel("End Time");
	lblEndTime.setBounds(10, 35, 130, 15);
	inputPanel.add(lblEndTime);
	
	textFieldEndTime = new JTextField();
	textFieldEndTime.setBounds(145, 35, 114, 19);
	textFieldEndTime.setColumns(10);
	textFieldEndTime.setEnabled(false);
	inputPanel.add(textFieldEndTime);
	
	JLabel lblNorthLongitude = new JLabel("North Longitude");
	lblNorthLongitude.setBounds(10, 60, 130, 15);
	inputPanel.add(lblNorthLongitude);
	
	textFieldNorthLongitude = new JTextField();
	textFieldNorthLongitude.setBounds(145, 60, 114, 19);
	textFieldNorthLongitude.setEnabled(false);
	textFieldNorthLongitude.setColumns(10);
	inputPanel.add(textFieldNorthLongitude);
	
	JLabel lblSouthLongitude = new JLabel("South Longitude");
	lblSouthLongitude.setBounds(10, 85, 130, 15);
	inputPanel.add(lblSouthLongitude);
	
	textFieldSouthLongitude = new JTextField();
	textFieldSouthLongitude.setColumns(10);
	textFieldSouthLongitude.setBounds(145, 85, 114, 19);
	textFieldSouthLongitude.setEnabled(false);
	inputPanel.add(textFieldSouthLongitude);
	
	JLabel lblWestLatitude = new JLabel("West Latitude");
	lblWestLatitude.setBounds(10, 110, 114, 15);
	inputPanel.add(lblWestLatitude);
	
	textFieldWestLatitude = new JTextField();
	textFieldWestLatitude.setBounds(145, 110, 114, 19);
	textFieldWestLatitude.setEnabled(false);
	textFieldWestLatitude.setColumns(10);
	inputPanel.add(textFieldWestLatitude);
	
	JLabel lblEastLatitude = new JLabel("East Latitude");
	lblEastLatitude.setBounds(12, 135, 130, 15);
	inputPanel.add(lblEastLatitude);
	
	textFieldEastLatitude = new JTextField();
	textFieldEastLatitude.setBounds(145, 135, 114, 19);
	textFieldEastLatitude.setEnabled(false);
	textFieldEastLatitude.setColumns(10);
	inputPanel.add(textFieldEastLatitude);
	
	JCheckBox chckbxMinimumTeperature = new JCheckBox("Minimum Temperature");
	chckbxMinimumTeperature.setBounds(10, 172, 249, 20);
	inputPanel.add(chckbxMinimumTeperature);
	
	JCheckBox chckbxMaximumTemperature = new JCheckBox("Maximum Temperature");
	chckbxMaximumTemperature.setBounds(10, 199, 249, 20);
	inputPanel.add(chckbxMaximumTemperature);
	
	JCheckBox chckbxMeanTemperatureOverTime = new JCheckBox("Mean Temperature over Time");
	chckbxMeanTemperatureOverTime.setBounds(10, 223, 249, 23);
	inputPanel.add(chckbxMeanTemperatureOverTime);
	
	JCheckBox chckbxMeanTemperatureOverRegion = new JCheckBox("Mean Temperature over Region");
	chckbxMeanTemperatureOverRegion.setBounds(10, 247, 249, 23);
	inputPanel.add(chckbxMeanTemperatureOverRegion);

	return inputPanel;
    }



    @Override
    public void actionPerformed(ActionEvent arg0) {
	// TODO Auto-generated method stub

    }
}