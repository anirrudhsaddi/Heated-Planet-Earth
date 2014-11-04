package EarthSim;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * A {@link JPanel} composed of the the earth and sun display components.
 * 
 * @author Andrew Bernard
 */
public class EarthPanel extends JPanel {

	private static final long serialVersionUID = -1108120537851962997L;
	private SunDisplay sunDisplay;
	private EarthGridDisplay earth;
	private static final int DEFAULT_GRID_SPACING = 15; // degrees
	private JTextArea tempIterationsLabel;

	/**
	 * Constructor - sets up the panel with the earth and sun display components
	 * using a {@link BoxLayout} with {@link BoxLayout#PAGE_AXIS}.
	 * 
	 * @param minSize
	 *            used in calling {@link #setMinimumSize(Dimension)}
	 * @param maxSize
	 *            used in calling {@link #setMaximumSize(Dimension)}
	 * @param prefSize
	 *            used in calling {@link #setPreferredSize(Dimension)}
	 */
	public EarthPanel(Dimension minSize, Dimension maxSize, Dimension prefSize) {
    super();
    setLayout(new GridBagLayout());
    
    setMinimumSize(minSize);
    setMaximumSize(maxSize);
    setPreferredSize(prefSize);
    
    earth = new EarthGridDisplay(DEFAULT_GRID_SPACING);
        
    sunDisplay = new SunDisplay(earth.getWidth());
    tempIterationsLabel = new JTextArea();
    
    tempIterationsLabel.setText("Iterations Ran: " + "\n" +
			" - Current Memory Usage: " + "\n" +
			" - Buffer Depth: " + "\n" +
			" - Step Simulation Time(ms): "  +"\n" + 
			" - Simulation Idle Time(ms): "  + "\n" +
			" - Sun Position(degrees) : " + "\n" +
			" - Current Simulation Date & Time : "); 
    GridBagConstraints sunConstraints = new GridBagConstraints();
    sunConstraints.fill = GridBagConstraints.HORIZONTAL;
    sunConstraints.gridx = 0;
    sunConstraints.gridy = 0;
//    sunConstraints.anchor = GridBagConstraints.LINE_START;
    add(sunDisplay);
    GridBagConstraints earthConstraints = new GridBagConstraints();
    earthConstraints.fill = GridBagConstraints.BOTH;
    earthConstraints.gridx = 0;
    earthConstraints.gridy = 1;
    earthConstraints.weighty = 1;
//    earthConstraints.anchor = GridBagConstraints.LINE_START;
    add(earth, earthConstraints);
    GridBagConstraints labelConstraints = new GridBagConstraints();
    labelConstraints.fill = GridBagConstraints.HORIZONTAL;
    labelConstraints.gridx = 1;
    labelConstraints.gridy = 1;
    add(tempIterationsLabel, labelConstraints);
    setIgnoreRepaint(true);
  }

	/**
	 * Draws the grid.
	 * 
	 * @param degreeSeparation
	 *            the latitude and longitude degree separations between the
	 *            cells in the grid
	 */
	public void drawGrid(int degreeSeparation) {
		earth.setGranularity(degreeSeparation);
		sunDisplay.drawSunPath(earth.getWidth());
		paint(this.getGraphics());
	}

	/**
	 * Gets the radius of the earth.
	 * 
	 * @return the radius of the earth in pixels
	 */
	public int getRadius() {
		return earth.getRadius();
	}

	/**
	 * Updates the display with the values from the temperature grid.
	 * 
	 * @param grid
	 *            the grid to get the new temperature values from
	 */
	public void updateGrid(SimulationStepResult result) {
		// TODO: Update instrumentation and other simulation result stuff on the
		// UI
		tempIterationsLabel.setText("Iterations Ran: "
				+ Integer.toString(result.getIteration()) + "\n"
				+ " - Current Memory Usage: "
				+ Long.toString(result.getMemoryUsage()) + "\n"
				+ " - Buffer Depth: " + result.getBufferDepth() + "\n"
				+ " - Step Simulation Time(ms): "
				+ Long.toString(result.getSimulationStepCalculationTimeInMs())
				+ "\n" + " - Simulation Idle Time(ms): "
				+ Long.toString(result.getSimulationIdleTimeInMs()) + "\n"
				+ " - Sun Position(degrees) : "
				+ Float.toString(result.getSunRotationalPosition()) + "\n"
				+ " - Current Simulation Date & Time : \n"
				+ result.getCalculationDate().toString());
		earth.updateGrid(result);
		moveSunPosition(result.getSunRotationalPosition());
	}

	/**
	 * Moves the sun's position the specified number of degrees.
	 * 
	 * @param degrees
	 *            the number of degrees to move the sun
	 */
	public void moveSunPosition(float degrees) {
		sunDisplay.setSunPosition(degrees);
		paint(this.getGraphics());
	}

	/**
	 * Resets the earth display and sun position.
	 */
	public void reset() {
		sunDisplay.reset();
		earth.reset();
		paint(this.getGraphics());
	}

	/**
	 * Sets the opacity of the map image on a scale of 0 to 1, with 0 being
	 * completely transparent.
	 * 
	 * @param value
	 *            the opacity value
	 */
	public void setMapOpacity(float value) {
		earth.setColorOpacity(value);
		paint(this.getGraphics());
	}

}
