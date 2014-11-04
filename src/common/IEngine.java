package common;


public interface IEngine extends Runnable {
	
	public void configure(int gs, int timeStep);
	
	public void pause();
	
	public void resume();
	
	public void start();
	
	public void stop();

}