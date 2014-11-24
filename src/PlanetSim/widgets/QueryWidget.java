package PlanetSim.widgets;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


//import org.jdatepicker.constraints.RangeConstraint;
//import org.jdatepicker.impl.JDatePanelImpl;
//import org.jdatepicker.impl.JDatePickerImpl;
//import org.jdatepicker.impl.UtilCalendarModel;
//import org.jdatepicker.impl.UtilDateModel;
//import org.jdatepicker.*;










import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import PlanetSim.QueryEngine;
import common.Constants;

public class QueryWidget extends JPanel {

	private static final long			serialVersionUID	= 1L;
	private QueryEngine					queryEngine;

	private final Integer[]				hours				= new Integer[24];
	private final Integer[]				minutes				= new Integer[60];
	
	//private final RangeConstraint dateRangeConstraint;
//	private JDatePanelImpl startDate;
//	private JDatePanelImpl	endDate;
	//private JDatePickerImpl startDatePicker, endDatePicker;

	private GridLayout					mainlayout			= new GridLayout(3, 0);

	// private JTextField textFieldEndTime, textFieldStartTime;
	private JComboBox<Integer>			endHour, startHour, endMinute, startMinute;

	private JTextField					textFieldNorthLongitude;
	private JTextField					textFieldSouthLongitude;
	private JTextField					textFieldWestLatitude;
	private JTextField					textFieldEastLatitude;
	private JTextField					textFieldSimulationName;

	private JList<?>					slBox;

	private JCheckBox					chckbxMinimumTemperature, chckbxMaximumTemperature,
			chckbxMeanTemperatureOverTime, chckbxMeanTemperatureOverRegion, chckbxActualValues;

	private HashMap<String, JTextField>	inputs				= new HashMap<String, JTextField>();
	private HashMap<String, JCheckBox>	checkBoxes				= new HashMap<String, JCheckBox>();
	
	public QueryWidget() {

		for (int i = 0; i < 24; i++)
			hours[i] = i;

		for (int i = 0; i < 60; i++)
			minutes[i] = i;
		
		Calendar startRange = (Calendar) Constants.START_DATE.clone();
		Calendar endRange = (Calendar) Constants.START_DATE.clone();
		endRange.add(Calendar.MONTH, Constants.MAX_SIM_LEN);
	//	dateRangeConstraint = new RangeConstraint(startRange, endRange);

		setBorder(BorderFactory.createTitledBorder("Query"));
		setLayout(new GridLayout());
		setAlignmentY(Component.RIGHT_ALIGNMENT);

		JScrollPane listScrollPane = new JScrollPane(slBox);
		listScrollPane.setPreferredSize(new Dimension(50, this.getHeight()));
		add(listScrollPane, BorderLayout.EAST);
		add(inputFields());

	}

