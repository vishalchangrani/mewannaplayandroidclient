
package com.net;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MyCurrentLocation {

    Timer timer1;
    LocationManager lm;
    LocationResult locationResult;
    boolean gps_enabled = false;
    boolean network_enabled = false;

    public boolean getLocation(Context context, LocationResult result) {
        // I use LocationResult callback class to pass location value from
        // MyLocation to user code.
        Log.e("MeWannaPlay", "MyCurrentLocation  getLocation 1 ");
        locationResult = result;
        if (lm == null)

            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // exceptions will be thrown if provider is not permitted.
        try {

            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {

            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {

        }

        // don't start listeners if no provider is enabled
        Log.e("MeWannaPlay", "MyCurrentLocation  getLocation 2 ");
        if (!gps_enabled && !network_enabled)
            return false;

        Log.e("MeWannaPlay", "MyCurrentLocation  getLocation 3");

        if (gps_enabled) {
            Log.e("MeWannaPlay", "MyCurrentLocation  getLocation gps_enabled 1 ");
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        }

        if (network_enabled) {
            Log.e("MeWannaPlay", "MyCurrentLocation  getLocation network_enabled 1 ");
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    locationListenerNetwork);
        }
        timer1 = new Timer();
        timer1.schedule(new GetLastLocation(), 20000);
        return true;

    }

    LocationListener locationListenerGps = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.e("MeWannaPlay", "MyCurrentLocation  locationListenerGps 1");
            timer1.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);

        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    };

    LocationListener locationListenerNetwork = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.e("MeWannaPlay", "MyCurrentLocation  locationListenerNetwork 1");
            timer1.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            Log.e("MeWannaPlay", "MyCurrentLocation  GetLastLocation run 1");
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);
            Location net_loc = null, gps_loc = null;

            if (gps_enabled) {
                Log.e("MeWannaPlay", "MyCurrentLocation  GetLastLocation run gps_enabled 2");
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (network_enabled) {
                Log.e("MeWannaPlay", "MyCurrentLocation  GetLastLocation run network_enabled 1");
                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            // if there are both values use the latest one
            if (gps_loc != null && net_loc != null) {
                if (gps_loc.getTime() > net_loc.getTime())
                    locationResult.gotLocation(gps_loc);
                else
                    locationResult.gotLocation(net_loc);
                return;

            }

            if (gps_loc != null) {
                locationResult.gotLocation(gps_loc);
                return;
            }
            if (net_loc != null) {
                locationResult.gotLocation(net_loc);
                return;
            }
            Log.e("MeWannaPlay", "MyCurrentLocation  GetLastLocation run gps_enabled 4");

            locationResult.gotLocation(null);
        }
    }

    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }

}
