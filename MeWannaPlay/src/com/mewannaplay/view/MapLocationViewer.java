
package com.mewannaplay.view;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.net.MyCurrentLocation;
import com.net.MyCurrentLocation.LocationResult;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MapLocationViewer extends LinearLayout {

    private MapLocationOverlay overlay;
    private final Context context1;
    // Known latitude/longitude coordinates that we'll be using.
    private static List<MapLocation> mapLocations;

    private static MapView mapView;
    private double curLatitude;
    private double curLongitude;
    private GeoPoint curLocGeoPoint;
    public LocationResult locationResult = new LocationResult() {

        @Override
        public void gotLocation(final Location location) {
            // Got the location!
            Log.e("MeWannaPlay", "MyCurrentLocation  gotLocation ");
            curLatitude = location.getLatitude();
            curLongitude = location.getLongitude();
            Log.e("MeWannaPlay", "MyCurrentLocation  curLatitude " + curLatitude);

            Log.e("MeWannaPlay", "MyCurrentLocation  curLongitude " + curLongitude);

            curLocGeoPoint = new GeoPoint((int) (curLatitude * 1e6), (int) (curLongitude * 1e6));

            mapView.getController().setZoom(14);
            mapView.getController().setCenter(curLocGeoPoint);

            Toast.makeText(getContext(),
                    "The location is " + location.toString(), Toast.LENGTH_LONG)
                    .show();

        };
    };

    public MapLocationViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        context1 = context;
        init();

    }

    public MapLocationViewer(Context context) {
        super(context);
        context1 = context;
        init();

    }

    public void init() {

        setOrientation(VERTICAL);
        setLayoutParams(new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.FILL_PARENT));

        mapView = new MapView(getContext(), "06w4tpHqcK13uBZxgyNpnlQGkbmGdty7XouFrzA");
        mapView.setEnabled(true);
        mapView.setClickable(true);
        addView(mapView);

        overlay = new MapLocationOverlay(this, context1);
        mapView.getOverlays().add(overlay);

        // MyLocationOverlay myLocationOverlay = new MyLocationOverlay(context1,
        // mapView);
        // myLocationOverlay.enableMyLocation();

        // mapView.getOverlays().add(myLocationOverlay);

        getCurrentLocation();
        // curLocGeoPoint = new GeoPoint((int) (curLatitude * 1e6), (int)
        // (curLongitude * 1e6));
        //
        // mapView.getController().setZoom(14);
        // mapView.getController().setCenter(curLocGeoPoint);

    }

    private boolean getCurrentLocation() {
        boolean gotLocation = false;
        Log.e("MeWannaPlay", "MyCurrentLocation  getCurrentLocation 1 ");
        MyCurrentLocation myLocation = new MyCurrentLocation();
        gotLocation = myLocation.getLocation(getContext(), locationResult);
        if (gotLocation == false) {
            Log.e("MeWannaPlay", "MyCurrentLocation  getCurrentLocation 2 ");
            ShowErrorDialog("Error while getting current location");
        }
        Log.e("MeWannaPlay", "MyCurrentLocation  getCurrentLocation 3 ");
        return gotLocation;
    }

    private void ShowErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message)
                        .setCancelable(false)
                        .setNeutralButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                    }
                                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static List<MapLocation> getMapLocations() {
        if (mapLocations == null) {
            mapLocations = new ArrayList<MapLocation>();
            Log.e("MeWannaPlay", "==========================================================");

            // mapLocations
            // .add(new MapLocation("Tennis Club", 37.799800872802734,
            // -122.40699768066406));
            // mapLocations
            // .add(new MapLocation("Sports Hall", 37.792598724365234,
            // -122.40599822998047));
            // mapLocations.add(new MapLocation("Public Tennis Court",
            // 37.80910110473633,
            // -122.41600036621094));
            // mapLocations.add(new MapLocation("Royal Tennis Club",
            // 37.79410171508789,
            // -122.4010009765625));
        }
        Log.e("MeWannaPlay", "==========================================================");

        return mapLocations;
    }

    public static void addMapLocation(String tennis_id, String tennis_latitude,
            String tennis_longitude, String tennis_subcourts,
            String Occupied, String tennis_facility_type, String tennis_name,
            String message_count) {
        if (mapLocations == null) {
            mapLocations = new ArrayList<MapLocation>();
            // mapLocations.add(new MapLocation(tennis_id, tennis_latitude,
            // tennis_longitude,
            // tennis_subcourts, Occupied, tennis_facility_type, tennis_name,
            // message_count));

        } else {
            mapLocations.add(new MapLocation(tennis_id, tennis_latitude, tennis_longitude,
                    tennis_subcourts, Occupied, tennis_facility_type, tennis_name, message_count));
        }
    }

    public static MapView getMapView() {
        return mapView;
    }

}
