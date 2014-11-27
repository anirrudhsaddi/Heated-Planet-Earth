package messaging.events;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import common.IGrid;

import simulation.util.GridCell;
import messaging.Message;

public class DisplayMessage implements Message {
	
	private Map<Calendar, List<GridCell>> table;
	GridCell minTemp, maxTemp; 
	List<Double> meanTempOverRegion, meanTempOverTime;
	private final IGrid grid;
		
	public DisplayMessage(IGrid grid) {
		
		if (grid == null)
			throw new IllegalArgumentException("Invalid IGrid provided");
		this.grid = grid;

		this.setTable(null);
		this.minTemp = null;
		this.maxTemp = null;
		this.meanTempOverRegion = null;
		this.meanTempOverTime = null;
	
		
	}
	
	public IGrid getGrid() {
		return this.grid;
	}
	
	public DisplayMessage(Map<Calendar, List<GridCell>> table, GridCell minTemp, GridCell maxTemp, 
			List<Double> meanTempOverRegion, List<Double> meanTempOverTime, IGrid grid) {
		this.setTable(table);
		this.minTemp = minTemp;
		this.maxTemp = maxTemp;
		this.meanTempOverRegion = meanTempOverRegion;
		this.meanTempOverTime = meanTempOverTime;
		
		if (grid == null)
			throw new IllegalArgumentException("Invalid IGrid provided");
		this.grid = grid;

	}

	public Map<Calendar, List<GridCell>> getTable() {
		return table;
	}

	public void setTable(Map<Calendar, List<GridCell>> table) {
		this.table = table;
	}
	
	public GridCell getMinTemp() {
		return this.minTemp;
	}
	
	public GridCell getMaxTemp() {
		return this.maxTemp;
	}
	
	public List<Double> getMeanTempOverRegion() {
		return this.meanTempOverRegion;
	}
	
	public List<Double> getMeanTempOverTime() {
		return this.meanTempOverTime;
	}
	
	public void setMinTemp(GridCell t) {
		this.minTemp = t;
	}
	
	public void setMaxTemp(GridCell t) {
		this.maxTemp = t;
	}
	
	public void setMeanTempOverRegion(List<Double> t) {
		this.meanTempOverRegion = t;
	}
	
	public void setMeanTempOverTime(List<Double> t) {
		this.meanTempOverTime = t;
	}	
}
