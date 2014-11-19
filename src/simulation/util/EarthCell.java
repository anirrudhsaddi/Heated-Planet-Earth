package simulation.util;

public interface EarthCell<T> extends Cell<T> {
	
	public float calculateTemp(int time, int currentTimeInSimulation);
	
	public float calTsun(int sunPosition, int currentTimeInSimulation);

	public void setGridProps(int x, int y, int latitude, int longitude, int gs, float axisTilt, float eccentricity);

	public int getGridSpacing();

	public float getPlanetX(int currentTime);

	public float getPlanetY(int currentTime);
	
	public float getSurfarea();

	public double getSunLatitudeOnEarth(int currentTimeInSimulation);

}
