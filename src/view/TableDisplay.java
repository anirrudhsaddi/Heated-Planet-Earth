package view;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import messaging.Message;
import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.DisplayMessage;
import messaging.events.ConfigureMessage;
import simulation.util.GridCell;
import common.Constants;
import common.IGrid;

public class TableDisplay extends JFrame {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private JTable				table;
	private String[]			columnNames;
	private Object[][]			data;
	private int					gs;

	private boolean				showMinTemp;
	private boolean				showMaxTemp;
	private boolean				showMeanTime;
	private boolean				showMeanRegion;
	private boolean				showActualValue;

	public TableDisplay() {

		super("Query Table");

		this.columnNames = new String[0];
		this.data = new Object[0][0];

		setupWindow();
		pack();
	}

	public void configure(ConfigureMessage msg) {

		this.gs = msg.gs();
		this.showMinTemp = msg.showMinTemp();
		this.showMaxTemp = msg.showMaxTemp();
		this.showMeanTime = msg.showMeanTime();
		this.showMeanRegion = msg.showMeanRegion();
		this.showActualValue = msg.showActualValue();
	}

	public void setupWindow() {

		setTitle("Query Table");

		setSize(800, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getContentPane().setLayout(new GridLayout());
		setLocationRelativeTo(null);

		lowerRightWindow(); // Set window location to lower right (so we don't
							// hide dialogs)
		setAlwaysOnTop(true);
		getContentPane().add(new JScrollPane(table));
	}

	private void lowerRightWindow() {

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) (dimension.getWidth() - this.getWidth());
		int y = (int) (dimension.getHeight() - this.getHeight());
		this.setLocation(x, y);

	}

	@SuppressWarnings("rawtypes")
	public void updateTableData(DisplayMessage msg) {

		getContentPane().removeAll();

		// Min Temp | Max Temp | Mean Region Temp | Mean Time Temp | Cell 1/1 |
		// Cell 1/2 | etc....
		// time 1
		// time 2
		// time 3
		// time 4
		// time 5
		// time 6

		// add column names
		int columnCount = 0;
		ArrayList<String> columnNameArray = new ArrayList<String>();
		this.columnNames = new String[4 + (msg.getGrid().getGridHeight() * msg.getGrid().getGridWidth())];

		columnNameArray.add("Time");
		columnCount++;

		if (showMinTemp) {
			columnNameArray.add("Min Temp");
			columnCount++;
		}

		if (showMaxTemp) {
			columnNameArray.add("Max Temp");
			columnCount++;
		}

		if (showMeanRegion) {
			columnNameArray.add("Mean Region Temp");
			columnCount++;
		}

		if (showMeanTime) {
			columnNameArray.add("Mean Time Temp");
			columnCount++;
		}

		if (showActualValue) {
			IGrid grid = msg.getGrid();
			for (int x = 0; x < grid.getGridWidth(); x++) {
				for (int y = 0; y < grid.getGridHeight(); y++) {
					columnNameArray.add("Grid " + x + "/" + y);
					columnCount++;
				}
			}
		}

		// add table data
		Map<Calendar, List<GridCell>> tableInfo = msg.getTable();
		java.util.Iterator<Entry<Calendar, List<GridCell>>> it = tableInfo.entrySet().iterator();

		data = new Object[tableInfo.size()][columnCount];

		int rowIdx = 0;
		int columnIdx = 0;

		GridCell gridCell;
		Calendar cal;
		List<Double> meanTemp;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			columnIdx = 0;

			// Time
			cal = (Calendar) pairs.getKey();
			data[rowIdx][columnIdx] = sdf.format(cal.getTime());
			columnIdx++;

			// Min Temp
			if (showMinTemp) { // ex: TEMP� at WHEN, WHERE
				gridCell = msg.getMinTemp();
				data[rowIdx][columnIdx] = "Min Temp: " + gridCell.getTemp() + " Time:" + gridCell.timeOfResult
						+ " Location:" + gridCell.getX() + "/" + gridCell.getY();
				columnIdx++;
			}

			// Max Temp
			if (showMaxTemp) { // ex: TEMP� at WHEN, WHERE
				gridCell = msg.getMaxTemp();
				data[rowIdx][columnIdx] = "Max Temp: " + gridCell.getTemp() + " Time:" + gridCell.timeOfResult
						+ " Location:" + gridCell.getX() + "/" + gridCell.getY();
				columnIdx++;
			}

			// Mean Temp Over Region
			if (showMeanRegion) { // ex: TEMP� at ROW
				meanTemp = msg.getMeanTempOverRegion();
				data[rowIdx][columnIdx] = "Mean Temp Over Region : " + meanTemp.get(rowIdx) + " Row:??";
				columnIdx++;
			}

			// Mean Temp Over Time
			if (showMeanTime) { // ex: TEMP� at ROW
				meanTemp = msg.getMeanTempOverTime();
				data[rowIdx][columnIdx] = "Mean Temp Over Time : " + meanTemp.get(rowIdx) + " Row:??";
				columnIdx++;
			}

			// Actual Cell Values
			if (showActualValue) { // ex: TEMP� at X,Y
				IGrid grid = msg.getGrid();
				for (int x = 0; x < grid.getGridWidth(); x++) {
					for (int y = 0; y < grid.getGridHeight(); y++) {
						data[rowIdx][columnIdx] = "Temp:" + grid.getTemperature(x, y) + " at lat:"
								+ getLatitude(y, grid.getGridHeight()) + " lon:" + getLongitude(x, grid.getGridWidth());
						columnIdx++;
					}
				}
			}

			it.remove(); // avoids a ConcurrentModificationException
			rowIdx++;
		}

