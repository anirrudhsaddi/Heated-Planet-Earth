package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Desktop.Action;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
    

    public TableDisplay() {
    	super("Query Table");
    	Publisher.getInstance().subscribe(DisplayMessage.class, this);
    	
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
		
    	//data = msg.data;
    	//columneNames = msg.columnNames;
    	
		if(data.length > 0 && columnNames.length > 0){
			this.setVisible(true);
    	
			table = new JTable(data, columnNames);
			table.setPreferredScrollableViewportSize(new Dimension(500, 70));
			table.setFillsViewportHeight(true);
		}
		else{
			this.setVisible(false);
		}
    }
	
	@Override
	public void onMessage(Message msg) {
		if (msg instanceof DisplayMessage) {
			updateTableData(((DisplayMessage) msg));
		}
	}
}