	private JList<?> list(String string) {

		try {
			queryEngine = new QueryEngine();
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}

		slBox = queryEngine.getSimulationList();
		slBox.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				String selection = (String) slBox.getSelectedValue();
				System.out.println("Selection in Jlist: " + selection);
				setFields(true);
			}
		});
		return slBox;
	}

	private JPanel inputFields() {

		JPanel inputPanel = new JPanel();
		inputPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		inputPanel.setLayout(null);

		JLabel lblHours = new JLabel("Hrs");
		lblHours.setBounds(285, 0, 50, 10);
		inputPanel.add(lblHours);
		
		JLabel lblMins = new JLabel("Mins");
		lblMins.setBounds(335, 0, 50, 10);
		inputPanel.add(lblMins);
		
		JLabel lblStartTime = new JLabel("Start Time");
		lblStartTime.setBounds(10, 15, 130, 15);
		inputPanel.add(lblStartTime);
		
		

		// startTime = new JPanel();
		// startTime.setBounds(145, 10, 114, 19);
		// startTime.setEnabled(false);
		// startTime.setColumns(10);
		//
		// inputPanel.add(textFieldStartTime);
		// inputs.put("Start Time", textFieldStartTime);

		startHour = new JComboBox<Integer>(hours);
		startMinute = new JComboBox<Integer>(minutes);
		
//		startDate = new JDatePanelImpl(new UtilCalendarModel((Calendar) Constants.START_DATE.clone()));
//		startDate.addDateSelectionConstraint(this.dateRangeConstraint);
//		startDatePicker = new JDatePickerImpl(startDate);
		
// 		startDatePicker.setBounds(145, 10, 95, 19);
//		startHour.setBounds(250, 10, 10, 19);
//		startMinute.setBounds(260, 10, 10, 19);
//    	inputPanel.add(startHour);
//		inputPanel.add(startMinute);
//		inputPanel.add(startDatePicker);
//		startDate = new JDatePanelImpl(new UtilCalendarModel((Calendar) Constants.START_DATE.clone()));
//	    startDate.addDateSelectionConstraint(this.dateRangeConstraint);
//		startDatePicker = new JDatePickerImpl(startDate);
		UtilDateModel startModel = new UtilDateModel();
		JDatePanelImpl startDatepanel = new JDatePanelImpl(startModel);
		JDatePickerImpl startDatePicker = new JDatePickerImpl(startDatepanel);
		startDatePicker.setBounds(145, 10, 135, 25);
		inputPanel.add(startDatePicker);
		
		startHour.setBounds(285, 10, 50, 25);
		startMinute.setBounds(335, 10, 50, 25);
    	inputPanel.add(startHour);
		inputPanel.add(startMinute);

		endHour = new JComboBox<Integer>(hours);
		endMinute = new JComboBox<Integer>(minutes);
		UtilDateModel endModel = new UtilDateModel();
		JDatePanelImpl endDatePanel = new JDatePanelImpl(endModel);
		JDatePickerImpl endDatePicker = new JDatePickerImpl(endDatePanel);
		endDatePicker.setBounds(145, 40, 135, 25);
		inputPanel.add(endDatePicker);
		
		endHour.setBounds(285, 40, 50, 25);
		endMinute.setBounds(335, 40, 50, 25);
    	inputPanel.add(endHour);
		inputPanel.add(endMinute);
		
		
//		endDate = new JDatePanelImpl(new UtilCalendarModel((Calendar) Constants.START_DATE.clone()));
//		endDate.addDateSelectionConstraint(this.dateRangeConstraint);
//		endDatePicker = new JDatePickerImpl(endDate);

		JLabel lblEndTime = new JLabel("End Time");
		lblEndTime.setBounds(10, 40, 130, 15);
		inputPanel.add(lblEndTime);

		// textFieldEndTime = new JTextField();
		// textFieldEndTime.setBounds(145, 35, 114, 19);
		// textFieldEndTime.setColumns(10);
		// textFieldEndTime.setEnabled(false);
		// inputPanel.add(textFieldEndTime);
		// inputs.put("End Time", textFieldEndTime);

		JLabel lblNorthLongitude = new JLabel("North Longitude");
		lblNorthLongitude.setBounds(10, 70, 130, 15);
		inputPanel.add(lblNorthLongitude);

		textFieldNorthLongitude = new JTextField();
		textFieldNorthLongitude.setBounds(145, 70, 114, 19);
		textFieldNorthLongitude.setEnabled(true);
		textFieldNorthLongitude.setColumns(10);
		inputPanel.add(textFieldNorthLongitude);
		inputs.put("North Longitude", textFieldNorthLongitude);

		JLabel lblSouthLongitude = new JLabel("South Longitude");
		lblSouthLongitude.setBounds(10, 95, 130, 15);
		inputPanel.add(lblSouthLongitude);

		textFieldSouthLongitude = new JTextField();
		textFieldSouthLongitude.setColumns(10);
		textFieldSouthLongitude.setBounds(145, 90, 114, 19);
		textFieldSouthLongitude.setEnabled(true);
		inputPanel.add(textFieldSouthLongitude);
		inputs.put("South Longitude", textFieldSouthLongitude);

		JLabel lblWestLatitude = new JLabel("West Latitude");
		lblWestLatitude.setBounds(10, 115, 114, 15);
		inputPanel.add(lblWestLatitude);

		textFieldWestLatitude = new JTextField();
		textFieldWestLatitude.setBounds(145, 115, 114, 19);
		textFieldWestLatitude.setEnabled(true);
		textFieldWestLatitude.setColumns(10);
		inputPanel.add(textFieldWestLatitude);
		inputs.put("West Latitude", textFieldWestLatitude);

		JLabel lblEastLatitude = new JLabel("East Latitude");
		lblEastLatitude.setBounds(12, 140, 130, 15);
		inputPanel.add(lblEastLatitude);

		textFieldEastLatitude = new JTextField();
		textFieldEastLatitude.setBounds(145, 140, 114, 19);
		textFieldEastLatitude.setEnabled(true);
		textFieldEastLatitude.setColumns(10);
		inputPanel.add(textFieldEastLatitude);
		inputs.put("East Latitude", textFieldEastLatitude);
		/*
		 * JLabel lblSimulationName = new JLabel("Simulation Name");
		 * lblSimulationName.setBounds(12, 160, 130, 15);
		 * inputPanel.add(lblSimulationName);
		 * 
		 * textFieldSimulationName = new JTextField();
		 * textFieldSimulationName.setBounds(145, 160, 114, 19);
		 * textFieldSimulationName.setEnabled(false);
		 * textFieldSimulationName.setColumns(10);
		 * inputPanel.add(textFieldSimulationName);
		 * inputs.put("Simulation Name", textFieldSimulationName);
		 */
		chckbxMinimumTemperature = new JCheckBox("Minimum Temperature");
		chckbxMinimumTemperature.setBounds(10, 185, 249, 20);
		inputPanel.add(chckbxMinimumTemperature);
		checkBoxes.put("Minimum Temp", chckbxMinimumTemperature);
		
		
		chckbxMaximumTemperature = new JCheckBox("Maximum Temperature");
		chckbxMaximumTemperature.setBounds(10, 210, 249, 20);
		inputPanel.add(chckbxMaximumTemperature);
		checkBoxes.put("Maximum Temp", chckbxMaximumTemperature);
		
		chckbxMeanTemperatureOverTime = new JCheckBox("Mean Temperature over Time");
		chckbxMeanTemperatureOverTime.setBounds(10, 235, 249, 23);
		inputPanel.add(chckbxMeanTemperatureOverTime);
		checkBoxes.put("Mean Time Temp", chckbxMeanTemperatureOverTime);
		
		chckbxMeanTemperatureOverRegion = new JCheckBox("Mean Temperature over Region");
		chckbxMeanTemperatureOverRegion.setBounds(10, 260, 249, 23);
		inputPanel.add(chckbxMeanTemperatureOverRegion);
		checkBoxes.put("Mean Region Temp", chckbxMeanTemperatureOverRegion);
		
		chckbxActualValues = new JCheckBox("Actual Values");
		chckbxActualValues.setBounds(10, 285, 249, 23);
		inputPanel.add(chckbxActualValues);
		checkBoxes.put("Actual Values", chckbxActualValues);
		
		return inputPanel;
	}

	public void setFields(boolean enabled) {

		if (!enabled) {
			// textFieldStartTime.setEnabled(false);
			textFieldEastLatitude.setEnabled(false);
			textFieldWestLatitude.setEnabled(false);
			textFieldSouthLongitude.setEnabled(false);
			textFieldNorthLongitude.setEnabled(false);
			// textFieldEndTime.setEnabled(false);
		} else if (!enabled) {
			// textFieldStartTime.setEnabled(true);
			textFieldEastLatitude.setEnabled(true);
			textFieldWestLatitude.setEnabled(true);
			textFieldSouthLongitude.setEnabled(true);
			textFieldNorthLongitude.setEnabled(true);
			// textFieldEndTime.setEnabled(true);
		}

	}

	public void getRequiredValues() {

		Boolean min, max, time, region, actualValues;

		min = chckbxMinimumTemperature.isSelected();
		max = chckbxMaximumTemperature.isSelected();
		time = chckbxMeanTemperatureOverTime.isSelected();
		region = chckbxMeanTemperatureOverRegion.isSelected();
		actualValues = chckbxActualValues.isSelected();
		// TODO: Method to access database asking for these values to be
		// computed.

	}

	public String GetUserInputs(String name) {
		return inputs.get(name).getText();
	}
	
	public boolean GetCheckBox(String name) {
		return checkBoxes.get(name).isSelected();
	}
	
//	public Calendar getSelectedStartDate() {
//		return (Calendar) this.startDatePicker.getModel().getValue();
//	}
//	
//	public Calendar getSelectedEndDate() {
//		return (Calendar) this.endDatePicker.getModel().getValue();
//	}
}
