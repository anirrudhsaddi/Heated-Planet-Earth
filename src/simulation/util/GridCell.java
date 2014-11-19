package simulation.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import common.Constants;

public final class GridCell implements EarthCell<GridCell> {

	// gs: grid spacing
	private int x;
	private int y;
	private int gs;
	private int latitude;
	private int longitude;
	
	// average temperature
	private static float avgsuntemp;
	private static float avgArea;

	private boolean visited;
	
	// Cell properties: surface area, perimeter
	private float lv;
	private float lb;
	private float lt;
	private float surfarea;
	private float pm;
	
	// P1
	private float currTemp;
	private float newTemp;
	private float tSun;
	private float axisTilt;
	private float eccentricity;

	private GridCell top = null, bottom = null, left = null, right = null;
	
	public static double foci 	= 0;
	public static double b		= 0;
	
	// Time of the Equinox
	private static int tauAN;

	public GridCell(float temp, int x, int y, int latitude, int longitude, int gs, float axisTilt, float eccentricity) {

		if (temp > Float.MAX_VALUE) throw new IllegalArgumentException("Invalid temp provided");
		if (x > Integer.MAX_VALUE || x < Integer.MIN_VALUE) throw new IllegalArgumentException("Invalid 'x' provided");
		if (y > Integer.MAX_VALUE || y < Integer.MIN_VALUE) throw new IllegalArgumentException("Invalid 'y' provided");

		this.setGridProps(x, y, latitude, longitude, gs, axisTilt, eccentricity);
		
		this.setTemp(temp);
	}

	public GridCell(GridCell top, GridCell bottom, GridCell left, GridCell right, float temp, int x, int y, int latitude, int longitude, int gs, float axisTilt, float eccentricity) {
		
		this(temp, x, y, latitude, longitude, gs, axisTilt, eccentricity);

		this.setTop(top);
		this.setBottom(bottom);
		this.setLeft(left);
		this.setRight(right);
	}

	@Override
	public void setTop(GridCell top) {

		if (top == null) return;
		this.top = top;
	}

	@Override
	public GridCell getTop() {
		return this.top;
	}

	@Override
	public void setBottom(GridCell bottom) {

		if (bottom == null) return;
		this.bottom = bottom;
	}

	@Override
	public GridCell getBottom() {
		return this.bottom;
	}

	@Override
	public void setRight(GridCell right) {

		if (right == null) return;
		this.right = right;
	}

	@Override
	public GridCell getRight() {
		return this.right;
	}

	@Override
	public void setLeft(GridCell left) {

		if (left == null) return;
		this.left = left;
	}

	@Override
	public GridCell getLeft() {
		return this.left;
	}

	@Override
	public float getTemp() {
		return this.currTemp;
	}

	@Override
	public void setTemp(float temp) {

		if (temp > Float.MAX_VALUE) throw new IllegalArgumentException("Invalid temp provided");
		this.currTemp = temp;
	}

	@Override
	public void setGridProps(int x, int y, int latitude, int longitude, int gs, float axisTilt, float eccentricity) {

		this.x = x;
		this.y = y;
		this.latitude = latitude;
		this.longitude = longitude;
		this.gs = gs;
		this.axisTilt = axisTilt;
		this.eccentricity = eccentricity;

		// calc lengths, area, etc.
		this.calSurfaceArea(latitude, gs);
		
		this.visited = false;
		
		// P2 Heated Planet: Set time of equinox
		this.setTimeOfEquinox();
		
		// P3
		tauAN = 0;
		b = Constants.a * (Math.sqrt(1 - (this.eccentricity * this.eccentricity)));
		foci = Math.sqrt((Constants.a * Constants.a) - (b * b));
	}
	
	// TODO add to interface
	public float getTSun() {
		return this.tSun;
	}

	@Override
	public void visited(boolean visited) {
		this.visited = visited;
	}