		if (data.length > 0 && columnNames.length > 0) {
			this.setVisible(true);

			table = new JTable(data, columnNames);
			table.setPreferredScrollableViewportSize(new Dimension(500, 70));
			table.setFillsViewportHeight(true);
		} else {
			this.setVisible(false);
		}

	}

	// Minimum temperature in the region, when and where it occurred;
	// that is, the smallest temperature in the entire table and the time and
	// location where it occurred
	private GridCell getMin() {

//		GridCell result = new GridCell(Constants.MAX_TEMP, 0, 0, 0, 0, 0, 0, 0);
//		for (Map.Entry<Calendar, List<GridCell>> entry : this.table.entrySet()) {
//			Calendar time = entry.getKey();
//			List<GridCell> gridCells = entry.getValue();
//			for (GridCell mycell : gridCells) {
//				if (mycell.getTemp() < result.getTemp()) {
//					result = mycell;
//					result.timeOfResult = time;
//				}
//			}
//		}
//		return result;
		
		return null;
	}

	// Maximum temperature in the region, when and where it occurred;
	// that is, the largest temperature in the table and the time and location
	// where it occurred
	private GridCell getMax() {

//		GridCell result = new GridCell(Constants.MIN_TEMP, 0, 0, 0, 0, 0, 0, 0);
//		for (Map.Entry<Calendar, List<GridCell>> entry : this.table.entrySet()) {
//
//			Calendar time = entry.getKey();
//			List<GridCell> gridCells = entry.getValue();
//			for (GridCell mycell : gridCells) {
//				if (mycell.getTemp() > result.getTemp()) {
//					result = mycell;
//					result.timeOfResult = time;
//				}
//			}
//		}
//
//		return result;
		
		return null;
	}

	// Mean temperature over the region for the requested times;
	// that is, for each row in the table, what was its mean temperature across
	// all of the columns. (The denominator is the number of columns.)
	private List<Double> getMeanTempOverRegion() {

//		List<Double> meanTemps = new LinkedList<Double>();
//		for (Map.Entry<Calendar, List<GridCell>> entry : this.table.entrySet()) {
//
//			// Integer time = entry.getKey();
//			List<GridCell> gridCells = entry.getValue();
//			double temperatures = 0;
//			for (GridCell mycell : gridCells) {
//				temperatures += mycell.getTemp();
//			}
//			temperatures /= gridCells.size();
//			meanTemps.add(temperatures);
//		}
//
//		return meanTemps;
		
		return null;
	}

	// Mean temperature over the times for the requested region;
	// that is, for each column in the table, what was its mean temperature down
	// all rows. (The denominator is the number of rows.
	private List<Double> getMeanTempOverTime() {

//		List<Double> meanTemps = new LinkedList<Double>();
//		for (Map.Entry<Calendar, List<GridCell>> entry : this.table.entrySet()) {
//
//			// Integer time = entry.getKey();
//			List<GridCell> gridCells = entry.getValue();
//			int i = 0;
//			for (GridCell mycell : gridCells) {
//				meanTemps.set(i++, meanTemps.get(i) + mycell.getTemp());
//			}
//		}
//
//		int i = 0;
//		for (Map.Entry<Calendar, List<GridCell>> entry : this.table.entrySet()) {
//			meanTemps.set(i++, meanTemps.get(i) / this.table.size());
//		}
//
//		return meanTemps;
		
		return null;
	}

	private int getLatitude(int y, int height) {
		return (y - (height / 2)) * this.gs;
	}

	private int getLongitude(int x, int width) {
		return x < (width / 2) ? -(x + 1) * this.gs : (360) - (x + 1) * this.gs;
	}
}
