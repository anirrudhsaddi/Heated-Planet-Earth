package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Desktop.Action;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.html.HTMLDocument.Iterator;

import PlanetSim.widgets.ControlWidget;
import PlanetSim.widgets.SettingsWidget;
import messaging.Message;
import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.DeliverMessage;
import messaging.events.DisplayMessage;
import messaging.events.StartMessage;
import simulation.util.GridCell;
import view.util.ThermalVisualizer;
import view.widgets.EarthImage;
import view.widgets.GridDisplay;
import view.widgets.SimulationStatus;
import common.Constants;
import common.IGrid;

public class TableDisplay extends JFrame implements MessageListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table;
    private String[] columnNames;
    private Object[][] data;
    private int gs;

    public TableDisplay() {
    	super("Query Table");
    	Publisher.getInstance().subscribe(DisplayMessage.class, this);
    	Publisher.getInstance().subscribe(StartMessage.class, this);
    	
    	this.columnNames =  new String[0];
    	this.data = new Object[0][0];
    	
    	setupWindow();
    	pack();
    }
    
    public TableDisplay(String[] columnNames,Object[][] tabledata) {
    	super("Query Table");
    	Publisher.getInstance().subscribe(DisplayMessage.class, this);
    	
    	this.columnNames =  columnNames;
    	this.data = tabledata;
    	
    	setupWindow();
    	pack();
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
		table = new JTable(data, columnNames);
		getContentPane().add(new JScrollPane(table));
	}
	
	private void lowerRightWindow() {

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) (dimension.getWidth() - this.getWidth());
		int y = (int) (dimension.getHeight() - this.getHeight());
		this.setLocation(x, y);

	}
	
	private void updateTableData(DisplayMessage msg) {
		getContentPane().removeAll();
		/*
		//			Min Temp	|	Max Temp	|	Mean Region Temp	|	Mean Time Temp	|	Cell 1/1	|	Cell 1/2	|	etc....
		//	time 1
		//	time 2
		//	time 3
		//	time 4
		//	time 5
		//	time 6
		
		
		// add column names
		int columnCount = 0;
		ArrayList<String> columnNameArray = new ArrayList<String>();
		this.columnNames = new String[4+(msg.getGrid().getGridHeight()*msg.getGrid().getGridWidth())];
		
		columnNameArray.add("Time");
		columnCount++;
		
		if(1){
			columnNameArray.add("Min Temp");
			columnCount++;
		}
		if(2){
			columnNameArray.add("Max Temp");
			columnCount++;
		}
		if(3){
			columnNameArray.add("Mean Region Temp");
			columnCount++;
		}
		if(4){
		columnNameArray.add("Mean Time Temp");
		columnCount++;
		}
		if(5){
			IGrid grid = msg.getGrid();
			for(int x=0;x<grid.getGridWidth();x++){
	    		for(int y=0;y<grid.getGridHeight();y++){
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
	        Map.Entry pairs = (Map.Entry)it.next();
	        columnIdx = 0;
	        
	        // Time
	        cal = (Calendar)pairs.getKey();
	        data[rowIdx][columnIdx] = sdf.format(cal.getTime());
	        columnIdx++;
	        
	        // Min Temp
	        if(1){	// 								ex: TEMP° at WHEN, WHERE 
	        	gridCell = msg.getMinTemp();
	        	data[rowIdx][columnIdx] = 	"Min Temp: " + gridCell.getTemp() + 
	        								" Time:" + gridCell.timeOfResult + 
    										" Location:" + gridCell.getX() + "/" + gridCell.getY();
	        	columnIdx++;
	        }
	        
	        // Max Temp
	        if(2){	// 								ex: TEMP° at WHEN, WHERE
	        	gridCell = msg.getMaxTemp();
	        	data[rowIdx][columnIdx] = 	"Max Temp: " + gridCell.getTemp() + 
	        								" Time:" + gridCell.timeOfResult + 
    										" Location:" + gridCell.getX() + "/" + gridCell.getY();
	        	columnIdx++;
	        }
	        
	        // Mean Temp Over Region
	        if(3){	// 								ex: TEMP° at ROW
	        	meanTemp = msg.getMeanTempOverRegion();
	        	data[rowIdx][columnIdx] = 	"Mean Temp Over Region : " + meanTemp.get(rowIdx) +
											" Row:??";
	        	columnIdx++;
	        }
	        
	        // Mean Temp Over Time
	        if(4){	// 								ex: TEMP° at ROW
	        	meanTemp = msg.getMeanTempOverTime();
	        	data[rowIdx][columnIdx] = 	"Mean Temp Over Time : " + meanTemp.get(rowIdx) +
											" Row:??";
	        	columnIdx++;
	        }
	        
	        // Actual Cell Values
	        if(5){	// 								ex: TEMP° at X,Y
	        	IGrid grid = msg.getGrid();
	        	for(int x=0;x<grid.getGridWidth();x++){
	        		for(int y=0;y<grid.getGridHeight();y++){
	        			data[rowIdx][columnIdx] = 	"Temp:" + grid.getTemperature(x, y) + 
	        										" at lat:" + getLatitude(y,grid.getGridHeight()) + 
	        										" lon:" + getLongitude(x,grid.getGridWidth());
        				columnIdx++;
	        		}
	        	}
	        }
	        
	        
	        it.remove(); // avoids a ConcurrentModificationException
	        rowIdx++;
	    }

    	
		if(data.length > 0 && columnNames.length > 0){
			this.setVisible(true);
    	
			table = new JTable(data, columnNames);
			table.setPreferredScrollableViewportSize(new Dimension(500, 70));
			table.setFillsViewportHeight(true);
		}
		else{
			this.setVisible(false);
		}
		*/
    }
	
	private int getLatitude(int y,int height) {
		return (y - (height / 2)) * this.gs;
	}

	private int getLongitude(int x,int width) {
		return x < (width / 2) ? -(x + 1) * this.gs : (360) - (x + 1) * this.gs;
	}
	
	@Override
	public void onMessage(Message msg) {
		if (msg instanceof DisplayMessage) {
			updateTableData(((DisplayMessage) msg));
		}
		else if(msg instanceof StartMessage){
			StartMessage startMsg = (StartMessage)msg;
			this.gs = startMsg.gs();
		}
	}
	
}