	@Override
	public Iterator<GridCell> getChildren(boolean visited) {
		
		List<GridCell> ret = new ArrayList<GridCell>();

		if (this.top != null 	&& this.top.visited == visited) 	ret.add(this.top);
		if (this.bottom != null && this.bottom.visited == visited) 	ret.add(this.bottom);
		if (this.left != null 	&& this.left.visited == visited) 	ret.add(this.left);
		if (this.right != null 	&& this.right.visited == visited) 	ret.add(this.right);

		return ret.iterator();
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public int getGridSpacing() {
		return this.gs;
	}
	
	@Override
	public float calculateTemp(int sunPosition, int currentTimeInSimulation) {
		
		this.tSun = calTsun(sunPosition, currentTimeInSimulation);
		float temp = this.currTemp + (calTneighbors() - this.currTemp) / 5 + (this.tSun + calTcool()) / 10;
		this.newTemp = (temp > 0) ? temp : 0;    // avoid negative temperature
		return this.newTemp; // new temp
	}

	@Override
	public void swapTemp() {
		this.currTemp = this.newTemp;
		this.newTemp = 0;
	}
	
	@Override
	public double getSunLatitudeOnEarth(int currentTimeInSimulation) {
		
		//return (Earth.tilt * Math.sin((getRotationalAngle(currentTime))));
		return (this.axisTilt * Math.sin((getRotationalAngle(currentTimeInSimulation))));
	}
	
	// TODD Add to interface
	@Override
	public float calTsun(int sunPosition, int currentTimeInSimulation) {
		
		int sunLongitude = getSunLocationOnEarth(sunPosition);
		//float attenuation_lat   = (float) Math.cos(Math.toRadians(this.latitude  + 1.0 * this.gs / 2));
		
		//P2 - Heated Planet : Find correct attenuation depending on the sun latitude
		int sunLatitude = (int) getSunLatitudeOnEarth(currentTimeInSimulation);
		float attenuation_lat = (float) Math.cos(Math.toRadians(Math.abs(sunLatitude - this.latitude)));
		float attenuation_longi = (float) Math.cos(Math.toRadians(sunLongitude - this.longitude));
		attenuation_longi = attenuation_longi > 0 ? attenuation_longi : 0;
		
		//P3 - Heated Planet : Sun's distance from planet, inverse square law
		double inverseDistanceRatio = 0.5 * Math.pow(distanceFromPlanet(currentTimeInSimulation),2)/Math.pow(distanceFromPlanet(0),2);
		return (float) (278 * attenuation_lat * attenuation_longi/inverseDistanceRatio); 
	
	}
	
	@Override
	public float getSurfarea() {
		return this.surfarea;
	}
	
	@Override
	public float getPlanetX(int currentTime) {
		return (float) ((Constants.a * this.eccentricity)  + (Constants.a * Math.cos((getEccentricAnamoly(currentTime)))));
	}

	@Override
	public float getPlanetY(int currentTime) {
		double b = Constants.a * (Math.sqrt(1 - (this.eccentricity * this.eccentricity)));
		return (float) (b * Math.sin((getEccentricAnamoly(currentTime))));
	}
	
	// Static methods
	
	public static void setAvgSuntemp(float avg){
		avgsuntemp = avg;
	}
	
	public static float getAvgSuntemp(){
		return avgsuntemp;
	}
	
	public static void setAverageArea(float avgarea) {
		avgArea = avgarea;
	}
	
	public static float getAverageArea() {
		return avgArea;
	}
	
	// Private methods
	
	private void calSurfaceArea(int latitude, int gs) {
		
		double p  = 1.0 * gs / 360;
		this.lv   = (float) (Constants.CIRCUMFERENCE * p);
		this.lb   = (float) (Math.cos(Math.toRadians(latitude)) * this.lv);
		this.lb   = this.lb > 0 ? this.lb: 0;
		this.lt   = (float) (Math.cos(Math.toRadians(latitude + gs)) * this.lv);
		this.lt   = this.lt > 0 ? this.lt: 0;
		double h  = Math.sqrt(Math.pow(this.lv, 2) - 1/4 * Math.pow((this.lb - this.lt), 2));

		this.pm = (float) (this.lt + this.lb + 2 * this.lv);
		this.surfarea =  (float) (1.0 / 2 * (this.lt + this.lb) * h);
	}

	// A help function for get the Sun's corresponding location on longitude.
	private int getSunLocationOnEarth(int sunPosition) {
		
		// Grid column under the Sun at sunPosition
		int cols = 360 / this.gs;
		int j    = sunPosition;
		return j < (cols / 2) ? -(j + 1) * this.gs : (360) - (j + 1) * this.gs;
	}

	private float calTcool() {
		
		float beta = (float) (this.surfarea / avgArea);  // actual grid area / average cell area
		return -1 * beta * this.currTemp / 288 * avgsuntemp;
	}
	
	private float calTneighbors() {

		float top_temp = 0, bottom_temp = 0;

		if (this.top != null)
			top_temp = this.lt / this.pm * this.top.getTemp();
		
		if (this.bottom != null)
			bottom_temp = this.lb / this.pm * this.bottom.getTemp();

		return  top_temp + bottom_temp + this.lv / this.pm * (this.left.getTemp() + this.right.getTemp());
	}
	
	//=================================================
	//P3 Heated Planet
	
	private double getMeanAnamoly(int currentTime) {
		return (2 * Math.PI * currentTime / Constants.T);
	}
	
	private double getEccentricAnamoly(int currentTime) {
		return equationSolverNewton(getMeanAnamoly(currentTime));
	}
	
	private double equationSolverNewton(double meanAnamoly) {
		
	    double del = 1e-5,xx = 0 ;
	    double dx = 0, x = Math.PI/2;
	    int k = 0;
	    
	    if (this.eccentricity > 0.8)
	    	x=Math.PI;
	    else
	    	x=meanAnamoly;
	    
	    while (Math.abs(xx - x) > del && k<10 && functionOfX(meanAnamoly, x) != 0) {
	      dx = functionOfX(meanAnamoly, x) / derivativeOfX(x);
	      xx = x;
	      x = x - dx;
	      k++;
	    }	    
	    return x;
	}
	
	// Method to provide function f(x)
	private double functionOfX(double meanAnamoly, double x) {
	    return (meanAnamoly - x + (this.eccentricity * Math.sin((x))));
	}

	// Method to provide the derivative f'(x).
	private double derivativeOfX(double x) {
	    return (-1 + this.eccentricity * Math.cos((x)));
	}	
	
	private double trueAnamoly(int currentTime) {
		
		double eccentricAnamoly = getEccentricAnamoly(currentTime);
		double numerator = Math.cos((eccentricAnamoly)) - this.eccentricity;
		double denominator = 1 - (this.eccentricity * Math.cos((eccentricAnamoly)));
		return (Math.acos((numerator/denominator)));
	}
	
	private double distanceFromPlanet(int currentTime) {
		
		double numerator = 1 - (this.eccentricity * this.eccentricity);
		double denominator = 1 + (this.eccentricity * Math.cos((trueAnamoly(currentTime))));
		return (Constants.a * numerator / denominator);
	}
	
	private void setTimeOfEquinox() {
		
		int t = 0;
		
		for ( ; tauAN == 0 && t < Constants.T; t++) {
			
			double trueAnamoly = trueAnamoly(t);
			
			//System.out.println("\n" + "trueAnamoly " + trueAnamoly);
			// Try 10 as a limit to try first
			if (Math.abs(Math.toRadians(Constants.omega)- trueAnamoly) <= 0.1) {
				tauAN = t;
				break;
			}
		}
	}
	
	private double getRotationalAngle(int currentTime) {
		
		double mod = (currentTime - tauAN) % Constants.T;
		return (mod * 2 * Math.PI / Constants.T);
	}
}