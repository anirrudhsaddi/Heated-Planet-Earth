package common;

public interface IGrid {
	
	public void setTemperature(int x, int y, double temp);
	
	public double getTemperature(int x, int y);
	
	public float getSunPositionDeg();
	
	public void setSunLatitudeDeg(float lat);
	
	public float getSunLatitudeDeg();
	
	public void setPlanetX(float x);
	
	public void setPlanetY(float y);
	
	public float getPlanetX();
	
	public float getPlanetY();	
	
	public long getCurrentTime();
	
	public int getGridWidth();
	
	public int getGridHeight();
	
	public long getDateTime();
	
	public String getSimulationName();

}
