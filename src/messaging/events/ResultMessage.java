package messaging.events;


import junit.framework.TestCase;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.Constants;

import simulation.util.GridCell;
import messaging.Message;

public class ResultMessage implements Message {
	
	private int width = 10;
	private boolean needsCalculation;
	
	private final List<Integer[]> coords;
	private final Map<Integer, Double> grid;
	
	//The user query can be thought of as producing a table in which there are rows 
	//corresponding to the times at which computations are made and columns corresponding to the grid cells.
	//table DS has integer as time(rows), List<GridCell> is list of grid cells(columns) 
	private final Map<Integer, List<GridCell>> table;	
	
	private final int southLatitude;
	private final int northLatitude;
	private final int westLongitude;
	private final int eastLongitude;
	
	public ResultMessage(int southLatitude, int northLatitude, int westLongitude, int eastLongitude, boolean needsCalculation) {
		
		this.southLatitude = southLatitude;
		this.northLatitude = northLatitude;
		this.westLongitude = westLongitude;
		this.eastLongitude = eastLongitude;
		this.needsCalculation = needsCalculation;
		this.table = new TreeMap<Integer, List<GridCell>>();
		
		grid = new TreeMap<Integer, Double>();
		coords = new LinkedList<Integer[]>();
	}
	
	public int getSouthRegionBounds() {
		return this.southLatitude;
	}
	
	public int getNorthRegionBounds() {
		return this.northLatitude;
	}
	
	public int getWestRegionBounds() {
		return this.westLongitude;
	}
	
	public int getEastRegionBounds() {
		return this.eastLongitude;
	}
	
	public boolean needsCalculation() {
		return this.needsCalculation;
	}
	
	public Iterator<Integer[]> genCoordinates() {
		return coords.iterator();
	}
	
	public void setTemperature(int longitude, int latitude, double temp) {
		
		grid.put(latitude * width + longitude, temp);
		coords.add(new Integer[] {longitude, latitude});
	}
	
	public double getTemperature(int longitude, int latitude) {
		
		return grid.get(latitude * width + longitude);
	}	
	
	public void PopulateTable() {
		int time = 0;	//TODO: result message should give output in a range of time
		List<GridCell> gridCells = new LinkedList<GridCell>();
		for (int i = getNorthRegionBounds() ; i <= getSouthRegionBounds(); i++) {
			for (int j = getEastRegionBounds() ; j <= getWestRegionBounds(); j++) {
				//check if this exists
				Integer[] checkInts = new Integer[] {i, j};
				if(coords.contains(checkInts)) {
				  GridCell thisCell = new GridCell(getTemperature(i, j), 0, 0, i, j, 0, 0, 0);
				  gridCells.add(thisCell);
				}
			}
		}
		this.table.put(time, gridCells);
	}
	
	public void InterpolateTable() {
		int time = 0;	//TODO: result message should give output in a range of time
		List<GridCell> gridCells = new LinkedList<GridCell>();
		GridCell thisCell = null;
		for (int i = getNorthRegionBounds() ; i <= getSouthRegionBounds(); i++) {
			for (int j = getEastRegionBounds() ; j <= getWestRegionBounds(); j++) {
				Integer[] checkInts = new Integer[] {i, j};
				if(coords.contains(checkInts)) {
  				  thisCell = new GridCell(getTemperature(i, j), i, j, i, j, 0, 0, 0);
				  gridCells.add(thisCell);
				} else if(thisCell != null) {
					double tempNearestCell = thisCell.getTemp();
					int k = thisCell.getX(), l = thisCell.getY();
					int dy = l-j, dx=k-i;
					double mytemp = tempNearestCell * (dy/dx);							
				    //SplineInterpolator splineInterp = new SplineInterpolator();
					thisCell = new GridCell(mytemp, i, j, i, j, 0, 0, 0);
					gridCells.add(thisCell);
				}
			}
		}
		this.table.put(time, gridCells);
	}
	
	//Minimum temperature in the region, when and where it occurred; 
	  //that is, the smallest temperature in the entire table and the time and location where it occurred
	public GridCell getMin() {
    	GridCell result = new GridCell(Constants.MAX_TEMP, 0, 0, 0, 0, 0, 0, 0);
    	for(Map.Entry<Integer, List<GridCell>> entry : this.table.entrySet()) {
    		Integer time = entry.getKey();
    		List<GridCell> gridCells = entry.getValue();
    		for (GridCell mycell : gridCells) {
    			if(mycell.getTemp() < result.getTemp()) {
    				result = mycell;
    				result.timeOfResult = time;
    			}
    		}
    	}
    	return result;
    }
    
	//Maximum temperature in the region, when and where it occurred; 
	  //that is, the largest temperature in the table and the time and location where it occurred
    public GridCell getMax() {
    	GridCell result = new GridCell(Constants.MIN_TEMP, 0, 0, 0, 0, 0, 0, 0);
    	for(Map.Entry<Integer, List<GridCell>> entry : this.table.entrySet()) {
    		Integer time = entry.getKey();
    		List<GridCell> gridCells = entry.getValue();
    		for (GridCell mycell : gridCells) {
    			if(mycell.getTemp() > result.getTemp()) {
    				result = mycell;
    				result.timeOfResult = time;
    			}
    		}
    	}
    	return result;
    }
    
  //Mean temperature over the region for the requested times; 
	  //that is, for each row in the table, what was its mean temperature across all of the columns. (The denominator is the number of columns.)
    public List<Double> getMeanTempOverRegion() {
    	List<Double> meanTemps = new LinkedList<Double>();
    	for(Map.Entry<Integer, List<GridCell>> entry : this.table.entrySet()) {
    		//Integer time = entry.getKey();
    		List<GridCell> gridCells = entry.getValue();
    		double temperatures=0;
    		for (GridCell mycell : gridCells) {
    			temperatures+= mycell.getTemp();
    		}
    		temperatures/=gridCells.size();
    		meanTemps.add(temperatures);
    	}
    	return meanTemps;
    }
	
    //Mean temperature over the times for the requested region; 
	  //that is, for each column in the table, what was its mean temperature down all rows. (The denominator is the number of rows.    
    public List<Double> getMeanTempOverTime() {
    	List<Double> meanTemps = new LinkedList<Double>();
    	for(Map.Entry<Integer, List<GridCell>> entry : this.table.entrySet()) {
    		//Integer time = entry.getKey();
    		List<GridCell> gridCells = entry.getValue();
    		int i =0;
    		for (GridCell mycell : gridCells) {
    			meanTemps.set(i,meanTemps.get(i)+ mycell.getTemp());
    			i++;
    		}
    	}
    	int i =0;
    	for(Map.Entry<Integer, List<GridCell>> entry : this.table.entrySet()) {
    	  meanTemps.set(i,meanTemps.get(i)/this.table.size());
    	  i++;
       }
    	return meanTemps;
    }
}
