package common;

public interface IMonitorCallback {
	
	public void notifyCurrentInterval(int currSimulationInterval, long date, long time);

}
