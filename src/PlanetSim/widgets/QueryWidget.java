package PlanetSim.widgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import PlanetSim.QueryEngine;
import common.Constants;

public class QueryWidget extends JPanel {

	private static final long			serialVersionUID	= 1L;

	private final Integer[]				hours				= new Integer[24];
	private final Integer[]				minutes				= new Integer[60];

	private JComboBox					endHour, startHour, endMinute, startMinute;

	private JTextField					textFieldWestLongitude;
	private JTextField					textFieldEastLongitude;
	private JTextField					textFieldNorthLatitude;
	private JTextField					textFieldSouthLatitude;

	@SuppressWarnings("rawtypes")
	private JList						slBox;

	private JCheckBox					chckbxMinimumTemperature, chckbxMaximumTemperature,
			chckbxMeanTemperatureOverTime, chckbxMeanTemperatureOverRegion, chckbxActualValues;

	private SettingsWidget				settings;
	private QueryEngine					engine;

	private HashMap<String, JTextField>	inputs				= new HashMap<String, JTextField>();
	private HashMap<String, JCheckBox>	checkBoxes			= new HashMap<String, JCheckBox>();
	private HashMap<String, JComboBox>	comboBoxes			= new HashMap<String, JComboBox>();

	private UtilDateModel				startModel, endModel;
	private JDatePanelImpl				startDatepanel, endDatePanel;
	private JDatePickerImpl				startDatePicker, endDatePicker;
	private Date						startDate, endDate;
	private JLabel						lblSimNames;

	public QueryWidget(QueryEngine engine, SettingsWidget settings) {

		this.engine = engine;
		this.settings = settings;

		for (int i = 0; i < 24; i++)
			hours[i] = i;

		for (int i = 0; i < 60; i++)
			minutes[i] = i;

		this.setBorder(BorderFactory.createTitledBorder("Query"));
		this.setAlignmentY(Component.RIGHT_ALIGNMENT);

		initJList();
		setLayout(null);
		JScrollPane listScrollPane = new JScrollPane(slBox);
		listScrollPane.setLocation(6, 18);
		listScrollPane.setSize(238, 403);
		listScrollPane.setPreferredSize(new Dimension(35, 300));
		this.add(listScrollPane);

		lblSimNames = new JLabel("Simulation Names");
		listScrollPane.setColumnHeaderView(lblSimNames);
		this.add(inputFields());

	}

	@SuppressWarnings("unchecked")
	public void query(int gridSpacing, int timeStep, int simulationLength, float presentationInterval, float axisTilt,
			float orbitalEccentricity) {

		// If there are no name selections, find names by physical data
		// otherwise, ignore - the valueChanged handler will populate the
		// name's physical data
		try {
			slBox.setListData(engine.getSimulationsByData(gridSpacing, timeStep, simulationLength,
					presentationInterval, axisTilt, orbitalEccentricity));
		} catch (Exception e) {
			ShowMessage("Unable to query for a list of matching Simulations. Error(" + e + ")");
		}
	}

	@SuppressWarnings("unchecked")
	public void updateQList() {
		try {
			slBox.setListData(this.engine.getSimulationList());
		} catch (Exception e) {
			ShowMessage("Unable update list of Simulation Names. Error(" + e + ")");
		}
	}

