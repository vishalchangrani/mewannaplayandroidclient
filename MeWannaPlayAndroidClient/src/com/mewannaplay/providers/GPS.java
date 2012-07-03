package com.mewannaplay.providers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;



public class GPS {
	
	public static final String GOOGLE_GEOCODER = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in
	// Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 60000*5; // in
	protected LocationManager locationManager;
	static double latitude, longitude;
	Context c;
	
//	public GPS(Context context) {
//		
//		// TODO Auto-generated constructor stub
//		c=context;
//	}
	
	
	
	public void gpsInitializer(Context context){
		
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATES,
				MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());	
    		}
	
	
	public double getLatitude(){
		
		return latitude;
		
	}
	
public double getLongitude(){
		
		return longitude;
		
	}
	
	
	public void showCurrentLocation() {

		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location != null) {
			latitude =  (location.getLatitude() );
			longitude =  (location.getLongitude() );
			Log.i("LOGLATshow", "" + (int) (location.getLatitude() ));
		}
		

	}

	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			latitude =  (location.getLatitude() );
			longitude =  (location.getLongitude() );
			Log.i("LOGLAT", "" + (int) (location.getLatitude()));

		}

		public void onStatusChanged(String s, int i, Bundle b) {

		}

		public void onProviderDisabled(String s) {

		}

		public void onProviderEnabled(String s) {

		}

	}
	
	
//	public static String getAddressFromGPSData(double lat, double longi) {
//		HttpRetriever agent = new HttpRetriever();
//		String request = GOOGLE_GEOCODER + lat + ","
//				+ longi + "&sensor=true";
//		// Log.d("GeoCoder", request);
//		String response = agent.retrieve(request);
//		String formattedAddress = "";
//		if (response != null) {
//			Log.d("GeoCoder", response);
//			try {
//				JSONObject parentObject = new JSONObject(response);
//				JSONArray arrayOfAddressResults = parentObject
//						.getJSONArray("results");
//				if(arrayOfAddressResults.length()==0)
//				{
//					formattedAddress="Address not available: lat="+lat+", lon:"+longi;
//				}
//				else
//				{
//					JSONObject addressItem = arrayOfAddressResults.getJSONObject(0);
//					formattedAddress = addressItem.getString("formatted_address");
//				}
//				
//			} catch (JSONException e) {
//
//				e.printStackTrace();
//			}
//
//		}
//
//		// Log.d("GeoCoder", response);
//		return formattedAddress;
//	}
//	
	
	
	

}
