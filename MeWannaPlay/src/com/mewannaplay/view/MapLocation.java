
package com.mewannaplay.view;

import com.google.android.maps.GeoPoint;

import android.util.Log;

/** Class to hold our location information */
public class MapLocation {

    private final GeoPoint point;

    private final String tennis_id;
    private final String tennis_latitude;
    private final double latitude;
    private final double longitude;
    private final String tennis_longitude;
    private final Integer tennis_subcourts;
    private final Integer Occupied;
    private final String tennis_facility_type;
    private final String tennis_name;
    private final Integer message_count;

    public MapLocation(String tennis_id, String tennis_latitude,
            String tennis_longitude, String tennis_subcourts,
            String Occupied, String tennis_facility_type, String tennis_name,
            String message_count) {

        this.tennis_id = tennis_id;
        this.tennis_latitude = tennis_latitude;
        this.tennis_longitude = tennis_longitude;
        this.tennis_subcourts = Integer.parseInt(tennis_subcourts);
        this.Occupied = Integer.parseInt(Occupied);
        this.tennis_facility_type = tennis_facility_type;
        this.tennis_name = tennis_name;
        this.message_count = Integer.parseInt(message_count);

        latitude = convertStringToDouble(tennis_latitude);
        longitude = convertStringToDouble(tennis_longitude);

        Log.e("MeWannaPlay", "--MapLocation------------------" + latitude);
        Log.e("MeWannaPlay", "---MapLocation-----------------" + longitude);

        this.point = new GeoPoint((int) (latitude * 1e6), (int) (longitude * 1e6));
    }

    public GeoPoint getPoint() {
        return point;
    }

    public String getTennisCourtName() {
        return tennis_name;
    }

    public String getTennisId() {
        return tennis_id;
    }

    public Integer isOccupied() {
        return Occupied;
    }

    public Integer noOfSubCourts() {
        return tennis_subcourts;
    }

    private double convertStringToDouble(String str) {
        if (str == null)
            return 0.0;
        Float f = new Float(str);
        double d = f.doubleValue();
        return d;
    }

}
