package EarthSim;

import java.util.Date;

/**
 * Provides utility functions for geometric calculations related to the earth
 * grid.
 * 
 * @author Andrew Bernard
 * @modifiedBy Liz Wong
 */
public abstract class Util {
	public static final float earthCircumferenceInMeters = 4.003014E7f;//40030140f;
	public static final float earthRadiusInMeters = 6.371E6f;
	public static final float earthSurfaceAreaInMetersSq = 2.55036E14f;
	public static final float solarConstant = 1.366E3f;
	public static final float stefanBoltzmannConstant = 5.67E-8f;
	public static final float earthEmissivity = 0.612f;
	public static final float surfaceAreaVisibleBySun = 2.55036E14f;
	
	/**
	 * Computes the circumference of the circle at the given latitude.
	 * 
   * @param latitude in degrees
   * @param earthRadius radius of the earth
   * 
   * @return the circumference of the circle at the given latitude in the units
   * given by <code>earthRadius</code> (i.e. if <code>earthRadius</code> is in 
   * pixels so is the calculated distance)
   */
  public static float getLatitudeCircum(double latitude, double earthRadius) {
    double latRadius = earthRadius * Math.sin(Math.toRadians(90d - latitude));
    return (float) (2d * latRadius * Math.PI);
  }

  /**
   * Computes the area of a trapezoid. All the lengths should be
   * of the same unit of measure.
   * 
   * @param topLength
   * @param bottomLength 
   * @param height
   * 
   * @return the area of the trapezoid
   */
  public static float getTrapezoidArea(double topLength, double bottomLength, double height) {
    return (float) ((.5 * height) * (topLength + bottomLength));
  }

  /**
   * Computes the length of the non-parallel sides of a trapezoid. All the 
   * lengths should be of the same unit of measure.
   * 
   * @param topLength
   * @param bottomLength 
   * @param height
   * 
   * @return the length of the non-parallel sides of the trapezoid
   */
  public static float getTrapezoidSideLen(double topLength, double bottomLength, double height) {
    return (float) Math.sqrt(Math.pow((Math.abs(topLength - bottomLength) / 2), 2) + Math.pow(height, 2));
  }

  
  /**
   * Computes the distance from a latitudal degree to the equator.
   * 
   * @param latitude in degrees
   * @param earthRadius radius of the earth
   * 
   * @return the distance to the equator in the units given by 
   * <code>earthRadius</code> (i.e. if <code>earthRadius</code> is in pixels so
   * is the calculated distance) 
   */
  public static float getDistToEquator(double latitude, double earthRadius) {
    return (float) (earthRadius * Math.sin(Math.toRadians(latitude)));
  }
  
  /**
   * Computes the longitude of the SW corner of the given grid location
   * 
   * @param column number in the grid
   * @param gridSpacing represents the size of the cell in degrees
   * 
   * @return the longitude of the cell's SW corner
   */
  public static float getLongitude(int column, int gridSpacing) {
	  return (float) ((360)-(column+1) * (gridSpacing));
  }
  
  /**
   * Computes the latitude of the SW corner of the given grid location
   * 
   * @param row number in the grid
   * @param gridSpacing represents the size of the cell in degrees
   * 
   * @return the latitude of the cell's SW corner
   */
  public static float getLatitude(int row, int gridSpacing) {
	  return (float) (row - ((180)/gridSpacing)/2) * gridSpacing;
  }
  
  /**
   * Computes the sun rotational position
   * @param currentPosition of the sun
   * @param timeStep 
   * @return float determining the position of the sun over the Earth
   */
  public static float getSunRotationalPosition(float currentPosition, float timeStep)  {
	
	float position = currentPosition;
		
	//convert time to hrs and calculate total degrees of step		
	// % 360 accounts for a full rotation of the earth
	position = (float) (position - 15 * (timeStep /60)) % 360;
	if (position < 0){
		position += 360;
	}
	return position;
  }
  
