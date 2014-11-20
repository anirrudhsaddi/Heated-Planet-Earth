package EarthSim;

import EarthSim.widgets.QueryWidget;
import org.apache.james.mime4j.field.datetime.DateTime;

import javax.management.Query;

public class QueryEngine {

    QueryWidget q = new QueryWidget();

    //TODO: Create method to accept user input from QueryWidgets
    private void getQueryValues(String simName, float axialTilt, float eccentricity, DateTime startTime, DateTime endTime, double wLat, double eLat, double nLat, double sLat ) {

        this.simName = simName;
        this.axialTilt = axialTilt;
        this.eccentricity = eccentricity;
        this.startTime = startTime;
        this.endTime = endTime;
        this.wLat = wLat;
        this.eLat = eLat;
        this.sLat = sLat;
        this.nLat = nLat;

    }

    //TODO: validate input to make sure that are valid

}
