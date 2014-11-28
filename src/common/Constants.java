package common;

import java.util.Calendar;
import java.util.TimeZone;

public interface Constants {

	// shot in the dark - this number seemd to produce best visual results
	public static final int			MAX_TEMP				= 550;

	public static final int			MIN_TEMP				= 0;

	public static final String		COLORMAP				= "thermal";
	public static final float		OPACITY					= 0.6f;
	public static final int			EARTH					= 0;
	public static final int			GRID					= 1;

	public static final int			INITIAL_TEMP			= 288;
	public static final int			MAX_DEGREES				= 180;
	public static final int			MAX_SPEED				= 525960;

	public static final int			MINUTES_IN_A_MONTH		= 43830;

	public static final int			DEFAULT_BUFFFER_SIZE	= 10;

	public static final int			PRECISION_MIN			= 0;
	public static final int			PRECISION_DEFAULT		= 7;
	public static final int			PRECISION_MAX			= 16;

	public static final int			GEOACCURACY_MIN			= 1;
	public static final int			GEOACCURACY_MAX			= 100;

	public static final int			TEMPORALACCURACY_MIN	= 1;
	public static final int			TEMPORALACCURACY_MAX	= 100;

	public static final int			MIN_GRID_SPACING		= 1;
	public static final int			DEFAULT_GRID_SPACING	= 15;
	public static final int			MAX_GRID_SPACING		= 180;

	public static final int			MIN_TIME_STEP			= 1;
	public static final int			DEFAULT_TIME_STEP		= 1440;
	public static final int			MAX_TIME_STEP			= 525600;

	public static final int			MIN_SIM_LEN				= 1;
	public static final int			DEFAULT_SIM_LEN			= 12;
	public static final int			MAX_SIM_LEN				= 1200;

	public static final float		MIN_PRESENTATION		= 1f;
	public static final float		DEFAULT_PRESENTATION	= 1f;
	public static final float		MAX_PRESENTATION		= Float.MAX_VALUE;

	public final static float		MIN_AXIS_TILT			= -180.0f;
	public final static float		DEFAULT_AXIS_TILT		= 23.44f;
	public final static float		MAX_AXIS_TILT			= 180f;

	public static final float		MIN_ECCENTRICITY		= 0f;
	public static final float		DEFAULT_ECCENTRICITY	= 0.0167f;
	public static final float		MAX_ECCENTRICITY		= 1.0f;

	public static final int			DEFAULT_START_LONGITUDE	= 0;
	public static final int			DEFAULT_STOP_LONGITUDE	= 360;

	public static final int			DEFAULT_START_LATITUDE	= 0;
	public static final int			DEFAULT_STOP_LATITUDE	= 180;

	public static final double		CIRCUMFERENCE			= 4.003014 * Math.pow(10, 7);
	public static final double		SURFACE_AREA			= 5.10072 * Math.pow(10, 14);

	// Orbital period of Earth in minutes
	public static final double		T						= 525974.4;

	// Length of the semi-major axis of earth IN METERS
	public static final double		a						= 1.496 * Math.pow(10, 11);

	// Argument of periapsis for the Earth:
	public static final double		omega					= 114;

	// planet around sun animation
	public static final double		animationDimenLim		= 150;
	public static final double		factor					= animationDimenLim / 2 * a;

	public static final Calendar	START_DATE				= Calendar.getInstance();

}