  /**
   * Each cell has the same E & W side lengths calculated based on gs
   * @param gridSpacing
   * @return The side length of any cell in meters
   */
  public static float getSideLengthOfCell(int gridSpacing) {
	  return (float) (earthCircumferenceInMeters*((float)gridSpacing/360f));
  }
  
  /**
   * Get the base length of a given cell in a row
   * Base does not mean South, just the fat side on the isosceles trapezoid
   * @param row
   * @param gridSpacing
   * @return The base length of any cell in meters
   */
  public static float getBaseLengthOfCell(int row, int gridSpacing, float latitude, float sideLength) {
	  return (float) Math.cos(Math.toRadians(latitude)) * sideLength;
  }
  
  /**
   * Get the top length of a given cell in a row
   * Top does not mean North, just the narrow side on the isosceles trapezoid
   * @param row
   * @param gridSpacing
   * @return The base length of any cell in meters
   */
  public static float getTopLengthOfCell(int row, int gridSpacing, float latitude, float sideLength) {
	  float posLat = Math.abs(latitude);
	  if (posLat > 90f) { // cannot wrap globe, print out for testing
		  System.out.println("Invalid row attempted to be calculated, latitude must be <= 90. Row sent is: "+row);
	  }
	  else if (posLat == 90f) {
		  return 0f;
	  }
	  return (float) Math.cos(Math.toRadians(posLat + (float)gridSpacing)) * sideLength;
  }

  /**
   * The height or altitude of the cell in meters.
   * @param sideLength
   * @param baseLength
   * @param topLength
   * @return The height in meters
   */
  public static float getHeightOfCell(float sideLength, float baseLength, float topLength) {
	  return (float) Math.sqrt(Math.pow(sideLength, 2) - (1f/4f) * Math.pow(baseLength - topLength, 2));
  }
  
  /**
   * Calculate the surface area in m^2 of a given cell in a row
   * @param row
   * @param gridSpacing
   * @return
   */
	public static float getAreaOfCell(float topLength, float baseLength, float height) {
		return (float) (1f/2) * (topLength + baseLength) * height;
	}
	
	/**
	 * Rotation angle in degrees as a function of time t in minutes since the simulation's start
	 * phi(t)
	 * @param timeInMinutes
	 * @return Rotation angle in degrees
	 */
	public static float rotationAngle(long timeInMinutes) {
		return (float) (timeInMinutes % 1440) * 360f/1440f; 
	}
	
	/**
	 * Determine the number of degrees of rotation d that theta corresponds to
	 * @param timeOfDayInMin
	 * @return
	 */
	public static float getDegreesOfRotation(float longitude) {
		float rotation = -1f * longitude;
		if (longitude > 0) {
			rotation = 360-longitude;
		}
		return rotation;
	}

	/**
	 * Calculate the new time once the step has been performed.
	 * @param currentTime
	 * @param timeStepInMinutes
	 * @return
	 */
	public static Date getTimeAfterStep(Date currentTime, int timeStepInMinutes) {
		long oneMinuteInMilliseconds = 60000; 
		return new Date(currentTime.getTime() + (timeStepInMinutes * oneMinuteInMilliseconds));
	}
	
	/**
	 * Calculate the heat attenuation value given longitude and latitude
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public static float getHeatAttentuation(long timeInMinutes, float longitude, float latitude) {
		float attenuation = 0f;
		float degAtNoon = Math.abs(getDegreesOfRotation(longitude) - rotationAngle(timeInMinutes));
		if (degAtNoon < 90) {
			attenuation = (float) (Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(degAtNoon)));
		}
		return attenuation;
	}
  
	/**
	 * GRID column under the Sun at time minutesSinceStart
	 * @param cols
	 * @param minutesSinceStart
	 * @return
	 */
	public static int getColumnSunIsOver(int cols, long minutesSinceStart) {
		return (int) ((cols*(minutesSinceStart/360) + cols/2)% cols);
	}
}