	@SuppressWarnings("rawtypes")
	private void initJList() {

		slBox = new JList();
		slBox.setBorder(new LineBorder(new Color(0, 0, 0)));
		slBox.setPreferredSize(new Dimension(35, 300));
		slBox.setSize(35, 300);

		this.updateQList();
		slBox.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {

				if (!slBox.isSelectionEmpty()) {
					
					String simulationName = (String) slBox.getSelectedValue();
					try {

						Hashtable<String, String> result = engine.getSimulationPhysicalParameters(simulationName);

						if (result.isEmpty()) {
							JOptionPane.showMessageDialog(getRootPane(), "No data available");
							return;
						}

						for (String key : result.keySet())
							settings.setInputText(key, result.get(key));

					} catch (Exception e) {
						ShowMessage("Unable to query for Simulation Physical Data. Error(" + e + ")");
					}

					setFields(true);
				}
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JPanel inputFields() {

		JPanel inputPanel = new JPanel();
		inputPanel.setBounds(256, 18, 367, 403);
		inputPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		inputPanel.setLayout(null);

		JLabel lblStartHours = new JLabel("Hrs");
		lblStartHours.setBounds(223, 83, 22, 10);
		inputPanel.add(lblStartHours);

		JLabel lblStartMins = new JLabel("Mins");
		lblStartMins.setBounds(316, 83, 30, 10);
		inputPanel.add(lblStartMins);

		JLabel lblStartDate = new JLabel("Start Time");
		lblStartDate.setBounds(19, 16, 114, 15);
		inputPanel.add(lblStartDate);

		startHour = new JComboBox(hours);
		startHour.setSelectedIndex(0);
		startMinute = new JComboBox(minutes);
		startMinute.setSelectedIndex(0);

		startModel = new UtilDateModel((Date) Constants.START_DATE.getTime());
		startDatepanel = new JDatePanelImpl(startModel);
		startDatePicker = new JDatePickerImpl(startDatepanel);
		startDatePicker.setBounds(145, 10, 135, 25);
		inputPanel.add(startDatePicker);

		startHour.setBounds(144, 76, 71, 25);
		comboBoxes.put("Start Hour", startHour);
		startMinute.setBounds(246, 76, 68, 25);
		comboBoxes.put("Start Minute", startMinute);
		inputPanel.add(startHour);
		inputPanel.add(startMinute);

		endHour = new JComboBox(hours);
		startMinute.setSelectedIndex(0);
		endMinute = new JComboBox(minutes);
		endMinute.setSelectedIndex(0);

		comboBoxes.put("End Hour", endHour);
		comboBoxes.put("End Minute", endMinute);
		
		endModel = new UtilDateModel(Constants.START_DATE.getTime());
		endDatePanel = new JDatePanelImpl(endModel);
		endDatePicker = new JDatePickerImpl(endDatePanel);
		endDatePicker.setBounds(145, 40, 135, 25);
		inputPanel.add(endDatePicker);

		endHour.setBounds(144, 113, 71, 25);
		endMinute.setBounds(246, 113, 68, 25);
		inputPanel.add(endHour);
		inputPanel.add(endMinute);

		JLabel lblEndDate = new JLabel("End Date");
		lblEndDate.setBounds(19, 43, 114, 15);
		inputPanel.add(lblEndDate);

		JLabel lblWestLongitude = new JLabel("West Longitude");
		lblWestLongitude.setBounds(19, 153, 114, 15);
		inputPanel.add(lblWestLongitude);

		textFieldWestLongitude = new JTextField();
		textFieldWestLongitude.setBounds(145, 150, 114, 19);
		textFieldWestLongitude.setEnabled(true);
		textFieldWestLongitude.setColumns(10);
		inputPanel.add(textFieldWestLongitude);
		inputs.put("West Longitude", textFieldWestLongitude);

		JLabel lblEastLongitude = new JLabel("East Longitude");
		lblEastLongitude.setBounds(19, 177, 114, 15);
		inputPanel.add(lblEastLongitude);

		textFieldEastLongitude = new JTextField();
		textFieldEastLongitude.setColumns(10);
		textFieldEastLongitude.setBounds(145, 173, 114, 19);
		textFieldEastLongitude.setEnabled(true);
		inputPanel.add(textFieldEastLongitude);
		inputs.put("East Longitude", textFieldEastLongitude);

		JLabel lblNorthLatitude = new JLabel("North Latitude");
		lblNorthLatitude.setBounds(19, 200, 114, 15);
		inputPanel.add(lblNorthLatitude);

		textFieldNorthLatitude = new JTextField();
		textFieldNorthLatitude.setBounds(145, 196, 114, 19);
		textFieldNorthLatitude.setEnabled(true);
		textFieldNorthLatitude.setColumns(10);
		inputPanel.add(textFieldNorthLatitude);
		inputs.put("North Latitude", textFieldNorthLatitude);

		JLabel lblSouthLatitude = new JLabel("South Latitude");
		lblSouthLatitude.setBounds(19, 223, 114, 15);
		inputPanel.add(lblSouthLatitude);

		textFieldSouthLatitude = new JTextField();
		textFieldSouthLatitude.setBounds(145, 219, 114, 19);
		textFieldSouthLatitude.setEnabled(true);
		textFieldSouthLatitude.setColumns(10);
		inputPanel.add(textFieldSouthLatitude);
		inputs.put("South Latitude", textFieldSouthLatitude);

		chckbxMinimumTemperature = new JCheckBox("Minimum Temperature");
		chckbxMinimumTemperature.setBounds(10, 262, 249, 20);
		inputPanel.add(chckbxMinimumTemperature);
		checkBoxes.put("Minimum Temp", chckbxMinimumTemperature);

		chckbxMaximumTemperature = new JCheckBox("Maximum Temperature");
		chckbxMaximumTemperature.setBounds(10, 287, 249, 20);
		inputPanel.add(chckbxMaximumTemperature);
		checkBoxes.put("Maximum Temp", chckbxMaximumTemperature);

		chckbxMeanTemperatureOverTime = new JCheckBox("Mean Temperature over Time");
		chckbxMeanTemperatureOverTime.setBounds(10, 312, 249, 23);
		inputPanel.add(chckbxMeanTemperatureOverTime);
		checkBoxes.put("Mean Time Temp", chckbxMeanTemperatureOverTime);

		chckbxMeanTemperatureOverRegion = new JCheckBox("Mean Temperature over Region");
		chckbxMeanTemperatureOverRegion.setBounds(10, 337, 249, 23);
		inputPanel.add(chckbxMeanTemperatureOverRegion);
		checkBoxes.put("Mean Region Temp", chckbxMeanTemperatureOverRegion);

		chckbxActualValues = new JCheckBox("Actual Values");
		chckbxActualValues.setBounds(10, 362, 249, 23);
		inputPanel.add(chckbxActualValues);
		checkBoxes.put("Actual Values", chckbxActualValues);

		JLabel lblStartTime = new JLabel("Start Time");
		lblStartTime.setBounds(19, 81, 114, 16);
		inputPanel.add(lblStartTime);

		JLabel lblEndTime = new JLabel("End Time");
		lblEndTime.setBounds(19, 119, 114, 16);
		inputPanel.add(lblEndTime);

		JLabel lblEndHours = new JLabel("Hrs");
		lblEndHours.setBounds(223, 120, 22, 10);
		inputPanel.add(lblEndHours);

		JLabel lblEndMins = new JLabel("Mins");
		lblEndMins.setBounds(316, 120, 30, 10);
		inputPanel.add(lblEndMins);

		inputPanel.setPreferredSize(new Dimension(400, 300));
		return inputPanel;
	}

	public void setFields(boolean enabled) {

		if (!enabled) {

			textFieldSouthLatitude.setEnabled(false);
			textFieldNorthLatitude.setEnabled(false);
			textFieldEastLongitude.setEnabled(false);
			textFieldWestLongitude.setEnabled(false);

		} else if (!enabled) {

			textFieldSouthLatitude.setEnabled(true);
			textFieldNorthLatitude.setEnabled(true);
			textFieldEastLongitude.setEnabled(true);
			textFieldWestLongitude.setEnabled(true);
		}

	}

	public String GetUserInputs(String name) {
		return inputs.get(name).getText();
	}

	public boolean GetCheckBox(String name) {
		return checkBoxes.get(name).isSelected();
	}

	public String GetComboBox(String name) {
		return comboBoxes.get(name).getSelectedItem().toString();
	}

	public Calendar getSelectedStartDate() {

		Calendar startCal = Calendar.getInstance();
		startDate = (Date) this.startDatePicker.getModel().getValue();
		startCal.setTime(startDate);
		return startCal;
	}

	public Calendar getSelectedEndDate() {

		Calendar endCal = Calendar.getInstance();
		endDate = (Date) this.endDatePicker.getModel().getValue();
		endCal.setTime(endDate);
		return endCal;
	}

	private void ShowMessage(final String message) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, message);
			}
		});
	}
}
